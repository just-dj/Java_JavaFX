package root;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import root.helper.DatabaseHelper;
import root.util.AlertUtil;

//修改密码弹出窗口
public class ChangePasswordWindow<T> {
	
	//舞台属性是静态的 这样确保窗口的一性 不浪费资源
	public static Stage changePassStage = null;
	public static Parent root;
	
	private static TextField newPassword;
		private static SimpleStringProperty newP;
	private static Button save;
	
	
	private  Class<T> entityClass;
	private String id;
	private Object entity;
	
	//为了实现类的复用 要求传入一个class对象
	public  ChangePasswordWindow(Class<T> entityClass,String id,Object entity){
		this.entityClass = entityClass;
		this.id = id;
		this.entity = entity;
		
		System.out.println(entityClass.getSimpleName());
		
		changePassStage = new Stage();
		//这里窗口保持在最前方
		changePassStage.setAlwaysOnTop(true);
		try{
			root = FXMLLoader.load(getClass().getResource("fxml/changePassword.fxml"));
		}catch (Exception e){
			e.printStackTrace();
		}
		//初始化参数
		initPara();
		//事件绑定
		bindEvent();
		
		Scene scene=new Scene(root);
		changePassStage.setTitle("更改密码");
		changePassStage.setScene(scene);
		changePassStage.show();
	}
	
	private void initPara(){
		newP = new SimpleStringProperty("");
		
		newPassword = (TextField)root.lookup("#newPassword");
		
		save = (Button)root.lookup("#save");
	}
	
	private void bindEvent(){
		newP.bind(newPassword.textProperty());
		
		save.setOnAction(e->{
			//是否为空检测
			if (newP != null && !newP.getValue().equals("")){
				//长度检测
				if (newP.getValue().length() < 6){
					AlertUtil.showAlert("ERROR","输入格式不正确！","密码最少为六位！");
				}else {
					String sql = "update " + entityClass.getSimpleName() + " set password = ? where id = ?";
					if (DatabaseHelper.executeUpdate(sql,newP.getValue(),id) > 0){
						//点击动作之后关闭AlwaysOnTop
						changePassStage.setAlwaysOnTop(false);
						AlertUtil.showAlert("SUCCESS","成功！","密码修改成功！");
						changePassStage.hide();
					}else {
						AlertUtil.showAlert("ERROR","失败！","密码更改失败，请稍后重试！");
						changePassStage.hide();
					}
					
				}
			}else {
				AlertUtil.showAlert("ERROR","输入为空！","请检查输入！");
			}
			
		});
	}
	

	
}
