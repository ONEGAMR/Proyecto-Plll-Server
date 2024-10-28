package business;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import data.Logic;
import javafx.event.ActionEvent;

public class MainGUIController {
	@FXML private Label lbProyect;
	@FXML private Label lbTitle;
	@FXML private Label lbSubtitle;
	@FXML private Button btSolicitarServicio;
	@FXML private Button btVerSaldo;
	@FXML private Button btAgregarEstudiante;
	@FXML private Button btVerEstudiante;
	@FXML private Button btShowOrders;

	@FXML
	private void initialize() {

	}
	// Event Listener on Button[#btSolicitarServicio].onAction
	@FXML
	public void solicitarServicio(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/MealGestor.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}
	// Event Listener on Button[#btVerSaldo].onAction
	@FXML
	public void verSaldo(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/RechargesRegister.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}
	// Event Listener on Button[#btAgregarEstudiante].onAction
	@FXML
	public void agregarEstudiante(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/AddStudent.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}
	// Event Listener on Button[#btVerEstudiante].onAction
	@FXML
	public void verEstudiante(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/ViewStudent.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}

	@FXML
	public void showOrder(ActionEvent event) {

		Logic.closeCurrentWindowAndOpen("/presentation/OrderRegister.fxml", ((Stage) btShowOrders.getScene().getWindow()));
	}

	@FXML
	public void handleLogOut(ActionEvent actionEvent) {
		Logic.closeCurrentWindowAndOpen("/presentation/LogIn.fxml", ((Stage) btShowOrders.getScene().getWindow()));
	}
}
