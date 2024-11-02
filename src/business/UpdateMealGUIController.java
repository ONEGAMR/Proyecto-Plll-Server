package business;

import data.Logic;
import data.Utils;
import domain.Meal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UpdateMealGUIController {

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

        fillForm();
    }

    public void fillForm(){

        if(Logic.meal != null){

            tfName.setText(Logic.meal.getName());
            tfPrice.setText(String.valueOf(Logic.meal.getPrice()));
        }
    }

    // Maneja la acción del botón de volver
    @FXML
    public void handleReturn(ActionEvent event) {
        Logic.meal = null;
        Logic.closeCurrentWindowAndOpen("/presentation/MealGestor.fxml", (Stage) btReturn.getScene().getWindow());
    }

    // Maneja la acción del botón de guardar
    @FXML
    public void handleSave(ActionEvent event) {
        String errorMessage = validateForm();

        if (errorMessage != null) {
        	Utils.notifyAction(lbErrorMessage, errorMessage, Color.RED);
            return; // Sale del método si la validación falla
        }

        String confirmationTitle = "¿Está seguro de editar el servicio?";
        String confirmationContent = Logic.meal.toStringMealData();

        boolean isConfirmed = Utils.showConfirmationAlert(
            confirmationTitle,
            confirmationContent
        );

        if (isConfirmed) {
            try {
                String name = tfName.getText().trim();
                int price = Integer.parseInt(tfPrice.getText().trim());
                Meal meal = new Meal(name, price,"");

                System.out.println("Desde updateMEal "+ meal.toStringMealData());
                Logic.MealsJsonUtils.setFilePath(Logic.filePath);
                //se actualiza
                Logic.MealsJsonUtils.updateMeal(meal, Logic.meal.getName());

                Utils.showAlert(AlertType.INFORMATION, "Registro Exitoso", "Nombre: " + name + "\nPrecio: ₡" + price);
                Utils.notifyAction(lbErrorMessage, "Comida guardada con éxito", Color.GREEN);
                Logic.meal = null;
            } catch (NumberFormatException e) {
            	Utils.notifyAction(lbErrorMessage, "Error al procesar el precio", Color.RED);
            } catch (Exception e) {
            	Utils.notifyAction(lbErrorMessage, "Error al guardar la comida: " + e.getMessage(), Color.RED);
            }
        }
    }

    // Valida el formulario
    private String validateForm() {

        if (tfName.getText().trim().isEmpty()) {
            return "El nombre no puede estar vacío";
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
