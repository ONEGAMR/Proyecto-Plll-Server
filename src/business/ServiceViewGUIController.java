package business;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import data.Logic;
import data.StudentData;
import data.Utils;
import domain.Meal;
import domain.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceViewGUIController {

	@FXML private ComboBox<Student> cbStudent;
	@FXML private ComboBox<String> cbReservationDay;
	@FXML private RadioButton rbBreakfast;
	@FXML private ToggleGroup mealTimeGroup;
	@FXML private RadioButton rbLunch;
	@FXML private TableView<Meal> tvMeals;
	@FXML private TableColumn<Meal, String> colMealName;
	@FXML private TableColumn<Meal, Integer> colPrice;
	@FXML private TableColumn<Meal, Button> colRequest;
	@FXML private Label lbEmptyTable;
	@FXML private Button btReturn;
	@FXML private Button btAddMeal;

	@FXML
	public void initialize() {
		setupComboBoxes();
		setupRadioButtons();
		setupTableColumns();
		setupListeners();
	}

	// Configura los ComboBox
	private void setupComboBoxes() {
		// Obtiene la lista de estudiantes
		List<Student> studentList = StudentData.getStudentList();

		// Crea una lista para los estudiantes activos
		List<Student> activeStudents = new ArrayList<>();

		for (Student student : studentList) {
			if (student.isEstaActivo()) {
				activeStudents.add(student);
			}
		}

		// Establece la lista filtrada en el ComboBox
		cbStudent.setItems(FXCollections.observableArrayList(activeStudents));

		cbStudent.setConverter(new StringConverter<>() {
			@Override
			public String toString(Student student) {
				return student != null ? student.getCarnet() + " " + student.getNombre() + " - ₡" + student.getDineroDisponible() : "";
			}

			@Override
			public Student fromString(String string) {
				return null;
			}
		});

		cbReservationDay.setItems(FXCollections.observableArrayList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"));
	}




	// Configura los RadioButton
	private void setupRadioButtons() {
		rbBreakfast.setUserData("breakfast");
		rbLunch.setUserData("lunch");
	}

	// Configura las columnas de la tabla
	private void setupTableColumns() {
		colMealName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		colPrice.setCellFactory(column -> new TableCell<>() {
			@Override
			protected void updateItem(Integer price, boolean empty) {
				super.updateItem(price, empty);
				setText(empty || price == null ? null : "₡" + price);
			}
		});
		colRequest.setCellFactory(tc -> new TableCell<>() {
			final Button btn = new Button("Solicitar Comida");
			{
				btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black; -fx-font-weight: normal; -fx-background-radius: 4;"
						+ " -fx-pref-height: 20; -fx-pref-width: 120;");
				btn.setOnAction(event -> {
					Meal meal = getTableView().getItems().get(getIndex());
					handleMealRequest(meal);
				});
			}

			@Override
			protected void updateItem(Button item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : btn);
			}
		});
	}

	// Configura los escuchadores de eventos
	private void setupListeners() {
		cbReservationDay.setOnAction(e -> updateTable());
		mealTimeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateTable());
	}

	// Actualiza la tabla de comidas según los filtros seleccionados
	private void updateTable() {
		String selectedDay = cbReservationDay.getValue();
		String selectedMealType = mealTimeGroup.getSelectedToggle() != null ?
				(String) mealTimeGroup.getSelectedToggle().getUserData() : null;

		if (selectedDay == null || selectedMealType == null) {
			tvMeals.setItems(FXCollections.observableArrayList());
			lbEmptyTable.setVisible(true);
			return;
		}

		//se obtiene la ruta para las comidas
		String filePath = Logic.getFilePath(selectedDay, selectedMealType);

		if (filePath != null) {
			Logic.MealsJsonUtils.setFilePath(filePath);
			System.out.println(filePath);
			try {
				List<Meal> meals = Logic.MealsJsonUtils.getElements(Meal.class);
				tvMeals.setItems(FXCollections.observableArrayList(meals));
				lbEmptyTable.setVisible(meals.isEmpty());
			} catch (IOException e) {
				e.printStackTrace();
				lbEmptyTable.setText("Error al cargar los datos.");
				lbEmptyTable.setVisible(true);
			}
		} else {
			tvMeals.setItems(FXCollections.observableArrayList());
			lbEmptyTable.setVisible(true);
		}
	}

	// Maneja la solicitud de una comida
	private void handleMealRequest(Meal meal) {
		Student selectedStudent = cbStudent.getValue();
		if (selectedStudent == null) {
			Utils.showAlert(AlertType.ERROR, "Error", "Por favor, selecciona un estudiante.");
			return;
		}

		double availableMoney = selectedStudent.getDineroDisponible();
		int mealPrice = meal.getPrice();

		if (availableMoney < mealPrice) {
			Utils.showAlert(AlertType.INFORMATION, "Saldo Insuficiente", "No tienes suficiente dinero para solicitar esta comida.");
			return;
		}

		boolean isConfirmed = Utils.showConfirmationAlert(
				"¿Deseas solicitar esta comida?",
				"Confirmación de Compra"
				);

		if (isConfirmed) {
			selectedStudent.setDineroDisponible(availableMoney - mealPrice);
			StudentData.updateStudent(selectedStudent);

			cbStudent.setItems(FXCollections.observableArrayList(StudentData.getStudentList()));
			cbStudent.setValue(null);

			Utils.showAlert(AlertType.INFORMATION, "Pedido Confirmado", "Pedido realizado con éxito. Nuevo saldo: ₡" + selectedStudent.getDineroDisponible());
		}
	}

	@FXML
	public void handleReturnAction(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/MainGUI.fxml", ((Stage) btReturn.getScene().getWindow()));
	}

	@FXML
	public void handleAddMealAction(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/ServiceRequestGUI.fxml", ((Stage) btAddMeal.getScene().getWindow()));
	}

}
