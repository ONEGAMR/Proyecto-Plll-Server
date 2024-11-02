package business;

import data.ServerSocketOrder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import data.Logic;
import data.StudentData;
import data.Utils;
import domain.Student;
import domain.Recharge;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class AddBalanceGUIController {

    public Button btReturn;
    @FXML private TextField tfStudentID;
    @FXML private TextField tfAmount;
    @FXML private DatePicker dpDate;
    @FXML private Button btBack;
    @FXML private Button btRecharge;
    @FXML private Label lbErrorMessage;

    private static final double MIN_BALANCE = 1000;
    private static final double MAX_BALANCE = 10000;
    
    // Método de inicialización llamado por el sistema FXML
    @FXML
    private void initialize() {

        // Configura el formato del DatePicker
        Logic.configureDatePicker(dpDate);
    }

    // Maneja la acción del botón de recargar saldo
    @FXML
    private void handleRechargeAction(ActionEvent event) {
        String studentID = tfStudentID.getText().trim();
        String amountText = tfAmount.getText().trim();

        if (!isValidForm(studentID, amountText)) {
            return;
        }
        
        // Obtén la fecha de inscripción del DatePicker
	    LocalDate date = dpDate.getValue();

	    // Verifica si la fecha es válida usando el nuevo método
	    String dateValidationError = Logic.validateDateNotInFuture(date);
	    if (dateValidationError != null) {
	        Utils.notifyAction(lbErrorMessage, dateValidationError, Color.RED);
	        return;
	    }

        double amount = Utils.parseDouble(amountText);
        Student student = Logic.findStudentByID(studentID);

        if (student == null) {
            Utils.notifyAction(lbErrorMessage, "El ID de estudiante " + studentID + " no existe. Por favor, ingrese un ID válido.", Color.RED);
            return;
        }

        // Verifica si el estudiante está activo
        if (!student.isEstaActivo()) {
            Utils.notifyAction(lbErrorMessage, "El estudiante con el ID " + studentID + " no está activo. No se puede realizar la recarga.", Color.RED);
            return;
        }

        double newBalance = student.getDineroDisponible() + amount;

        if (!Logic.isValidBalance(amount, MIN_BALANCE, MAX_BALANCE)) {
            Utils.notifyAction(lbErrorMessage, "El saldo total debe estar entre " + MIN_BALANCE + " y " + MAX_BALANCE + ". Actual: " + student.getDineroDisponible(), Color.RED);
            return;
        }

        Recharge recharge = new Recharge(studentID, amount, LocalDate.now());

        if (Utils.showConfirmationAlert("¿Está seguro de que desea agregar esta cantidad?", "Agregar Monto")) {
            try {
                Logic.RechargesJsonUtils.saveElement(recharge);
                updateStudentBalance(student, newBalance);

                //se envia al cliente el nuevo balance
                ServerSocketOrder.sendMessageToClient(student.getCarnet(), "newBalance,"+newBalance);

                clearFields();
                Utils.notifyAction(lbErrorMessage, "Saldo recargado exitosamente.", Color.GREEN);
                Logic.closeCurrentWindowAndOpen("/presentation/RechargesRegister.fxml", (Stage) dpDate.getScene().getWindow());
            } catch (IOException e) {

                Utils.notifyAction(lbErrorMessage, "Error al guardar la recarga: " + e.getMessage(), Color.RED);
            } catch (Exception e) {

                Utils.notifyAction(lbErrorMessage, "Ocurrió un error inesperado: " + e.getMessage(), Color.RED);
            }
        }
    }


    // Verifica si el formulario es válido
    private boolean isValidForm(String studentID, String amountText) {
        if (studentID.isEmpty() || amountText.isEmpty()) {
        	Utils.notifyAction(lbErrorMessage, "Por favor, complete todos los campos.",Color.RED);
            return false;
        }
        if (Utils.parseDouble(amountText) == -1.0) {
        	Utils.notifyAction(lbErrorMessage, "Monto inválido. Por favor, ingrese un número válido.",Color.RED);
            return false;
        }
        return true;
    }

    // Actualiza el saldo de un estudiante
    private void updateStudentBalance(Student student, double newBalance) throws IOException {

        List<Student> studentList = StudentData.getStudentList();
        for (Student s : studentList) {
            if (s.getCarnet().equals(student.getCarnet())) {
                s.setDineroDisponible(newBalance);
                break;
            }
        }

        StudentData.updateJson(studentList);
    }

    // Limpia los campos del formulario y el mensaje de error
    private void clearFields() {
        tfStudentID.clear();
        tfAmount.clear();
        lbErrorMessage.setText("");
    }

    public void handleReturnAction(ActionEvent actionEvent) {
        Logic.closeCurrentWindowAndOpen("/presentation/RechargesRegister.fxml", (Stage) dpDate.getScene().getWindow());
    }
}
