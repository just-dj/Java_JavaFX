package root.util;

import javafx.scene.control.Alert;

public class AlertUtil {
	
	public static void showAlert(String title,String header,String content){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
