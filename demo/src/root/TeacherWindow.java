package root;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import root.entity.Manager;
import root.entity.Student;
import root.entity.Teacher;
import root.helper.DatabaseHelper;
import root.util.AlertUtil;

import java.util.List;

//教师窗口
public class TeacherWindow {
	
	private Parent root;
	private static  Teacher teacher;
	
	private static  Student student = null;
	
	private Button refresh;
	private Button changePassword;
		private ChangePasswordWindow<Teacher> changePass = null;
	//待定表
	private TableView unsureTable;
		private ObservableList<Student> unsureList;
		private TableColumn id;
		private TableColumn name;
		private TableColumn sex;
		private TableColumn major;
		private TableColumn classId;
		private TableColumn phone;
	//选定表
	private TableView confirmTable;
		private ObservableList<Student> confirmList;
		private TableColumn idF;
		private TableColumn nameF;
		private TableColumn sexF;
		private TableColumn majorF;
		private TableColumn classIdF;
		private TableColumn phoneF;
	
	private Button confirm;
	private Button out;
	
	public TeacherWindow(Teacher teacher) {
		this.teacher = teacher;
		
		Stage managerStage=new Stage();
		try{
			root = FXMLLoader.load(getClass().getResource("fxml/teacher.fxml"));
		}catch (Exception e){
			e.printStackTrace();
		}
		
		initPara();
		
		bindEvent();
		
		Scene scene=new Scene(root);
		managerStage.setTitle("欢迎"+ teacher.getName()+"老师");
		managerStage.setScene(scene);
		managerStage.show();
	}
	
	
	private  void initPara(){
		
		changePassword = (Button)root.lookup("#changePassword") ;
		
		unsureTable = (TableView)root.lookup("#unsureTable");
		confirmTable = (TableView)root.lookup("#confirmTable");
		
		
		id = new TableColumn("学号");
		name = new TableColumn("姓名");
		sex = new TableColumn("性别");
		major = new TableColumn("专业");
		classId = new TableColumn("班级");
		phone = new TableColumn("联系电话");
		
		idF = new TableColumn("学号");
		nameF = new TableColumn("姓名");
		sexF = new TableColumn("性别");
		majorF = new TableColumn("专业");
		classIdF = new TableColumn("班级");
		phoneF = new TableColumn("联系电话");
		//待定表
		id.setCellValueFactory(new PropertyValueFactory<Student,String>("id"));
			id.setMinWidth(140);
		name.setCellValueFactory(new PropertyValueFactory<Student,String>("name"));
			name.setMinWidth(80);
		sex.setCellValueFactory(new PropertyValueFactory<Student,String>("sex"));
			sex.setMinWidth(50);
			sex.setMaxWidth(50);
		major.setCellValueFactory(new PropertyValueFactory<Student,String>("major"));
			major.setMinWidth(100);
		classId.setCellValueFactory(new PropertyValueFactory<Student,String>("classId"));
			classId.setMinWidth(100);
		phone.setCellValueFactory(new PropertyValueFactory<Student,String>("phone"));
			phone.setMinWidth(120);
		//选定表
		idF.setCellValueFactory(new PropertyValueFactory<Student,String>("id"));
			idF.setMinWidth(140);
		nameF.setCellValueFactory(new PropertyValueFactory<Student,String>("name"));
			nameF.setMinWidth(80);
		sexF.setCellValueFactory(new PropertyValueFactory<Student,String>("sex"));
			sexF.setMinWidth(50);
			sexF.setMaxWidth(50);
		majorF.setCellValueFactory(new PropertyValueFactory<Student,String>("major"));
			majorF.setMinWidth(100);
		classIdF.setCellValueFactory(new PropertyValueFactory<Student,String>("classId"));
			classIdF.setMinWidth(100);
		phoneF.setCellValueFactory(new PropertyValueFactory<Student,String>("phone"));
			phoneF.setMinWidth(120);
		
		unsureTable.getColumns().addAll(id,name,sex,major,classId,phone);
			unsureList = FXCollections.observableArrayList();
			unsureTable.setItems(unsureList);
			getUnsureStudentList();
		confirmTable.getColumns().addAll(idF,nameF,sexF,majorF,classIdF,phoneF);
			confirmList = FXCollections.observableArrayList();
			confirmTable.setItems(confirmList);
			getConfirmStudentList();
		
		refresh = (Button)root.lookup("#refresh");
		confirm = (Button)root.lookup("#confirm");
		out = (Button)root.lookup("#out");
		
	}
	
	private void bindEvent(){
		
		changePassword.setOnAction(e->{
			if (changePass == null)
				changePass = new ChangePasswordWindow(Teacher.class,teacher.getId(),teacher);
			else {
				changePass.changePassStage.show();
			}
		});
		
		unsureTable.getSelectionModel().selectedItemProperty().addListener(observable->{
			student =(Student) unsureTable.getSelectionModel().getSelectedItem();
		});
		//刷新数据
		refresh.setOnAction(e->{
			getUnsureStudentList();
			getConfirmStudentList();
		});
		//选定学生
		confirm.setOnAction(e->{
			if (null != student){
				if (confirmList.size() < getMaxNum()){
					student.setConfirm(1+"");
					student.setState("选定");
					if (DatabaseHelper.updateEntity(Student.class,student.getId(),student)){
						//刷新数据
						getUnsureStudentList();
						getConfirmStudentList();
					}else {
						AlertUtil.showAlert("ERROR","系统错误！","确定失败，请稍后重试！");
					}
				}else {
					AlertUtil.showAlert("ERROR","","学生数量已满！");
				}
			}
		});
		//淘汰学生
		out.setOnAction(e->{
			if (null != student){
				student.setConfirm(0+"");
				student.setState("未选");
				student.setTutorId("");
				if (DatabaseHelper.updateEntity(Student.class,student.getId(),student)){
					//刷新数据
					getUnsureStudentList();
					getConfirmStudentList();
				}else {
					AlertUtil.showAlert("ERROR","系统错误！","确定失败，请稍后重试！");
				}
			}
		});
		
	}
	//获取能带的学生上限
	private int getMaxNum(){
		Manager manager = DatabaseHelper.queryEntityList(Manager.class,"select * from manager").get(0);
		return manager.getMaxNum();
	}
	//获取待定学生列表
	private void getUnsureStudentList(){
		String sql = "select * from student where tutorId  = ? and confirm = 0";
		List<Student>list = DatabaseHelper.queryEntityList(Student.class,sql,teacher.getId());
		unsureList.clear();
		unsureList.addAll(list);
	}
	//获取选定学生列表
	private void getConfirmStudentList(){
		String sql = "select * from student where tutorId  = ? and confirm = 1";
		List<Student>list = DatabaseHelper.queryEntityList(Student.class,sql,teacher.getId());
		confirmList.clear();
		confirmList.addAll(list);
	}
}
