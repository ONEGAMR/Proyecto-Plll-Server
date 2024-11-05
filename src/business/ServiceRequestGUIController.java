package business;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import data.ClientHandler;
import data.Logic;
import data.Utils;
import domain.Meal;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import static data.Logic.filePath;

public class ServiceRequestGUIController {
    @FXML private Button btSelectImage;
    @FXML private ImageView ivPreview;
    @FXML private Label lbImagePath;

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

    private File selectedImageFile;

    @FXML
    public void initialize() {
        cbServiceDay.setItems(FXCollections.observableArrayList(
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes"
        ));

        // Configurar el ImageView para la vista previa
        ivPreview.setFitHeight(150);
        ivPreview.setFitWidth(150);
        ivPreview.setPreserveRatio(true);
    }

    @FXML
    public void handleSelectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog((Stage) btSelectImage.getScene().getWindow());
        if (selectedFile != null) {
            selectedImageFile = selectedFile;
            // Mostrar vista previa de la imagen
            Image image = new Image(selectedFile.toURI().toString());
            ivPreview.setImage(image);
            lbImagePath.setText(selectedFile.getName());
        }
    }

    private String generateImageName(String mealName) {
        // Convertir el nombre de la comida a un formato válido para nombre de archivo
        String fileName = mealName.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");

        // Obtener la extensión del archivo original
        String extension = selectedImageFile.getName().substring(
                selectedImageFile.getName().lastIndexOf(".")).toLowerCase();

        return fileName + extension;
    }

    private String copyImageToProject(String imageName) throws IOException {
        // Crear el directorio de imágenes si no existe
        Path imagesDir = Paths.get("./src/images/");
        if (!Files.exists(imagesDir)) {
            Files.createDirectories(imagesDir);
        }

        // Copiar la imagen al directorio del proyecto
        Path destination = imagesDir.resolve(imageName);
        Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        return "./src/images/" + imageName;
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String errorMessage = validateForm();

        if (errorMessage != null) {
            Utils.notifyAction(lbErrorMessage, errorMessage, Color.RED);
            return;
        }

        String day = cbServiceDay.getValue();
        String type = rbBreakfast.isSelected() ? "Desayuno" : rbLunch.isSelected() ? "Almuerzo" : "";

        String confirmationTitle = "¿Está seguro de agregar un nuevo servicio?";
        String confirmationContent = "Día: " + day + "\nHorario: " +
                (type.equals("breakfast") ? "Desayuno" : "Almuerzo");

        boolean isConfirmed = Utils.showConfirmationAlert(confirmationTitle, confirmationContent);

        if (isConfirmed) {
            try {
                String name = tfName.getText().trim();
                int price = Integer.parseInt(tfPrice.getText().trim());

                // Generar nombre de imagen y copiarla
                String imageName = generateImageName(name);
                String imagePath = copyImageToProject(imageName);

                Meal meal = new Meal(name, price, imagePath);

                //se guarda la comida en el json dependiendo del filtro
                filePath = Logic.getFilePath(day, type);
                Logic.MealsJsonUtils.setFilePath(filePath);
                Logic.MealsJsonUtils.saveElement(meal);


                ClientHandler clientHandler = new ClientHandler();
                String imagePat = meal.getImagePath();
                clientHandler.sendImage(imagePat);
                System.out.println("En agregar: "+imagePat);


                Utils.showAlert(Alert.AlertType.INFORMATION, "Registro Exitoso",
                        "Nombre: " + name + "\nPrecio: ₡" + price);
                Utils.notifyAction(lbErrorMessage, "Comida guardada con éxito", Color.GREEN);
                Logic.closeCurrentWindowAndOpen("/presentation/MealGestor.fxml", ((Stage) btReturn.getScene().getWindow()));
            } catch (NumberFormatException e) {
                Utils.notifyAction(lbErrorMessage, "Error al procesar el precio", Color.RED);
            } catch (IOException e) {
                Utils.notifyAction(lbErrorMessage, "Error al guardar la imagen: " + e.getMessage(), Color.RED);
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

    public void handleReturn(ActionEvent actionEvent) {
        Logic.closeCurrentWindowAndOpen("/presentation/MealGestor.fxml", ((Stage) btReturn.getScene().getWindow()));
    }
}
