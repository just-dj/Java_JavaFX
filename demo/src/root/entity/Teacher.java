package root.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Teacher {
	//工号、姓名、性别、职称、研究方向、联系电话
	private SimpleStringProperty id = new SimpleStringProperty();
	
	private SimpleStringProperty password = new SimpleStringProperty();
	
	private SimpleStringProperty name = new SimpleStringProperty();
	
	private SimpleStringProperty sex  = new SimpleStringProperty();
	
	private SimpleStringProperty position  = new SimpleStringProperty() ;
	
	private SimpleStringProperty direction = new SimpleStringProperty();
	
	private SimpleStringProperty phone = new SimpleStringProperty();
	
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
	
	public String getPosition() {
		return position.get();
	}
	
	public SimpleStringProperty positionProperty() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position.set(position);
	}
	
	public String getDirection() {
		return direction.get();
	}
	
	public SimpleStringProperty directionProperty() {
		return direction;
	}
	
	public void setDirection(String direction) {
		this.direction.set(direction);
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
	
}
