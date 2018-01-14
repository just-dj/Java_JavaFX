package root;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import root.entity.Manager;
import root.entity.Student;
import root.entity.Teacher;
import root.helper.DatabaseHelper;
import root.util.AlertUtil;

import java.util.LinkedList;
import java.util.List;

public class StudentWindow {
	
	private Parent root;
	private static  Student student;
	private static Teacher teacher;
	private ChangePasswordWindow<Student> changePass;
	
	
	private Pagination tables;
	
	private TableView tableView;
	private TableColumn id;
	private TableColumn name;
	private TableColumn sex;
	private TableColumn position;
	private TableColumn direction;
	private TableColumn phone;
	
	
	private Label state;
	private Button refresh;
	private Button changePassword;
	private HBox chooseContainer;
	private Label chooseTeacherName;
	
	
	private VBox teacherInfo;
	private Label teacherInfoHeader;
	private Label maxNum;
	private Label allNum;
	private Label confirmedHeader;
	private ListView confirmed;
	private Label notSureHeader;
	private ListView notSure;
	private ObservableList<String> confirmedList;
	private ObservableList<String> notSureList;
	
	private Button choose;
	private Button unChoose;
	
	ObservableList<Teacher> teacherList ;
	
	public StudentWindow(Student student) {
		
		this.student = student;
		Stage managerStage=new Stage();
		try{
			root = FXMLLoader.load(getClass().getResource("fxml/student.fxml"));
		}catch (Exception e){
			e.printStackTrace();
		}
		//初始化数据
		initPara();
		//绑定事件
		bindEvent();
		
		Scene scene=new Scene(root);
		managerStage.setTitle("欢迎"+ student.getName()+"同学");
		managerStage.setScene(scene);
		managerStage.show();
		
	}
	
	private void initPara(){
		teacherList = FXCollections.observableArrayList();
		
		tables = (Pagination)root.lookup("#tables");
			initPagination();
		tableView = (TableView)root.lookup("#table");
		
		id = new TableColumn("工号");
		name = new TableColumn("姓名");
		sex = new TableColumn("性别");
		position = new TableColumn("职称");
		direction = new TableColumn("研究方向");
		phone = new TableColumn("联系电话");
		
		id.setCellValueFactory(new PropertyValueFactory<Teacher,String>("id"));
			id.setMinWidth(120);
		name.setCellValueFactory(new PropertyValueFactory<Teacher,String>("name"));
			name.setMinWidth(120);
		sex.setCellValueFactory(new PropertyValueFactory<Teacher,String>("sex"));
			sex.setMinWidth(120);
		position.setCellValueFactory(new PropertyValueFactory<Teacher,String>("position"));
			position.setMinWidth(120);
		direction.setCellValueFactory(new PropertyValueFactory<Teacher,String>("direction"));
			direction.setMinWidth(120);
		phone.setCellValueFactory(new PropertyValueFactory<Teacher,String>("phone"));
			phone.setMinWidth(120);
			
		tableView.getColumns().addAll(id,name,sex,position,direction,phone);
		
		
		state = (Label)root.lookup("#state");
		refresh = (Button)root.lookup("#refresh");
		changePassword = (Button)root.lookup("#changePassword");
		chooseContainer = (HBox)root.lookup("#chooseContainer") ;
			chooseTeacherName = (Label)root.lookup("#chooseTeacherName");
			if (!student.getState().equals("未选")){
				chooseContainer.setVisible(true);
				chooseTeacherName.setText(getTeacher(student.getTutorId()).getName());
			}
		
		
		teacherInfo = (VBox)root.lookup("#teacher_info");
		teacherInfoHeader = (Label)root.lookup("#teacher_info_header");
		maxNum  =(Label)root.lookup("#maxNum");
		allNum = (Label)root.lookup("#allNum");
		confirmedHeader = (Label)root.lookup("#confirmedHeader");
		confirmed = (ListView) root.lookup("#confirmed");
		notSureHeader = (Label)root.lookup("#notSureHeader");
		notSure = (ListView) root.lookup("#notSure");
		choose = (Button)root.lookup("#choose");
		unChoose = (Button)root.lookup("#unChoose");
		
		
		confirmedList = FXCollections.observableArrayList();
		notSureList = FXCollections.observableArrayList();
		confirmed.setItems(confirmedList);
		notSure.setItems(notSureList);
		
	}
	
	private void bindEvent(){
		//将学生状态标签设置为当前学生状态
		state.setText(student.getState());
		
		tableView.getSelectionModel().selectedItemProperty().addListener((Observable observable) -> {
			getTeacherInfo();
		});
		//点击选择该导师
		choose.setOnAction(e->{
			String sql = "select * from student where id = ?";
			Student mid = DatabaseHelper.queryEntity(Student.class,sql,student.getId());
			if (null == mid)
				return;
			//判断是否满额
			if (confirmedList.size() >= getMaxNum()){
				AlertUtil.showAlert("ERROR","","该导师选定的学生数量达到上限");
			}else {
				//判断学生不能在选定状态
				if (!mid.getState().equals("选定")){
					chooseTeacher();
					state.setText(student.getState());
					chooseContainer.setVisible(true);
					chooseTeacherName.setText(getTeacher(student.getTutorId()).getName());
				}else {
					AlertUtil.showAlert("ERROR","已选定！","你已选定导师,不能再次选择！");
					getTeacherInfo();
				}
			}
		});
		
		//推选该导师
		unChoose.setOnAction(e->{
			String sql = "select * from student where id = ?";
			Student mid = DatabaseHelper.queryEntity(Student.class,sql,student.getId());
			//判断取消按钮是否有效 就是当前table row对应的的导师是不是你待定的导师
			if (teacher.getId().equals(student.getTutorId())){
				if (null == mid)
					return;
				//判断学生不能在选定状态
				if (!mid.getConfirm().equals("1")){
					unChooseTeacher();
					state.setText(student.getState());
					chooseContainer.setVisible(false);
				}else {
					AlertUtil.showAlert("ERROR","已选定！","你已选定导师,不能退选！");
				}
			}
			
		});
		
		//刷新当前页面数据
		refresh.setOnAction(e->{
			getTeacherInfo();
		});
		//弹出修改密码按钮
		changePassword.setOnAction(e->{
			if (changePass == null)
				changePass = new ChangePasswordWindow(Student.class,student.getId(),student);
			else {
				changePass.changePassStage.show();
			}
		});
		
	}
	
	//初始化分页表
	private void initPagination() {
		List<Teacher> mid = DatabaseHelper.queryEntityList(Teacher.class,"select * from teacher");
		//获取页数
		tables.setPageCount((mid.size()+19)/20);
		//默认选中第一页
		tables.setCurrentPageIndex(0);
		//页面翻动事件
		tables.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer param) {
				
				//teacherInfo.setVisible(false);
				getStudentList(param);
				return tableView;
			}
		});
		
	}
	//获得第para页的数据
	private void getStudentList(int para){
		//一页二十行数据
		String sql = "select * from teacher limit " + (para*20)+",20";
		teacherList.clear();
		teacherList.addAll(FXCollections.observableArrayList(DatabaseHelper.queryEntityList(Teacher.class,sql)));
		
		tableView.setItems(teacherList);
	}
	//通过id获取老师
	private Teacher getTeacher(String id){
		String sql = "select * from teacher where id = ?";
		Teacher mid = DatabaseHelper.queryEntity(Teacher.class,sql,id);
		return mid;
	}
	//通过id获取学生
	private Student getStudent(String id){
		String sql = "select * from student where id = ?";
		Student mid = DatabaseHelper.queryEntity(Student.class,sql,id);
		return mid;
	}
	
	//修改学生选定的导师名称 和 学生状态
	private void chooseTeacher(){
		student.setState("待定");
		student.setTutorId(teacher.getId());
		DatabaseHelper.updateEntity(Student.class,student.getId(),student);
		getTeacherInfo();
		student = getStudent(student.getId());
	}
	
	//修改学生选定的导师名称 和 学生状态
	private void unChooseTeacher(){
		student.setState("未选");
		student.setTutorId("");
		DatabaseHelper.updateEntity(Student.class,student.getId(),student);
		getTeacherInfo();
		student = getStudent(student.getId());
	}
	
	//将老师信息显示在左侧面板
	private void getTeacherInfo(){
		student = getStudent(student.getId());
		if (!student.getState().equals("未选")){
			chooseContainer.setVisible(true);
			chooseTeacherName.setText(getTeacher(student.getTutorId()).getName());
		}
		state.setText(student.getState());
		teacherInfo.setVisible(true);
		
		String sql;
		teacher = (Teacher)tableView.getSelectionModel().getSelectedItem();
		Teacher mid = null;
		List<Student> list = new LinkedList <>();
		
		if (teacher == null)
			return;
		//刷新右侧当前选中老师信息面板
		teacherInfoHeader.setText("导师"+teacher.getName()+"的被选情况如下");
		
		maxNum.setText(getMaxNum()+"");
		//下面三句重新渲染数据
		refreshAllNum();
		
		refreshConfirmedList();
		
		refreshUnsureList();
		
	}
	//获取老师能带学生数量的上限制
	private int getMaxNum(){
		Manager manager = DatabaseHelper.queryEntityList(Manager.class,"select * from manager").get(0);
		return manager.getMaxNum();
	}
	//刷新对应组件数据
	private void refreshAllNum(){
		String sql = "select * from student where tutorId = ?";
		List<Student> list = DatabaseHelper.queryEntityList(Student.class,sql,teacher.getId()+"");
		allNum.setText(list.size()+"");
	}
	
	private void refreshConfirmedList(){
		String sql = "select * from student where tutorId = ? and confirm = '1'";
		List<Student> list = DatabaseHelper.queryEntityList(Student.class,sql,teacher.getId()+"");
		String nameList = "";
		confirmedList.clear();
		for (Student student:list)
			confirmedList.add(String.format(student.getName()+ "     "));
	}
	
	private void refreshUnsureList(){
		String sql = "select * from student where tutorId = ? and confirm = '0'";
		List<Student> list = DatabaseHelper.queryEntityList(Student.class,sql,teacher.getId()+"");
		notSureList.clear();
		for (Student student:list){
			notSureList.add(String.format(student.getName()+ "     "));
		}
		
	}
}
