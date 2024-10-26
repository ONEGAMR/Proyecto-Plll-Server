package business;

import java.time.LocalDate;

import data.Logic;
import data.LogicBD;
import data.StudentData;
import data.Utils;
import domain.Student;
import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddStudentGUIController {

	@FXML private TextField tfStudentID;
	@FXML private TextField tfName;
	@FXML private TextField tfEmail;
	@FXML private TextField tfPhone;
	@FXML private TextField tfPasword;
	@FXML private DatePicker dpEnrollmentDate;
	@FXML private RadioButton rbMale;
	@FXML private ToggleGroup genderGroup;
	@FXML private RadioButton rbFemale;
	@FXML private TextField tfAvailableBalance;
	@FXML private Label lbErrorMessage;
	@FXML private Button btReturn;
	@FXML private Button btSave;
	@FXML private ComboBox<String> cmType;
	
    // Método de inicialización llamado por el sistema FXML
    @FXML
    private void initialize() {
        // Configura el formato del DatePicker
        Logic.configureDatePicker(dpEnrollmentDate);
		cmType.getItems().addAll("estudiante", "personal");
    }

	// Maneja la acción del botón de volver
	@FXML
	private void handleReturnAction(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/MainGUI.fxml", (Stage) btReturn.getScene().getWindow());
	}

	@FXML
	private void handleSaveAction(ActionEvent event) {
	    String errorMessage = validateForm();
	    if (errorMessage != null) {
	        Utils.notifyAction(lbErrorMessage, errorMessage, Color.RED);
	        return;
	    }

	    // Obtén la fecha de inscripción del DatePicker
	    LocalDate enrollmentDate = dpEnrollmentDate.getValue();

	    // Verifica si la fecha es válida usando el nuevo método
	    String dateValidationError = Logic.validateDateNotInFuture(enrollmentDate);
	    if (dateValidationError != null) {
	        Utils.notifyAction(lbErrorMessage, dateValidationError, Color.RED);
	        return;
	    }

	    char gender = rbMale.isSelected() ? 'M' : 'F';
	    int phone = Utils.parseInteger(tfPhone.getText());
	    double availableBalance = Utils.parseDouble(tfAvailableBalance.getText());

	    if (phone == -1) {
	        Utils.notifyAction(lbErrorMessage, "Número de teléfono inválido", Color.RED);
	        return;
	    }
	    if (availableBalance == -1.0) {
	        Utils.notifyAction(lbErrorMessage, "Saldo disponible inválido", Color.RED);
	        return;
	    }

	    Student student = new Student(
	            tfStudentID.getText(),
	            tfName.getText().isEmpty() ? null : tfName.getText(),
	            tfEmail.getText().isEmpty() ? null : tfEmail.getText(),
	            phone,
	            true,
	            enrollmentDate,
	            gender,
	            availableBalance
	    );

	    if (Utils.showConfirmationAlert("¿Está seguro de que desea guardar?", "Confirmación")) {
	        boolean success = StudentData.saveStudent(student);
	        if (success) {

				LogicBD.saveUser(new User(tfStudentID.getText(), tfPasword.getText(), cmType.getValue(), "Sin foto"));
	            Utils.notifyAction(lbErrorMessage, "Estudiante registrado exitosamente", Color.GREEN);
	            clearForm();
	        } else {
	            Utils.notifyAction(lbErrorMessage, "Error al registrar al estudiante", Color.RED);
	        }
	    } else {
	        Utils.notifyAction(lbErrorMessage, "Operación cancelada", Color.ORANGE);
	    }
	}


	// Valida el formulario antes de guardar el estudiante
	private String validateForm() {
		if (tfStudentID.getText().trim().isEmpty()) return "El ID de estudiante no puede estar vacío";
		if (tfName.getText().trim().isEmpty()) return "El nombre no puede estar vacío";
		if (tfPhone.getText().trim().isEmpty()) return "El número de teléfono no puede estar vacío";
		if (tfAvailableBalance.getText().trim().isEmpty()) return "El saldo disponible no puede estar vacío";
		if (dpEnrollmentDate.getValue() == null) return "La fecha de inscripción no puede estar vacía";
		if (tfPasword.getText().trim().isEmpty()) return "La contraseña no puede estar vacía";
		if (cmType.getValue() == null) return "Seleccione el tipo de usuario";

		if (!rbMale.isSelected() && !rbFemale.isSelected()) {
			return "Debe seleccionar el género";
		}

		if (tfStudentID.getText().length() > 10) {
			return "El ID de estudiante no puede exceder los 10 caracteres";
		} else if (Logic.validateStudentID(tfStudentID.getText())) {
			return "El ID de estudiante ya existe";
		}

		String phone = tfPhone.getText();
		if (!phone.matches("\\d{8,10}")) {
			return "El número de teléfono debe tener de 8 a 10 dígitos";
		}

		try {
			double balance = Double.parseDouble(tfAvailableBalance.getText());
			if (!Logic.isValidBalance(balance, 5000, 15000)) {
				return "El saldo disponible debe estar entre 5000 y 15000";
			}
		} catch (NumberFormatException e) {
			return "El saldo disponible debe ser un número válido";
		}

		return null;
	}


	// Limpia el formulario después de guardar o cancelar
	private void clearForm() {
		tfStudentID.clear();
		tfName.clear();
		tfEmail.clear();
		tfPhone.clear();
		tfAvailableBalance.clear();
		dpEnrollmentDate.setValue(null);
		rbMale.setSelected(false);
		rbFemale.setSelected(false);
	}
}
