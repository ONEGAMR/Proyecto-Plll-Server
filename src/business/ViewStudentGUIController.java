package business;

import data.LogicBD;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import data.Logic;
import data.StudentData;
import data.Utils;
import domain.Student;

import java.time.LocalDate;
import java.util.List;

public class ViewStudentGUIController {

    @FXML private Label lbTitle;
    @FXML private TableView<Student> tvStudents;
    @FXML private TableColumn<Student, String> tcStudentID;
    @FXML private TableColumn<Student, String> tcName;
    @FXML private TableColumn<Student, String> tcEmail;
    @FXML private TableColumn<Student, String> tcPhone;
    @FXML private TableColumn<Student, String> tcActive;
    @FXML private TableColumn<Student, String> tcEnrollmentDate;
    @FXML private TableColumn<Student, String> tcGender;
    @FXML private TableColumn<Student, Double> tcBalance;
    @FXML private Label lbEmptyTable;
    @FXML private Button btReturn;
    @FXML private Button btEdit;
    @FXML private Button btDelete;

    private Student selectedStudent;

    @FXML
    public void handleReturnAction(ActionEvent event) {
        Logic.closeCurrentWindowAndOpen("/presentation/MainGUI.fxml", ((Stage) btEdit.getScene().getWindow()));
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStudentData();
        setupTableSelection();
    }

    @FXML
    public void handleEditAction(ActionEvent event) {
        if (selectedStudent != null) {
            Logic.setCurrentStudent(selectedStudent);
            Logic.closeCurrentWindowAndOpen("/presentation/UpdateStudent.fxml", ((Stage) btEdit.getScene().getWindow()));
        }
    }

    @FXML
    public void handleDeleteAction(ActionEvent event) {
        if (selectedStudent != null) {
            if (Utils.showConfirmationAlert("¿Estás seguro de que deseas eliminar este estudiante?", "Confirmar Eliminación")) {
                StudentData.delete();
                LogicBD.deleteUser(selectedStudent.getCarnet());
                tvStudents.getItems().remove(selectedStudent);
                tvStudents.refresh();
                selectedStudent = null;
                lbEmptyTable.setVisible(tvStudents.getItems().isEmpty());

            }
        }
    }

    private void setupTableColumns() {
        tcStudentID.setCellValueFactory(new PropertyValueFactory<>("carnet"));
        tcName.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getNombre();
            return new javafx.beans.property.SimpleStringProperty(name != null ? name : "Sin nombre");
        });

        tcEmail.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getCorreoElectronico();
            return new javafx.beans.property.SimpleStringProperty(email != null ? email : "Sin email");
        });

        tcPhone.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        tcActive.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isEstaActivo() ? "Sí" : "No"));
        // Usa el método formatDate de Logic para formatear la fecha de inscripción
        tcEnrollmentDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getFechaIngreso();
            String formattedDate = Logic.formatDate(date);
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });
        tcGender.setCellValueFactory(new PropertyValueFactory<>("genero"));
        tcBalance.setCellValueFactory(new PropertyValueFactory<>("dineroDisponible"));
    }

    private void loadStudentData() {
        List<Student> studentList = StudentData.getStudentList();
        if (studentList != null) {
            tvStudents.setItems(FXCollections.observableArrayList(studentList));
            lbEmptyTable.setVisible(studentList.isEmpty());
        }
    }

    private void setupTableSelection() {
        tvStudents.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedStudent = newValue;
            if (selectedStudent != null) {
                Logic.setCurrentStudent(selectedStudent);
            }
        });
    }
}
