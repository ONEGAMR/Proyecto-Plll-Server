package business;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import data.Logic;
import data.Utils;
import domain.Meal;

public class ServiceRequestGUIController {

    @FXML private Label lbTitle;
    @FXML private Label lbMealTime;
    @FXML private RadioButton rbBreakfast;
    @FXML private ToggleGroup mealGroup;
    @FXML private RadioButton rbLunch;
    @FXML private Label lbServiceDay;
    @FXML private ComboBox<String> cbServiceDay;
    @FXML private TextField tfName;
    @FXML private Label lbPrice;
    @FXML private TextField tfPrice;
    @FXML private Label lbErrorMessage;
    @FXML private Button btReturn;
    @FXML private Button btSave;

    private String filePath;

    // Inicializa el ComboBox con los días de la semana en español
    @FXML
    public void initialize() {
        cbServiceDay.setItems(FXCollections.observableArrayList(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        ));
    }
    // Maneja la acción del botón de volver
    @FXML
    public void handleReturn(ActionEvent event) {
        Logic.closeCurrentWindowAndOpen("/presentation/ServiceViewGUI.fxml", (Stage) btReturn.getScene().getWindow());
    }

    // Maneja la acción del botón de guardar
    @FXML
    public void handleSave(ActionEvent event) {
        String errorMessage = validateForm();

        if (errorMessage != null) {
        	Utils.notifyAction(lbErrorMessage, errorMessage, Color.RED);
            return; // Sale del método si la validación falla
        }

        String day = cbServiceDay.getValue();
        String type = rbBreakfast.isSelected() ? "breakfast" : rbLunch.isSelected() ? "lunch" : "";

        String confirmationTitle = "¿Está seguro de agregar un nuevo servicio?";
        String confirmationContent = "Día: " + day + "\nHorario: " + (type.equals("breakfast") ? "Desayuno" : "Almuerzo");

        boolean isConfirmed = Utils.showConfirmationAlert(
            confirmationTitle,
            confirmationContent
        );

        if (isConfirmed) {
            try {
                String name = tfName.getText().trim();
                int price = Integer.parseInt(tfPrice.getText().trim());
                Meal meal = new Meal(name, price);

                filePath = Logic.getFilePath(day, type);
                Logic.MealsJsonUtils.setFilePath(filePath);
                Logic.MealsJsonUtils.saveElement(meal);

                Utils.showAlert(AlertType.INFORMATION, "Registro Exitoso", "Nombre: " + name + "\nPrecio: ₡" + price);
                Utils.notifyAction(lbErrorMessage, "Comida guardada con éxito", Color.GREEN);
            } catch (NumberFormatException e) {
            	Utils.notifyAction(lbErrorMessage, "Error al procesar el precio", Color.RED);
            } catch (Exception e) {
            	Utils.notifyAction(lbErrorMessage, "Error al guardar la comida: " + e.getMessage(), Color.RED);
            }
        }
    }

    // Valida el formulario
    private String validateForm() {
        if (cbServiceDay.getValue() == null || cbServiceDay.getValue().isEmpty()) {
            return "Debes seleccionar un día";
        }

        if (!rbBreakfast.isSelected() && !rbLunch.isSelected()) {
            return "Debes seleccionar un tipo de comida (desayuno o almuerzo)";
        }

        if (tfName.getText().trim().isEmpty()) {
            return "El nombre no puede estar vacío";
        }else if(tfName.getText().length() > 25){
            return  "El nombre es muy largo (Max 25 caracteres)";
        }

        String priceText = tfPrice.getText().trim();
        if (priceText.isEmpty()) {
            return "El precio no puede estar vacío";
        }
        try {
            int price = Integer.parseInt(priceText);
            if (price < 0) {
                return "El precio no puede ser negativo";
            }
        } catch (NumberFormatException e) {
            return "El precio debe ser un número entero válido";
        }

        return null;
    }
}
