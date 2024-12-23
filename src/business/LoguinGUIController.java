package business;

import data.*;
import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoguinGUIController {
	@FXML
	private TextField tfIp;
	@FXML
	private TextField tfId;
	@FXML
	private TextField tfPassword;
	@FXML
	private Button btEnter;
	@FXML
	private Label message;
	

	// Event Listener on Button[#btEnter].onAction
	@FXML
	public void initialize() {

		//Se inicia la conxion con los clientes
		ServerSocketOrder.runServer();
	}
	
	@FXML
	public void btEnter(ActionEvent event) {

		//Validacion de texfield vacios
		if(tfId.getText().trim().isEmpty() || tfPassword.getText().trim().isEmpty()){

			message.setText("No pueden haber campos vacios");
		}else if(LogicBD.getUserValidate(tfId.getText()) != null){

			//Se busca el usuario en el servidor y se realizan verificacios de tipo y estado de usuario
			User user = LogicBD.getUserValidate(tfId.getText());

			if(!StudentData.searchStudent(tfId.getText()).isEstaActivo()){
				System.out.println(StudentData.searchStudent(tfId.getText()).isEstaActivo()+"sddasa");

				message.setText("El usiario inactivo");
				return;
			}

			if(tfPassword.getText().equals(user.getPassword())){

				if(user.getType().equals("personal")) {

					Logic.closeCurrentWindowAndOpen("/presentation/MainGUI.fxml", ((Stage) btEnter.getScene().getWindow()));
				}else {

					message.setText("El usiario no es un personal");
				}
			}else {

				message.setText("Contraseña incorrecta");
			}
		}else {

			message.setText("Usuario no existe");
		}
	}

}
