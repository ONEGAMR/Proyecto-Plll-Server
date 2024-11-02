package business;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import data.Logic;
import domain.Meal;

import java.io.IOException;
import java.util.List;

public class ServiceViewGUIController {

	@FXML private ComboBox<String> cbReservationDay;
	@FXML private RadioButton rbBreakfast;
	@FXML private ToggleGroup mealTimeGroup;
	@FXML private RadioButton rbLunch;
	@FXML private TableView<Meal> tvMeals;
	@FXML private TableColumn<Meal, String> colMealName;
	@FXML private TableColumn<Meal, Integer> colPrice;
	@FXML private Label lbEmptyTable;
	@FXML private Button btReturn;
	@FXML private Button btAddMeal;
	@FXML private Button btEdit;
	@FXML private Button btDelete;

	@FXML
	public void initialize() {
		setupComboBoxes();
		setupRadioButtons();
		setupTableColumns();
		setupListeners();
		getSelectedStatus();
	}
	@FXML
	private void editMeal() {

		if(Logic.meal != null){
			generatePath();
			Logic.closeCurrentWindowAndOpen("/presentation/UpdateMeal.fxml", ((Stage) btAddMeal.getScene().getWindow()));
		}else{
			System.out.println("seleccione una meal edit");
		}

	}
	public void generatePath(){
		String selectedDay = cbReservationDay.getValue();
		String selectedMealType = mealTimeGroup.getSelectedToggle() != null ?
				(String) mealTimeGroup.getSelectedToggle().getUserData() : null;


		Logic.filePath = Logic.getFilePath(selectedDay, selectedMealType);
	}

	@FXML
	private void deleteMeal() throws IOException {
		if(Logic.meal != null){
			generatePath();

			Logic.MealsJsonUtils.setFilePath(Logic.filePath);
			Logic.MealsJsonUtils.deleteMeal(Logic.meal.getName());
			updateTable();
			Logic.meal = null;
		}else{
			System.out.println("seleccione una meal delete ");
		}

	}
	// Configura los ComboBox
	private void setupComboBoxes() {

		cbReservationDay.setItems(FXCollections.observableArrayList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"));
	}

	public void getSelectedStatus() {
		tvMeals.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {

				Logic.meal = newValue;
				System.out.println("Meal seleccionada: " + newValue.toStringMealData());
			} else {
				System.out.println("No hay ningun meal seleccionada.");
			}
		});
	}

	// Configura los RadioButton
	private void setupRadioButtons() {
		rbBreakfast.setUserData("Desayuno");
		rbLunch.setUserData("Almuerzo");
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
		System.out.println(selectedDay+selectedMealType+" "+filePath);

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

	@FXML
	public void handleReturnAction(ActionEvent event) {
		Logic.meal = null;
		Logic.closeCurrentWindowAndOpen("/presentation/MainGUI.fxml", ((Stage) btReturn.getScene().getWindow()));
	}

	@FXML
	public void handleAddMealAction(ActionEvent event) {
		Logic.closeCurrentWindowAndOpen("/presentation/ServiceRequest.fxml", ((Stage) btAddMeal.getScene().getWindow()));
	}

}
