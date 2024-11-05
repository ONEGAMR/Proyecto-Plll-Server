package data;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import domain.Orders;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import domain.Meal;
import domain.Recharge;
import domain.Student;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class Logic {

	private static final String BASE_PATH = "./src/files/";
	private static final String[] DAYS = {"monday", "tuesday", "wednesday", "thursday", "friday"};
	private static final String[] MEALS = {"breakfast", "lunch"};
	private static final String FILE_NAME = "./src/files/student_recharges.json";
	public static Orders order;
	public static Meal meal;
	public static final JSONUtils<Meal> MealsJsonUtils = new JSONUtils<>();
	public static final JSONUtils<Recharge> RechargesJsonUtils = new JSONUtils<>(FILE_NAME);
	private static final String IMAGE_DIRECTORY = "src/images/";
	public static Student currentStudent = new Student();
	public static String filePath;
	public static String status;
	public static String date;

	// Getter para obtener el estudiante actualmente seleccionado
	public static Student getCurrentStudent() {
		return currentStudent;
	}

	// Setter para establecer el estudiante actualmente seleccionado
	public static void setCurrentStudent(Student student) {
		Logic.currentStudent = student;
	}

	// Genera la ruta del archivo JSON basado en el día y tipo de comida
	public static String getFilePath(String day, String mealType) {
		String dayFile = switch (day) {
		case "Lunes" -> DAYS[0];
		case "Martes" -> DAYS[1];
		case "Miércoles" -> DAYS[2];
		case "Jueves" -> DAYS[3];
		case "Viernes" -> DAYS[4];
		default -> null;
		};

		String mealDay = switch (mealType) {
		case "Desayuno" -> MEALS[0];
		case "Almuerzo" -> MEALS[1];
		default -> null;
		};

		return (dayFile != null && mealDay != null) ? BASE_PATH + dayFile + "_" + mealDay + ".json" : null;
	}
	
	// Cierra la ventana actual y abre una nueva ventana con la vista especificada
	public static void closeCurrentWindowAndOpen(String fxmlPath, Stage currentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(Logic.class.getResource(fxmlPath));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.show();
			currentStage.close();
		} catch (IOException e) {
			Utils.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista.");
		}
	}

	// Verifica si el saldo está dentro del rango permitido
	public static boolean isValidBalance(double balance, double minBalance, double maxBalance) {
		return balance >= minBalance && balance <= maxBalance;
	}

	// Verifica si un ID de estudiante es válido (existe en la lista de estudiantes)
	public static boolean validateStudentID(String id) {
		return StudentData.getStudentList().stream()
				.anyMatch(student -> student.getCarnet().equals(id));
	}

	// Encuentra un estudiante por su ID
	public static Student findStudentByID(String id) {
		return StudentData.getStudentList().stream()
				.filter(student -> student.getCarnet().equals(id))
				.findFirst()
				.orElse(null);
	}

    // Método para configurar el formato de DatePicker
    public static void configureDatePicker(DatePicker datePicker) {
        datePicker.setPromptText("dd/MM/yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
            }
        });
    }
    
    // Método para formatear LocalDate en formato "dd/MM/yyyy"
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "No Date";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
    //Validacion de fecha
    public static String validateDateNotInFuture(LocalDate date) {
        if (date == null) {
            return "La fecha no puede estar vacía";
        }
        if (date.isAfter(LocalDate.now())) {
            return "La fecha no puede ser posterior a la fecha actual";
        }
        return null;
    }

	/// Método que guarda el filtro de confirmación
	public static boolean fillList(ComboBox<String> select) {
		if (status != null && select.getItems().contains(status)) {
			select.getSelectionModel().select(status);
			return true;
		}
		return false;
	}

	public static void showPopupMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Mensaje");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static boolean deleteSpecificImage(String imagePath) {
		try {
			// Limpiamos la ruta de caracteres extra si los hay
			imagePath = imagePath.replace("./", "");

			// Crear el objeto Path
			Path path = Paths.get(imagePath);

			// Verificar que el archivo existe
			if (!Files.exists(path)) {
				System.err.println("Error: No se encuentra la imagen a eliminar en: " + path.toString());
				// Intentar con ruta absoluta
				path = Paths.get(System.getProperty("user.dir"), imagePath);
				if (!Files.exists(path)) {
					System.err.println("Error: Tampoco se encuentra en ruta absoluta: " + path.toString());
					return false;
				}
			}

			// Verificar permisos de escritura
			if (!Files.isWritable(path)) {
				System.err.println("Error: No hay permisos para eliminar la imagen en: " + path.toString());
				return false;
			}

			// Eliminar el archivo
			Files.delete(path);

			System.out.println("Imagen eliminada exitosamente: " + path.toString());
			return true;

		} catch (SecurityException e) {
			System.err.println("Error de seguridad al eliminar la imagen: " + e.getMessage());
			return false;
		} catch (IOException e) {
			System.err.println("Error al eliminar la imagen: " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.err.println("Error inesperado al eliminar: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

}
