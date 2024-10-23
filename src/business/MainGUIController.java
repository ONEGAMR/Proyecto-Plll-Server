package business;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import data.Logic;
import data.ServerSocketOrder;
import javafx.application.Application;
import javafx.event.ActionEvent;

public class MainGUIController extends Application {
	@FXML private Label lbProyect;
	@FXML private Label lbTitle;
	@FXML private Label lbSubtitle;
	@FXML private Button btSolicitarServicio;
	@FXML private Button btVerSaldo;
	@FXML private Button btAgregarEstudiante;
	@FXML private Button btVerEstudiante;
	@FXML private Button btShowOrders;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/presentation/MainGUI.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@FXML
	private void initialize() {
		ServerSocketOrder.runServer();
	}
	// Event Listener on Button[#btSolicitarServicio].onAction
	@FXML
	public void solicitarServicio(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/ServiceViewGUI.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}
	// Event Listener on Button[#btVerSaldo].onAction
	@FXML
	public void verSaldo(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/BalanceInquiryGUI.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}
	// Event Listener on Button[#btAgregarEstudiante].onAction
	@FXML
	public void agregarEstudiante(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/AddStudentGUI.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}
	// Event Listener on Button[#btVerEstudiante].onAction
	@FXML
	public void verEstudiante(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/ViewStudentGUI.fxml", ((Stage) lbTitle.getScene().getWindow()));
	}

	@FXML
	public void showOrder(ActionEvent event) {

		Logic.closeCurrentWindowAndOpen("/presentation/ConfirmOrder.fxml", ((Stage) btShowOrders.getScene().getWindow()));
	}
}
