package root.helper;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import root.util.CollectionUtil;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//数据库操作助手类
//这个简单库是根据《架构探险 从零开始写javaweb框架》写的 原本是用于服务器端的
public final class DatabaseHelper {
	
	//	dbutils包中的类
	private static final QueryRunner QUERY_RUNNER ;
	
	//	线程安全的数据库连接
	private static final ThreadLocal<Connection> CONNECTION_HOLDER ;
	
	//	数据库连接池
	private static final BasicDataSource DATA_SOURCE;
	
	static {
		QUERY_RUNNER = new QueryRunner();
		CONNECTION_HOLDER = new ThreadLocal<Connection>();
		

		String DRIVER = "com.mysql.jdbc.Driver";
		//这里指明编码格式并且不使用ssl，因为数据库在本地
		String URL = "jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		String USERNAME = "root";
		String PASSWORD = "521947";
		
		//创建数据库连接
		DATA_SOURCE = new BasicDataSource();
		DATA_SOURCE.setDriverClassName(DRIVER);
		DATA_SOURCE.setUrl(URL);
		DATA_SOURCE.setUsername(USERNAME);
		DATA_SOURCE.setPassword(PASSWORD);
		
		DATA_SOURCE.setInitialSize(5); // 初始的连接数；
		DATA_SOURCE.setMaxTotal(20);  //最大连接数
		DATA_SOURCE.setMaxIdle(10);  // 设置最大空闲连接
		DATA_SOURCE.setMaxWaitMillis(1000);  // 设置最大等待时间
		DATA_SOURCE.setMinIdle(5);  // 设置最小空闲连接
		getConnection();
	}
	
	//	获取数据库连接
	public static Connection getConnection(){
		Connection connection = CONNECTION_HOLDER.get();
		if (connection == null){
			try {
				connection = DATA_SOURCE.getConnection();
			}catch (SQLException e){
//				LOGGER.error("get connection failure",e);
				throw new RuntimeException(e);
			}finally {
				CONNECTION_HOLDER.set(connection);
			}
		}
		return connection;
	}
	
	//	查询单个实体
	public static<T> T queryEntity(Class<T> entityClass,String sql,Object... params){
		T entity;
		try{
			Connection connection = CONNECTION_HOLDER.get();
			entity = QUERY_RUNNER.query(connection,sql,new BeanHandler<T>(entityClass),params);
		}catch (SQLException e){
			//		LOGGER.error("query entity failure",e);
			throw new RuntimeException(e);
		}
		return entity;
	}

	//	查询实体列表 支持条件查询
	//	这个函数里用到了泛型，以及函数多参数
	public static<T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params){
		List<T> entityList;

	//		Page47 妙啊
		try{
			Connection connection = getConnection();
			entityList = QUERY_RUNNER.query(connection,sql,new BeanListHandler<T>(entityClass),params);
		} catch (SQLException e) {
			//		LOGGER.error("query entity list failure",e);
			throw new RuntimeException(e);
		}
		return entityList;
	}
	
	//	 插入实体
	public static<T> boolean insertEntity(Class<T> entityClass,Object entity){
		Map<String,Object> filedMap = beanToMap(entity);
		if (CollectionUtil.isEmpty(filedMap)){
//			LOGGER.error("can not insert entity:filedMap is empty");
			return false;
		}
		String sql = "insert into "+ getTableName(entityClass);
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");
		for (String filedName : filedMap.keySet()){
			columns.append(filedName).append(", ");
			values.append("?, ");
		}
		columns.replace(columns.lastIndexOf(", "),columns.length(),")");
		values.replace(values.lastIndexOf(", "),values.length(),")");
		sql += columns + "values " + values;
		Object[] params = filedMap.values().toArray();
		return  executeUpdate(sql,params) == 1;
	}
	
	//	更新实体
	public static<T> boolean updateEntity(Class<T> entityClass,String id,Object entity){
		Map<String,Object> filedMap = beanToMap(entity);
		if(CollectionUtil.isEmpty(filedMap)){
//			LOGGER.error("can not update entity :filedMap is empty");
			return false;
		}
		String sql = " update " + getTableName(entityClass) + " set " ;
		StringBuilder columns = new StringBuilder();
		for (String filedName : filedMap.keySet()){
			columns.append(filedName).append("=?, ");
		}
		sql += columns.substring(0,columns.lastIndexOf(", ")) + " where id = ?";
		List<Object> paramList = new ArrayList<Object>();
		//获取值
		paramList.addAll(filedMap.values());
		paramList.add(id);
		Object[] params = paramList.toArray();
		return executeUpdate(sql,params) == 1;
	}
	
	//	删除实体
	public static <T> boolean deleteEntity(Class<T> entityClass,String id){
		String sql  = "delete from " + getTableName(entityClass) + " WHERE ID = ?";
		return  executeUpdate(sql,id) == 1;
	}
	
	//	执行查询语句
	public static List<Map<String,Object>> executeQuery(String sql, Object... params){
		List<Map<String,Object>> result;
		try {
			Connection connection = CONNECTION_HOLDER.get();
			result =    QUERY_RUNNER.query(connection,sql,new MapListHandler(),params);
		}catch (SQLException e){
//		LOGGER.error("execute query failure",e);
			throw new RuntimeException(e);
		}
		return result;
	}
	
	//	执行更新语句 (包括 updata,delete)
	public static int executeUpdate(String sql,Object... params){
		int rows = 0;
		try {
			Connection connection = CONNECTION_HOLDER.get();
			rows = QUERY_RUNNER.update(connection,sql,params);
		}catch (SQLException e){
//		LOGGER.error("execute update failure",e);
			throw new RuntimeException(e);
		}
		return rows;
	}
	
	//	执行sql文件
	public static void executeSqlFile(String filepath){
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filepath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try{
			String sql;
			while ((sql=reader.readLine())!= null){
				executeUpdate(sql);
			}
		}catch (Exception e){
//			LOGGER.error("execute sql file failure",e);
			throw new RuntimeException(e);
		}
	}
	
	//将一个类转换为map
	public static Map<String, Object> beanToMap(Object obj) {
		Map<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (!"class".equals(name)) {
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}
	
	//获取实体类对应的表名
	private static String getTableName(Class<?> entityClass){
		return entityClass.getSimpleName();
	}
	
}
