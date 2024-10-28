package business;

import data.Logic;
import data.LogicBD;
import data.ServerSocketOrder;
import domain.Orders;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateOrderGUIController {

    @FXML
    private Label lbTitle;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPrice;
    
    @FXML
    private TextField tfCarnet;

    @FXML
    private Label lbErrorMessage;

    @FXML
    private Button btReturn;

    @FXML
    private Button btSave;

    // Método initialize para configurar el controlador
    @FXML
    public void initialize() {
        cbStatus.getItems().addAll("Pendiente", "Preparando", "Listo", "Entregado");
        fillForm();
    }

    public void fillForm() {

        if(Logic.order != null) {

            cbStatus.getSelectionModel().select(Logic.order.getStatus());
            tfName.setText(Logic.order.getName());
            tfPrice.setText(String.valueOf(Logic.order.getTotalOrder()));
            tfCarnet.setText(Logic.order.getIdStudent());
        }
    }

    @FXML
    private void handleReturn() {
        // Cierra la ventana actual
        Logic.closeCurrentWindowAndOpen("/presentation/ConfirmOrder.fxml", ((Stage) btReturn.getScene().getWindow()));
    }

    @FXML
    private void handleSave() {
        // Obtiene el estado seleccionado
        String selectedStatus = cbStatus.getSelectionModel().getSelectedItem();

        if (selectedStatus == null) {
            lbErrorMessage.setText("Por favor, seleccione un estado.");
        } else {
            Logic.order.setStatus(selectedStatus);

            if(LogicBD.updateOrderBD(Logic.order)) {

                ServerSocketOrder.sendMessageToClient(Logic.order.getIdStudent(), "notifyStatus,Tu pedido "+ Logic.order.getName() +
                        " se encuentra: "+ selectedStatus);

                lbErrorMessage.setText("Estado guardado exitosamente.");
                Logic.closeCurrentWindowAndOpen("/presentation/ConfirmOrder.fxml", ((Stage) btReturn.getScene().getWindow()));
            }else{
                lbErrorMessage.setText("Error al guardar el estado.");
            }
        }
    }
}