package root;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import root.entity.Manager;
import root.entity.Student;
import root.entity.Teacher;
import root.helper.DatabaseHelper;
import root.util.AlertUtil;


//负责展示登录窗口
public class LoginWindow extends Application {
    
    private Stage loginStage;
    private Parent root;
    
    private TextField account;
        SimpleStringProperty accountText;
    private PasswordField password;
        SimpleStringProperty passwordText;
    
    private ToggleGroup group;
    private RadioButton student;
    private RadioButton teacher;
    private RadioButton manager;
    
    private int kind = 1;
    
    private Label login;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        loginStage = primaryStage;
        root = FXMLLoader.load(getClass().getResource("fxml/login.fxml"));
        //初始化参数
        initPara();
        //绑定事件
        bindEvent();
        
        primaryStage.setTitle("登录");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initPara(){
        //账号密码输入框
        account = (TextField)root.lookup("#userAccount");
            accountText = new SimpleStringProperty("");
            accountText.bind(account.textProperty());
        password = (PasswordField)root.lookup("#password");
            passwordText = new SimpleStringProperty("");
            passwordText.bind(password.textProperty());
        //三种登录类型
        group = new ToggleGroup();
        student = (RadioButton)root.lookup("#student");
            student.setToggleGroup(group);
        teacher = (RadioButton)root.lookup("#teacher");
            teacher.setToggleGroup(group);
        manager = (RadioButton)root.lookup("#manager");
            manager.setToggleGroup(group);
        student.setSelected(true);
        
        login = (Label)root.lookup("#login");
    }
    
    private void bindEvent(){
        //将单选按钮绑定为对应的页面
        student.setOnAction(e-> kind = 1);
        teacher.setOnAction(e-> kind = 2);
        manager.setOnAction(e-> kind = 3);
        //点击登陆按钮
        login.setOnMouseClicked((javafx.scene.input.MouseEvent event) ->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (accountText.getValue().trim().equals("")){
                AlertUtil.showAlert("ERROR","请检查输入","账号不能为空");
                return;
            }else if (passwordText.getValue().trim().equals("")){
                AlertUtil.showAlert("ERROR","请检查输入","密码不能为空");
                return;
            }
            String sql = "";
            //检查三种类型
            switch (kind){
                
                //学生
                case 1:
                    sql = "select * from student where id = ? and password = ?";
                     Student student = DatabaseHelper.queryEntity(Student.class,sql,accountText.getValue(),passwordText.getValue());
                    if (student != null){
                        new StudentWindow(student);
                        loginStage.hide();
                    }else {
                        AlertUtil.showAlert("ERROR","请检查输入","账号或密码错误 请检查后重试");
                    }
                    break;
                //老师
                case 2:
                    sql = "select * from teacher where id = ? and password = ?";
                    Teacher teacher = DatabaseHelper.queryEntity(Teacher.class,sql,accountText.getValue(),passwordText.getValue());
                    if (teacher != null){
                        new TeacherWindow(teacher);
                        loginStage.hide();
                    }else {
                        AlertUtil.showAlert("ERROR","请检查输入","账号或密码错误 请检查后重试");
                    }
                    break;
                //管理员
                case 3:
                    sql = "select * from manager where id = ? and password = ?";
                    Manager manager = DatabaseHelper.queryEntity(Manager.class,sql,accountText.getValue(), passwordText.getValue());
                    if (manager != null){
                        new ManagerWindow(manager);
                        loginStage.hide();
                    }else {
                        AlertUtil.showAlert("ERROR","请检查输入","账号或密码错误 请检查后重试");
                    }
                    break;
            }
            
        });
        
        
    }
    
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
