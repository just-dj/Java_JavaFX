package root;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import root.entity.Manager;
import root.entity.Student;
import root.entity.Teacher;
import root.helper.DatabaseHelper;
import root.util.AlertUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


public class ManagerWindow {
	
	private Parent root;
	private Manager manager;
	
	private TabPane tabPane;
//学生Tab
	private Map<String,String> studentParas;
	private ComboBox<String> studentParams;
	private TextField studentKey;
	private Button studentSearch;
	private TableView studentTable;
	private ObservableList<Student> studentList;
	private Button studentDelBtn;
	private Button studentIstBtn;
	private Label selectStudentName;
	private Label student_teacherInfo;
	private List<TextField> studentTextFiledList;
	
//老师Tab
	private Map<String,String> teacherParas;
	private TableView teacherTable;
	private ComboBox<String> teacherParams;
	private TextField teacherKey;
	private Button teacherSearch;
	private ObservableList<Teacher> teacherList;
	private Button teacherDel;
	private Button teacherIst;
	private List<TextField> teacherTextFiled;
	private ListView<String> confirmStudentList;
	private ObservableList<String> confirmStudent;
	private ListView<String> notSureStudentList;
	private ObservableList<String> notSureStudent;
	
//	设置上限Tab
	private Label maxNumNow;
	private TextField newMaxNum;
	private Button saveMaxNum;
	
	
	public ManagerWindow(Manager manager) {
		this.manager = manager;
		Stage managerStage=new Stage();
		try{
			root = FXMLLoader.load(getClass().getResource("fxml/manager.fxml"));
		}catch (Exception e){
			e.printStackTrace();
		}
		//初始化控件
		initPara();
		//绑定事件
		bindEvent();
		
		Scene scene=new Scene(root);
		managerStage.setTitle("欢迎"+ manager.getName()+"管理员");
		managerStage.setScene(scene);
		managerStage.show();
	}
	
	private void initPara(){
	
		tabPane = (TabPane)root.lookup("#tabPane");
		
		studentTable = (TableView)root.lookup("#studentTable");
			studentList = FXCollections.observableArrayList();
			studentTable.setItems(studentList);
			
		teacherTable = (TableView)root.lookup("#teacherTable");
			teacherList = FXCollections.observableArrayList();
			teacherTable.setItems(teacherList);
		
		initStudentTab();
			studentParams = (ComboBox<String>)root.lookup("#student_para");
			studentParams.setItems(FXCollections.observableArrayList(studentParas.keySet()));
			studentParams.getSelectionModel().select(0);
			studentKey = (TextField)root.lookup("#student_key");
			studentSearch = (Button)root.lookup("#student_search");
			
			
		initTeacherTab();
			teacherParams = (ComboBox<String>)root.lookup("#teacher_para");
			teacherParams.setItems(FXCollections.observableArrayList(teacherParas.keySet()));
			teacherParams.getSelectionModel().select(0);
			teacherKey = (TextField)root.lookup("#teacher_key");
			teacherSearch = (Button)root.lookup("#teacher_search");
		
		studentDelBtn = (Button)root.lookup("#student_del");
		studentIstBtn = (Button)root.lookup("#student_insert");
		selectStudentName = (Label) root.lookup("#studentName");
		selectStudentName.setText("");
		student_teacherInfo = (Label)root.lookup("#student_teacherInfo");
		studentTextFiledList = new LinkedList <>();
		for (int i = 0;i < 12;++i)
			studentTextFiledList.add((TextField) root.lookup("#studentPara"+i));
		
		teacherDel = (Button)root.lookup("#teacher_del");
		teacherIst = (Button)root.lookup("#teacher_insert");
		confirmStudentList = (ListView)root.lookup("#confirmStudentList");
		confirmStudent = FXCollections.observableArrayList();
		confirmStudentList.setItems(confirmStudent);
		notSureStudentList = (ListView)root.lookup("#notSureStudentList");
		notSureStudent = FXCollections.observableArrayList();
		notSureStudentList.setItems(notSureStudent);
		teacherTextFiled = new LinkedList <>();
		for (int i = 0;i < 7;++i)
			teacherTextFiled.add((TextField) root.lookup("#teacherPara"+ i));
		
		maxNumNow = (Label)root.lookup("#limitValue");
		newMaxNum = (TextField)root.lookup("#newValue");
		saveMaxNum = (Button)root.lookup("#submitValue");
	}
	
	private void bindEvent(){
		
		//初始化选中学生管理页面
		tabPane.getSelectionModel().select(0);
		loadStudentData();
		
		tabPane.getSelectionModel().selectedIndexProperty().addListener(e->{
			int a = tabPane.getSelectionModel().getSelectedIndex();
			switch (a){
				//学生管理页面
				case 0:
					loadStudentData();
					break;
				//老师管理界面
				case 1:
					loadTeacherData();
					break;
				//其他界面
				case 2:
					loadMaxNum();
					break;
			}
			
		});
		//搜索学生按钮
		studentSearch.setOnAction(e->{
			//获取要检索的属性
			String para = studentParas.get(studentParams.getValue());
			//获取检索关键字
			String key = studentKey.getText();
			String sql = "select * from student where "+ para +" like '%"+key+"%'";
			//System.out.println(sql);
			studentList.clear();
			studentList.addAll(DatabaseHelper.queryEntityList(Student.class,sql));;
		});
		//收搜索老师按钮
		teacherSearch.setOnAction(e->{
			//获取要检索的属性
			String para = teacherParas.get(teacherParams.getValue());
			//获取检索关键字
			String key = teacherKey.getText();
			String sql = "select * from teacher where "+ para +" like '%"+key+"%'";
			//System.out.println(sql);
			teacherList.clear();
			teacherList.addAll(DatabaseHelper.queryEntityList(Teacher.class,sql));
		});
		//选中学生表某一行
		studentTable.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				//将学生对应的导师信息输出
				Student mid = (Student)studentTable.getSelectionModel().getSelectedItem();
				selectStudentName.setText(mid.getName()+"的导师为");
				if (mid.getTutorId()!=null &&!mid.getTutorId().equals("")){
					student_teacherInfo.setText(getTeacher(mid.getTutorId()).getName());
				}else {
					student_teacherInfo.setText("");
				}
				
			}
		});
		//选中老师表某一行
		teacherTable.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				//显示导师对应的学生信息
				Teacher mid = (Teacher)teacherTable.getSelectionModel().getSelectedItem();
				//刷新选定列表
				refreshConfirmedList(mid);
				//刷新待定列表
				refreshUnsureList(mid);
			}
		});
		//删除选中的学生
		studentDelBtn.setOnAction(e->{
			Student mid = (Student) studentTable.getSelectionModel().getSelectedItem();
			if (mid!=null){
				//判断是否删除成功
				if (DatabaseHelper.deleteEntity(Student.class,mid.getId())){
					studentList.remove(studentTable.getSelectionModel().getFocusedIndex());
				}else {
					AlertUtil.showAlert("ERROR","系统错误","数据删除失败，请稍后重试!");
				}
				;
			}
		});
		//插入学生
		studentIstBtn.setOnAction(e->{
			Student mid = getInsertStudent();
			if (checkStudent(mid)){
				DatabaseHelper.insertEntity(Student.class,mid);
				loadStudentData();
			}else {
				AlertUtil.showAlert("ERROR","","插入失败，请检查数据!");
			}
		});
		//删除选中老师
		teacherDel.setOnAction(e->{
			Teacher mid = (Teacher)teacherTable.getSelectionModel().getSelectedItem();
			if (mid!=null){
				//判断是否删除成功
				if (DatabaseHelper.deleteEntity(Teacher.class,mid.getId())){
					teacherList.remove(teacherTable.getSelectionModel().getFocusedIndex());
				}else {
					AlertUtil.showAlert("ERROR","系统错误","数据删除失败，请稍后重试!");
				}
				;
			}
		});
		//插入老师
		teacherIst.setOnAction(e->{
			Teacher mid = getInsertTeacher();
			if (checkTeacher(mid)){
				DatabaseHelper.insertEntity(Teacher.class,mid);
				loadTeacherData();
			}else {
				AlertUtil.showAlert("ERROR","","插入失败，请检查数据!");
			}
		});
		//修改上限
		saveMaxNum.setOnAction(e->{
			int num = -1;
			if (newMaxNum.getText() != null && !newMaxNum.getText().trim().equals("")){
				num = Integer.parseInt(newMaxNum.getText());
				if (num > 0){
					manager.setMaxNum(num);
					DatabaseHelper.updateEntity(Manager.class,manager.getId(),manager);
					loadMaxNum();
					AlertUtil.showAlert("Success","","数据修改成功!");
				}else {
					AlertUtil.showAlert("ERROR","","数据修改失败，请检查输入!");
				}
			}
		});
	}
	
	private void refreshConfirmedList(Teacher teacher){
		String sql = "select * from student where tutorId = ? and confirm = 1";
		List<Student> list = DatabaseHelper.queryEntityList(Student.class,sql,teacher.getId()+"");
		String nameList = "";
		confirmStudent.clear();
		for (Student student:list)
			confirmStudent.add(String.format(student.getName()+ "     "));
	}
	
	private void refreshUnsureList(Teacher teacher){
		String sql = "select * from student where tutorId = ? and confirm = 0";
		List<Student> list = DatabaseHelper.queryEntityList(Student.class,sql,teacher.getId()+"");
		notSureStudent.clear();
		for (Student student:list)
			notSureStudent.add(String.format(student.getName()+ "     "));
	}
	//对插入老师数据做简单检查
	private boolean checkTeacher(Teacher teacher){
		boolean mid = false;
		mid = teacher.getId().equals("");
		//检测工号是否重复
		if (!mid){
			if (getTeacher(teacher.getId()) != null){
				//AlertUtil.showAlert("ERROR","","学号重复,请检查输入!");
				return false;
			}
		}
		//这里指定所有数据不能为空
		mid = teacher.getName().equals("");
		mid = teacher.getSex().equals("");
		mid = teacher.getPosition().equals("");
		mid = teacher.getDirection().equals("");
		mid = teacher.getName().equals("");
		mid = teacher.getPassword().equals("");
		
		return !mid;
		
	}
	//对插入学生数据做简单检查
	private boolean checkStudent(Student student){
		boolean mid = false;
		mid = student.getId().equals("");
		if (!mid){
			if (getStudent(student.getId()) != null){
				//AlertUtil.showAlert("ERROR","","学号重复,请检查输入!");
				return false;
			}
			
		}
		if (student.getConfirm().equals("1")&&!student.getState().equals("选定")){
			
			return false;
		}
		
		mid = student.getPassword().equals("");
		mid = student.getName().equals("");
		mid = student.getSex().equals("");
		mid = student.getMajor().equals("");
		mid = student.getClassId().equals("");
		
		return !mid;
	}
	
	private Teacher getInsertTeacher(){
		Teacher mid = new Teacher();
		mid.setId(teacherTextFiled.get(0).getText() + "");
		mid.setPassword(teacherTextFiled.get(1).getText() + "");
		mid.setName(teacherTextFiled.get(2).getText() + "");
		mid.setSex(teacherTextFiled.get(3).getText() + "");
		mid.setPosition(teacherTextFiled.get(4).getText() + "");
		mid.setDirection(teacherTextFiled.get(5).getText() + "");
		mid.setPhone(teacherTextFiled.get(6).getText() + "");
		return mid;
	}
	
	private Student getInsertStudent(){
		Student mid = new Student();
		mid.setId(studentTextFiledList.get(0).getText()+"");
		mid.setPassword(studentTextFiledList.get(1).getText()+"");
		mid.setName(studentTextFiledList.get(2).getText()+"");
		mid.setSex(studentTextFiledList.get(3).getText()+"");
		mid.setMajor(studentTextFiledList.get(4).getText()+"");
		mid.setClassId(studentTextFiledList.get(5).getText()+"");
		mid.setPhone(studentTextFiledList.get(6).getText()+"");
		mid.setState(studentTextFiledList.get(7).getText()+"");
		mid.setTutorId(studentTextFiledList.get(8).getText()+"");
		mid.setConfirm(studentTextFiledList.get(9).getText()+"");
		return mid;
	}
	//获取对应id的老师
	private Teacher getTeacher(String id){
		String sql = "select * from teacher where id = ?";
		Teacher mid = DatabaseHelper.queryEntity(Teacher.class,sql,id);
		return mid;
	}
	//获取对应id的学生
	private Student getStudent(String id){
		String sql = "select * from student where id = ?";
		Student mid = DatabaseHelper.queryEntity(Student.class,sql,id);
		return mid;
	}
	
	private  void initStudentTab(){
			studentParas = new HashMap <>();//存放中文属性和类属性的映射
		List<TableColumn> studentColumnList = new LinkedList <>();
		TableColumn id = new TableColumn("学号");
			studentParas.put("学号","id");
			studentColumnList.add(id);
		TableColumn password = new TableColumn("密码");
			studentParas.put("密码","password");
			studentColumnList.add(password);
		TableColumn name = new TableColumn("姓名");
			studentParas.put("姓名","name");
			studentColumnList.add(name);
		TableColumn sex = new TableColumn("性别");
			studentParas.put("性别","sex");
			studentColumnList.add(sex);
		TableColumn major = new TableColumn("专业");
			studentParas.put("专业","major");
			studentColumnList.add(major);
		TableColumn classId = new TableColumn("班级");
			studentParas.put("班级","classId");
			studentColumnList.add(classId);
		TableColumn phone = new TableColumn("联系电话");
			studentParas.put("联系电话","phone");
			studentColumnList.add(phone);
		TableColumn state = new TableColumn("当前状态");
			studentParas.put("当前状态","state");
			studentColumnList.add(state);
		TableColumn tutorId = new TableColumn("导师ID");
			studentParas.put("导师ID","tutorId");
			studentColumnList.add(tutorId);
		TableColumn confirm = new TableColumn("是否选定");
			studentParas.put("是否选定","confirm");
			studentColumnList.add(confirm);
		String[] studentPara = new String[]{"id","password","name","sex","major","classId","phone","state","tutorId",
				"confirm"};
			//讲列设置为可编辑
			for (int i = 0;i < studentColumnList.size(); ++i){
				//先绑定
					studentColumnList.get(i).setCellValueFactory(new PropertyValueFactory<Student,String>(studentPara[i]));
					studentColumnList.get(i).setMinWidth(120);
					studentColumnList.get(i).setEditable(true);
					studentColumnList.get(i).setCellFactory(TextFieldTableCell.<Student>forTableColumn());
			}
		//每个单元格的编辑事件
		id.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setId(event.getNewValue().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		password.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setPassword(event.getNewValue().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		
		name.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setName(event.getNewValue().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		
		sex.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setSex(event.getNewValue().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		major.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setMajor(event.getNewValue()
						.toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		classId.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setClassId(event.getNewValue()
						.toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		phone.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setPhone(event.getNewValue
						().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		state.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setState(event.getNewValue
						().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		tutorId.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setTutorId(event
						.getNewValue().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
		confirm.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Student) studentTable.getSelectionModel().getSelectedItem()).setConfirm(event
						.getNewValue().toString());
				updateStudent((Student) studentTable.getSelectionModel().getSelectedItem());
			}
		});
			
		studentTable.getColumns().addAll(studentColumnList);
		
	}
	//更新学生数据
	private void updateStudent(Student student){
		DatabaseHelper.updateEntity(Student.class,student.getId(),student);
	}
	
	private void initTeacherTab(){
		teacherParas  = new HashMap <>();//存放中文属性和类属性的映射
		List<TableColumn> teacherColumnList = new LinkedList <>();
		TableColumn id = new TableColumn("工号");
			teacherParas.put("工号","id");
			teacherColumnList.add(id);
		TableColumn password = new TableColumn("密码");
			teacherParas.put("密码","password");
			teacherColumnList.add(password);
		TableColumn name = new TableColumn("姓名");
			teacherParas.put("姓名","name");
			teacherColumnList.add(name);
		TableColumn sex = new TableColumn("性别");
			teacherParas.put("性别","sex");
			teacherColumnList.add(sex);
		TableColumn position = new TableColumn("职称");
			teacherParas.put("职称","position");
			teacherColumnList.add(position);
		TableColumn direction = new TableColumn("研究方向");
			teacherParas.put("研究方向","direction");
			teacherColumnList.add(direction);
		TableColumn phone = new TableColumn("联系电话");
			teacherParas.put("联系电话","phone");
			teacherColumnList.add(phone);
		
		String[] teacherPara = new String[]{"id","password","name","sex","position","direction","phone"};
		//设置对应列可编辑
		for (int i = 0;i< teacherColumnList.size();++i){
			teacherColumnList.get(i).setCellValueFactory(new PropertyValueFactory<Teacher,String>(teacherPara[i]));
			teacherColumnList.get(i).setMinWidth(120);
			teacherColumnList.get(i).setEditable(true);
			teacherColumnList.get(i).setCellFactory(TextFieldTableCell.<Teacher>forTableColumn());
		}
		//每个单元格的编辑事件
		id.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Teacher) studentTable.getSelectionModel().getSelectedItem()).setId(event.getNewValue().toString());
				updateTeacher((Teacher) teacherTable.getSelectionModel().getSelectedItem());
			}
		});
		
		password.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Teacher) studentTable.getSelectionModel().getSelectedItem()).setPassword(event.getNewValue().toString());
				updateTeacher((Teacher) teacherTable.getSelectionModel().getSelectedItem());
			}
		});
		name.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Teacher) studentTable.getSelectionModel().getSelectedItem()).setName(event.getNewValue().toString());
				updateTeacher((Teacher) teacherTable.getSelectionModel().getSelectedItem());
			}
		});
		sex.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Teacher) studentTable.getSelectionModel().getSelectedItem()).setSex(event.getNewValue().toString());
				updateTeacher((Teacher) teacherTable.getSelectionModel().getSelectedItem());
			}
		});
		
		position.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Teacher) studentTable.getSelectionModel().getSelectedItem()).setPosition(event.getNewValue().toString());
				updateTeacher((Teacher) teacherTable.getSelectionModel().getSelectedItem());
			}
		});
		direction.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Teacher) studentTable.getSelectionModel().getSelectedItem()).setDirection(event.getNewValue().toString());
				updateTeacher((Teacher) teacherTable.getSelectionModel().getSelectedItem());
			}
		});
		phone.setOnEditCommit(new EventHandler <TableColumn.CellEditEvent>() {
			@Override
			public void handle(TableColumn.CellEditEvent event) {
				((Teacher) studentTable.getSelectionModel().getSelectedItem()).setPhone(event.getNewValue().toString());
				updateTeacher((Teacher) teacherTable.getSelectionModel().getSelectedItem());
			}
		});
		
		teacherTable.getColumns().addAll(id,password,name,sex,position,direction,phone);
	}
	//更新老师数据
	private void updateTeacher(Teacher teacher){
		DatabaseHelper.updateEntity(Teacher.class,teacher.getId(),teacher);
	}
	//获取学生信息并渲染
	private void loadStudentData(){
		String sql = "select * from student";
		studentList.clear();
		studentList.addAll(DatabaseHelper.queryEntityList(Student.class,sql));
		while (studentList.isEmpty());
	}
	//获取上限
	private void loadMaxNum(){
		String sql = "select * from manager where id = ?";
		this.manager = DatabaseHelper.queryEntity(Manager.class,sql,manager.getId());
		maxNumNow.setText(manager.getMaxNum()+"");
	}
	//获取教师信息并渲染
	private void loadTeacherData(){
		String sql = "select * from teacher";
		teacherList.clear();
		teacherList.addAll(DatabaseHelper.queryEntityList(Teacher.class,sql));
	}
	
	
}
