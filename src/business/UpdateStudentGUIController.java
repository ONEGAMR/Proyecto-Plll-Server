package business;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import data.Logic;
import data.StudentData;
import data.Utils;
import domain.Student;
import javafx.event.ActionEvent;

public class UpdateStudentGUIController {

    @FXML private TextField tfStudentID;
    @FXML private TextField tfName;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;
    @FXML private CheckBox cbIsActive;
    @FXML private TextField tfAvailableMoney;
    @FXML private Label lbErrorMessage;
    @FXML private Button btReturn;
    @FXML private Button btEdit;

    private Student student = Logic.getCurrentStudent();

    @FXML
    public void handleReturnAction(ActionEvent event) {
        Logic.closeCurrentWindowAndOpen("/presentation/ViewStudentGUI.fxml", ((Stage) btReturn.getScene().getWindow()));
    }

    @FXML
    public void handleEditAction(ActionEvent event) {
        String errorMessage = validateForm();
        if (errorMessage != null) {
        	Utils.notifyAction(lbErrorMessage, errorMessage, Color.RED);
            return;
        }

        int phone = Utils.parseInteger(tfPhone.getText());
        double availableMoney = Utils.parseDouble(tfAvailableMoney.getText());

        if (phone == -1) {
        	Utils.notifyAction(lbErrorMessage, "Número de teléfono inválido", Color.RED);
            return;
        }
        if (availableMoney == -1.0) {
        	Utils.notifyAction(lbErrorMessage, "Cantidad de dinero disponible inválida", Color.RED);
            return;
        }

        // Actualiza el objeto estudiante existente
        student.setNombre(tfName.getText().isEmpty() ? null : tfName.getText());
        student.setCorreoElectronico(tfEmail.getText() == null || tfEmail.getText().isEmpty() ? null : tfEmail.getText());
        student.setTelefono(phone);
        student.setEstaActivo(cbIsActive.isSelected());
        student.setDineroDisponible(availableMoney);

        // Confirma la acción antes de guardar
        if (Utils.showConfirmationAlert("¿Estás seguro de que deseas guardar los cambios?", "Confirmación")) {
            boolean success = StudentData.updateStudent(student);
            if (success) {
            	Utils.notifyAction(lbErrorMessage, "Estudiante actualizado con éxito", Color.GREEN);
                Logic.closeCurrentWindowAndOpen("/presentation/ViewStudentGUI.fxml", ((Stage) btReturn.getScene().getWindow()));
            } else {
            	Utils.notifyAction(lbErrorMessage, "Error al actualizar el estudiante", Color.RED);
            }
        } else {
        	Utils.notifyAction(lbErrorMessage, "Operación cancelada", Color.ORANGE);
        }
    }

    // Valida los campos del formulario
    private String validateForm() {
        if (tfStudentID.getText().trim().isEmpty()) return "El ID no puede estar vacío";
        if (tfPhone.getText().trim().isEmpty()) return "El número de teléfono no puede estar vacío";
        if (tfAvailableMoney.getText().trim().isEmpty()) return "La cantidad de dinero disponible no puede estar vacía";
        if (!tfPhone.getText().matches("\\d{8,10}")) return "El número de teléfono debe tener entre 8 y 10 dígitos";

        try {
            double money = Double.parseDouble(tfAvailableMoney.getText());
            if (!Logic.isValidBalance(money, 5000, 15000)) {
                return "La cantidad de dinero disponible debe estar entre 5000 y 15000";
            }
        } catch (NumberFormatException e) {
            return "La cantidad de dinero disponible debe ser un número válido";
        }

        return null;
    }

    @FXML
    private void initialize() {
        if (student != null) {
            tfStudentID.setText(student.getCarnet());
            tfName.setText(student.getNombre());
            tfEmail.setText(student.getCorreoElectronico());
            tfPhone.setText(String.valueOf(student.getTelefono()));
            cbIsActive.setSelected(student.isEstaActivo());
            tfAvailableMoney.setText(String.valueOf(student.getDineroDisponible()));
        } else {
            lbErrorMessage.setText("No hay datos de estudiante disponibles.");
        }
    }
}
