package business;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import data.Logic;
import domain.Recharge;
import domain.Student;

public class BalanceInquiryGUIController {

    @FXML private TextField tfStudentID;
    @FXML private TableView<Recharge> tvRecharges;
    @FXML private TableColumn<Recharge, String> tcStudentID;
    @FXML private TableColumn<Recharge, String> tcStudentName;
    @FXML private TableColumn<Recharge, String> tcRechargeDate;
    @FXML private TableColumn<Recharge, Double> tcAmount;
    @FXML private Label lbEmptyTableMessage;
    @FXML private Button btConsult;
    @FXML private Button btReturn;
    @FXML private Button btAddBalance;

    private Student currentStudent;

    // Inicializa las columnas de la tabla
    @FXML
    private void initialize() {
        setupTableColumns();
    }

    private void setupTableColumns() {
        tcStudentID.setCellValueFactory(new PropertyValueFactory<>("carnet"));
        tcRechargeDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            String formattedDate = Logic.formatDate(date);
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });
        tcAmount.setCellValueFactory(new PropertyValueFactory<>("monto"));
        tcStudentName.setCellValueFactory(cellData -> {
            String studentName = "";
            if (currentStudent != null && currentStudent.getCarnet().equals(cellData.getValue().getCarnet())) {
                studentName = currentStudent.getNombre();
            }
            return new SimpleStringProperty(studentName != null && !studentName.isEmpty() ? studentName : "Nombre no disponible");
        });
    }

    // Maneja la acción del botón de consultar saldo
    @FXML
    public void handleConsultBalance() {
        String carnet = tfStudentID.getText();
        if (carnet.isEmpty()) {
            notifyError("El ID del estudiante está vacío");
            return;
        }

        currentStudent = Logic.findStudentByID(carnet);
        if (currentStudent == null) {
            tvRecharges.setItems(FXCollections.emptyObservableList());
            notifyError("El ID del estudiante " + carnet + " no existe");
            return;
        }

        // Verifica si el estudiante está activo
        if (!currentStudent.isEstaActivo()) {
            tvRecharges.setItems(FXCollections.emptyObservableList());
            notifyError("El estudiante con el ID " + carnet + " no está activo");
            return;
        }

        try {
            List<Recharge> rechargesList = Logic.RechargesJsonUtils.getElements(Recharge.class);
            if (rechargesList == null || rechargesList.isEmpty()) {
                notifyError("No se encontraron recargas");
                return;
            }

            List<Recharge> filteredRecharges = rechargesList.stream()
                .filter(recharge -> carnet.equals(recharge.getCarnet()))
                .collect(Collectors.toList());

            if (filteredRecharges.isEmpty()) {
                tvRecharges.setItems(FXCollections.emptyObservableList());
                notifyError("No se encontraron recargas para el ID del estudiante " + carnet);
            } else {
                tvRecharges.setItems(FXCollections.observableArrayList(filteredRecharges));
                lbEmptyTableMessage.setText(""); // Limpia el mensaje de tabla vacía si hay datos presentes
            }
            tvRecharges.refresh();
        } catch (IOException e) {
            notifyError("Error al cargar los datos");
        }
    }

    // Muestra un mensaje de error en la etiqueta
    private void notifyError(String message) {
        lbEmptyTableMessage.setText(message);
        lbEmptyTableMessage.setTextFill(Color.RED);
    }

    // Maneja la acción del botón de volver
    @FXML
    public void handleReturnAction() {
        Logic.closeCurrentWindowAndOpen("/presentation/MainGUI.fxml", (Stage) btAddBalance.getScene().getWindow());
    }

    // Maneja la acción del botón de agregar saldo
    @FXML
    public void handleAddBalanceAction() {
        Logic.closeCurrentWindowAndOpen("/presentation/AddBalance.fxml", (Stage) btAddBalance.getScene().getWindow());
    }
}
