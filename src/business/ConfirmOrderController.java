package business;

import data.Logic;
import data.LogicBD;
import domain.Orders;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;

public class ConfirmOrderController {

    @FXML
    private TableView<Orders> tvOrdes;

    @FXML
    private TableColumn<Orders, String> tcStudentID;

    @FXML
    private TableColumn<Orders, String> tcNameOrder;

    @FXML
    private TableColumn<Orders, Integer> tcNumber;

    @FXML
    private TableColumn<Orders, Integer> tcTotal;

    @FXML
    private TableColumn<Orders, String> tcStatus;

    @FXML
    private TableColumn<Orders, String> tcCarnetEs;

    @FXML
    private Label lbEmptyTableMessage;

    @FXML
    private Button btReturn;

    @FXML
    private Button btAddBalance;

    @FXML
    private Button btAll;

    @FXML
    private Button btDelete;

    @FXML
    private ComboBox<String> cmStatus;

    private Orders selectedOrder;
    @FXML
    void handleReturnAction(ActionEvent event) {
        Logic.status = null;
        Logic.closeCurrentWindowAndOpen("/presentation/MainGUI.fxml", ((Stage) btReturn.getScene().getWindow()));
    }
    @FXML
    void deleteOrder(ActionEvent event) {

        if(selectedOrder != null){

            LogicBD.deleteOrder(selectedOrder);
            if(cmStatus != null){
                fillTable(cmStatus.getValue());
            }else {
                fillTable("Pendiente");
            }

        }else{

            System.out.println("Elija una orden a eliminar");

        }

    }

    @FXML
    void allORders(ActionEvent event) {

        fillTable("Pendiente");
    }

    @FXML
    void handleAddBalanceAction(ActionEvent event) {
        if (selectedOrder != null) {

            Logic.order = selectedOrder;

            Logic.status = cmStatus.getValue();
            Logic.closeCurrentWindowAndOpen("/presentation/UpdateOrder.fxml", ((Stage) btReturn.getScene().getWindow()));

        }else{
            notifyError("No hay pedidos seleccionados");
        }
    }

    public void columTable() {
       tcStudentID.setCellValueFactory((cellData ->
               new ReadOnlyObjectWrapper<>(tvOrdes.getItems().indexOf(cellData.getValue()) + 1).asString()
       ));
        tcNameOrder.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcNumber.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
       tcTotal.setCellValueFactory(new PropertyValueFactory<>("totalOrder"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tcCarnetEs.setCellValueFactory(new PropertyValueFactory<>("idStudent"));
    }

    public void fillTable(String list) {
        tvOrdes.getItems().clear();
        List<Orders> ordersList;

        if(list.equals("Todos")){

            ordersList = LogicBD.getListOrders();
        }else {


            System.out.println(list+ "  estado pasa");
            ordersList = LogicBD.getListOrdersStatus(list);
        }


        if (!ordersList.isEmpty()) {

            tvOrdes.setItems(FXCollections.observableArrayList(ordersList));
        } else {

            notifyError("No hay pedidos");
            lbEmptyTableMessage.setVisible(true);  // Opcional: Mostrar mensaje si la lista está vacía
        }
    }
    private void notifyError(String message) {
        lbEmptyTableMessage.setText(message);
        lbEmptyTableMessage.setTextFill(Color.RED);
    }

    // Asumiendo que tvOrders es tu TableView de tipo TableView<Orders>
    public void getSelectedOrder() {
        tvOrdes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedOrder = newValue;
                // Aquí puedes trabajar con el nuevo elemento seleccionado
                System.out.println("Orden seleccionada: " + selectedOrder.toString());
            } else {
                System.out.println("No hay ninguna orden seleccionada.");
            }
        });
    }

    public void getSelectedStatus() {
        cmStatus.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                fillTable(newValue);
                System.out.println("Orden seleccionada: " + newValue);
            } else {
                System.out.println("No hay ningun orden seleccionada.");
            }
        });
    }


    @FXML
    public void initialize() {
        cmStatus.getItems().addAll("Todos", "Preparando", "Listo", "Entregado");
        columTable();
        fillTable("Pendiente");
        getSelectedOrder();
        getSelectedStatus();

        if(Logic.fillList(cmStatus)){
            fillTable(Logic.status);
        }
    }
}
