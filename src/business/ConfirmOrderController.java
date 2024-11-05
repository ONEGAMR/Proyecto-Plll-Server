package business;

import data.Logic;
import data.LogicBD;
import data.Utils;
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
    private Label lbErrorMessage;

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

        //se verifica si hay un elemento seleccionado y se recarga la lista con o sin filtro
        if(selectedOrder != null){

            String confirmationTitle = "¿Está seguro de desea eliminar el pedio?";
            String confirmationContent = selectedOrder.getName() + " Cliente: " + selectedOrder.getIdStudent();

            boolean isConfirmed = Utils.showConfirmationAlert(confirmationTitle, confirmationContent);

            if(isConfirmed) {
                LogicBD.deleteOrder(selectedOrder);

                if (cmStatus.getValue() != null) {

                    fillTable(cmStatus.getValue());
                } else {

                    fillTable("Pendiente");
                }

                selectedOrder = null;
            }

        }else{

            Logic.showPopupMessage("Elija una orden a eliminar");
        }

    }

    @FXML
    void allORders(ActionEvent event) {

        //se llena la tabala con el filtro de pendientes
        fillTable("Pendiente");
        selectedOrder = null;
    }

    @FXML
    void handleAddBalanceAction(ActionEvent event) {
        //se verifica si hay un elemento seleccionado y se envia a editar
        if (selectedOrder != null) {

            Logic.order = selectedOrder;

            Logic.status = cmStatus.getValue();
            Logic.closeCurrentWindowAndOpen("/presentation/UpdateOrder.fxml", ((Stage) btReturn.getScene().getWindow()));

        }else{

            Logic.showPopupMessage("No hay pedidos seleccionados");
        }
    }

    public void columTable() {
        //se inicializan las columnas
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
        //se llena la tabla dependiendo del filtro solicitado
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

    // Escucha si se selecciona un elemento de la tabla
    public void getSelectedOrder() {
        tvOrdes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedOrder = newValue;
            }
        });
    }

    //metodo para saber la seleccion de combobox
    public void getSelectedStatus() {
        cmStatus.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                fillTable(newValue);
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

        //se llena la lista con el filtro guardado
        if(Logic.fillList(cmStatus)){
            fillTable(Logic.status);
        }
    }
}
