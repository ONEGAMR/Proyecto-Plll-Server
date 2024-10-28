package business;

import data.LogicBD;
import domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML private TextField tfPassword;
    @FXML private CheckBox cbIsActive;
    @FXML private TextField tfAvailableMoney;
    @FXML private Label lbErrorMessage;
    @FXML private Button btReturn;
    @FXML private Button btEdit;
    @FXML private ComboBox<String> cmType;

    private Student student = Logic.getCurrentStudent();
    private User us = LogicBD.getUserValidate(student.getCarnet());

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
        us.setPassword(tfPassword.getText());
        us.setType(cmType.getValue());

        // Confirma la acción antes de guardar
        if (Utils.showConfirmationAlert("¿Estás seguro de que deseas guardar los cambios?", "Confirmación")) {
            boolean success = StudentData.updateStudent(student);
            if (success) {

                LogicBD.updateUserBDUs(us);
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
        if (tfPassword.getText().length() > 10) return "La contraseña no puede ser mayor a 45 caracteres";
        if (tfStudentID.getText().trim().isEmpty()) return "El ID no puede estar vacío";
        if (tfPhone.getText().trim().isEmpty()) return "El número de teléfono no puede estar vacío";
        if (tfAvailableMoney.getText().trim().isEmpty()) return "La cantidad de dinero disponible no puede estar vacía";
        if (!tfPhone.getText().matches("\\d{8,10}")) return "El número de teléfono debe tener entre 8 y 10 dígitos";
        if (tfPassword.getText().trim().isEmpty()) return "La contraseña no puede estar vacía";
        if (cmType.getValue() == null) return "Seleccione el tipo de usuario";

        try {
            double money = Double.parseDouble(tfAvailableMoney.getText());
            if (money < 1000) {

                return "La cantidad de dinero disponible debe ser mayor o igual a 1000";
            }
        } catch (NumberFormatException e) {
            return "La cantidad de dinero disponible debe ser un número válido";
        }

        return null;
    }

    @FXML
    private void initialize() {
        cmType.getItems().addAll("estudiante", "personal");

        if (student != null) {
            tfStudentID.setText(student.getCarnet());
            tfName.setText(student.getNombre());
            tfEmail.setText(student.getCorreoElectronico());
            tfPhone.setText(String.valueOf(student.getTelefono()));
            cbIsActive.setSelected(student.isEstaActivo());
            tfAvailableMoney.setText(String.valueOf(student.getDineroDisponible()));
            tfPassword.setText(us.getPassword());
            cmType.getSelectionModel().select(us.getType());
        } else {
            lbErrorMessage.setText("No hay datos de estudiante disponibles.");
        }
    }
}
