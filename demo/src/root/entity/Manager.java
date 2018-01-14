package root.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Manager {
	
	private SimpleStringProperty id = new SimpleStringProperty();
	
	private SimpleStringProperty password = new SimpleStringProperty();
	
	private SimpleStringProperty name = new SimpleStringProperty();
	
	private SimpleIntegerProperty maxNum  = new SimpleIntegerProperty();
	
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
	
	public int getMaxNum() {
		return maxNum.get();
	}
	
	public SimpleIntegerProperty maxNumProperty() {
		return maxNum;
	}
	
	public void setMaxNum(int maxNum) {
		this.maxNum.set(maxNum);
	}
}
