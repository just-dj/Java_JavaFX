package root.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Student {
	
	//学号、姓名、性别、专业、班级、联系电话

	private SimpleStringProperty id = new SimpleStringProperty();
	
	private SimpleStringProperty password = new SimpleStringProperty();
	
	private SimpleStringProperty name = new SimpleStringProperty();
	
	private SimpleStringProperty sex = new SimpleStringProperty();
	
	private SimpleStringProperty major = new SimpleStringProperty();
	//应该需要另外一张班级表 这里假设班级id就是班级名
	private SimpleStringProperty classId = new SimpleStringProperty();
	
	private SimpleStringProperty phone = new SimpleStringProperty();
	
	private SimpleStringProperty state = new SimpleStringProperty();
	
	private SimpleStringProperty tutorId = new SimpleStringProperty();
	
	private SimpleStringProperty confirm = new SimpleStringProperty();
	
	
	public String getId() {
		return id.get();
	}
	
	public SimpleStringProperty idProperty() {
		return id;
	}
	
	public void setId(String id) {
		this.id.set(id);
	}
	
	public String getPassword() {
		return password.get();
	}
	
	public SimpleStringProperty passwordProperty() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password.set(password);
	}
	
	public String getName() {
		return name.get();
	}
	
	public SimpleStringProperty nameProperty() {
		return name;
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public String getSex() {
		return sex.get();
	}
	
	public SimpleStringProperty sexProperty() {
		return sex;
	}
	
	public void setSex(String sex) {
		this.sex.set(sex);
	}
	
	public String getMajor() {
		return major.get();
	}
	
	public SimpleStringProperty majorProperty() {
		return major;
	}
	
	public void setMajor(String major) {
		this.major.set(major);
	}
	
	public String getClassId() {
		return classId.get();
	}
	
	public SimpleStringProperty classIdProperty() {
		return classId;
	}
	
	public void setClassId(String classId) {
		this.classId.set(classId);
	}
	
	public String getPhone() {
		return phone.get();
	}
	
	public SimpleStringProperty phoneProperty() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone.set(phone);
	}
	
	public String getState() {
		return state.get();
	}
	
	public SimpleStringProperty stateProperty() {
		return state;
	}
	
	public void setState(String state) {
		this.state.set(state);
	}
	
	public String getTutorId() {
		return tutorId.get();
	}
	
	public SimpleStringProperty tutorIdProperty() {
		return tutorId;
	}
	
	public void setTutorId(String tutorId) {
		this.tutorId.set(tutorId);
	}
	
	public String getConfirm() {
		return confirm.get();
	}
	
	public SimpleStringProperty confirmProperty() {
		return confirm;
	}
	
	public void setConfirm(String confirm) {
		this.confirm.set(confirm);
	}
}
