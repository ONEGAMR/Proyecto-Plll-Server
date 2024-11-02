package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import domain.Meal;
import domain.Recharge;
import domain.Student;
import domain.User;
import javafx.collections.FXCollections;

public class LogicServer {

	//metodo para separar por "," el mensaje
	public static List<String> separarPalabras(String texto) {
        // Usa el método split() para separar por comas
        String[] palabrasArray = texto.split(","); 
        // Convierte el arreglo a una lista y la retorna
        return new ArrayList<>(Arrays.asList(palabrasArray));
    }

	//Envia los datps mediante un string anidado dependiendo del resultado
	public static void message(ClientHandler clientHandler,int validationResult, String carnet, String password, String type){

		if(validationResult != -10) {

			clientHandler.sendMessage("user," + validationResult);
		}else{
			clientHandler.sendMessage("user," + validationResult + "," +StudentData.searchStudent(carnet).toStringUserData()+","+password+","+type+ ","+
					LogicBD.getUserValidate(carnet).getPhotoRoute());
			System.out.println(StudentData.searchStudent(carnet).toStringUserData() + " message datos en LOgicServer");
		}
	}

	//Valida si el usuario es correcto por id y password
	public static int validateUser(User user, ArrayList<?> info) {
	    if (user != null) {
	        
	        // Usar equals() para comparar cadenas
			//si no esta correcta
	        if (!user.getPassword().equals(info.get(2))) {
	        	System.out.println(info.get(2));
	            return 1;
	        }
	        //si esta correcta la contraseña
	        if (user.getPassword().equals(info.get(2))) {
	            return -10;
	        }
	    }

	    return -1;  // Si no hay coincidencia
	}

	//verifica si se realizo la actualizacion del usuario
	public static boolean updateUser(String user){
		Student s = new Student();
		s.setCarnet(user.split(",")[1]);
		s.setNombre(user.split(",")[2]);
		s.setCorreoElectronico(user.split(",")[3]);
		s.setTelefono(Integer.parseInt(user.split(",")[4]));
		s.setEstaActivo(Boolean.parseBoolean(user.split(",")[5]));
		s.setFechaIngreso(LocalDate.parse(user.split(",")[6]));
		s.setGenero(user.split(",")[7].charAt(0));
		s.setDineroDisponible(Double.parseDouble(user.split(",")[8]));

		System.out.println(s.toStringUserData()+" updateUser");
		if(StudentData.updateStudent(s) && LogicBD.updateUserBD(user)){
			return true;
		};

		return false;
	}

	public static List<Recharge> getListRecharges(String idStudent) {
		String[] routeParts = idStudent.split(",");
		List<Recharge> recharges = new ArrayList<>();

		try {
			List<Recharge> temp = Logic.RechargesJsonUtils.getElements(Recharge.class);

			for(Recharge r : temp){

				if(r.getCarnet().equals(routeParts[1])){
					recharges.add(r);
				}
			}

		} catch (IOException e) {
			System.out.println("Error al cargar los datos." + e.getMessage());
		}

		return recharges;
	}

	public static List<Meal> getListMeals(String route){
		String[] routeParts = route.split(",");
		String filePath = Logic.getFilePath(routeParts[1], routeParts[2]);
		System.out.println(filePath);
		List<Meal> meals = new ArrayList<>();

		if (filePath != null) {
			Logic.MealsJsonUtils.setFilePath(filePath);
			try {
				meals = Logic.MealsJsonUtils.getElements(Meal.class);

			} catch (IOException e) {
				System.out.println("Error al cargar los datos." + e.getMessage());
			}
		}else{

			System.out.println("No se pudo encontrar el archivo.");
		}
		return meals;
	}



	public static List<Meal> getListMealsOrderClient(String listOrderClient){
		String[] routeParts = listOrderClient.split(",");
		List<Meal> meals = new ArrayList<>();

		if(routeParts[2].equals("Todos")){
			System.out.println(routeParts[2]);

			meals = (List<Meal>) LogicBD.getListOrderClient(routeParts[1], "Todos");
		}

		if(routeParts[2].equals("Pendiente")){
			System.out.println(routeParts[2]);

			meals = (List<Meal>) LogicBD.getListOrderClient(routeParts[1], "Pendiente");
		}

		if(routeParts[2].equals("Preparando")){
			System.out.println(routeParts[2]);

			meals = (List<Meal>) LogicBD.getListOrderClient(routeParts[1], "Preparando");
		}

		if(routeParts[2].equals("Listo")){
			System.out.println(routeParts[2]);

			meals = (List<Meal>) LogicBD.getListOrderClient(routeParts[1], "Listo");
		}

		if(routeParts[2].equals("Entregado")){
			System.out.println(routeParts[2]);

			meals = (List<Meal>) LogicBD.getListOrderClient(routeParts[1], "Entregado");
		}

		return meals;
	}

	public static void saveOrder(String order){

		LogicBD.saveOrder(order);
		StudentData.updateMoney(order.split(",")[4],Double.parseDouble(order.split(",")[3]));
	}

	public static void sendAllImages(ClientHandler clientHandler) {
		File imageDirectory = new File("src/images");
		if (!imageDirectory.exists() || !imageDirectory.isDirectory()) {
			System.out.println("Directory src/images not found");
			return;
		}

		File[] images = imageDirectory.listFiles((dir, name) ->
				name.toLowerCase().endsWith(".jpg") ||
						name.toLowerCase().endsWith(".png") ||
						name.toLowerCase().endsWith(".jpeg")
		);

		if (images != null) {
			// Primero enviamos la cantidad de imágenes que se van a transferir
			clientHandler.sendMessage("imageCount," + images.length);

			for (File image : images) {
				try {
					byte[] imageData = Files.readAllBytes(image.toPath());
					String base64Image = Base64.getEncoder().encodeToString(imageData);

					// Enviamos el nombre del archivo y los datos de la imagen
					clientHandler.sendMessage("image," + image.getName() + "," + base64Image);

					// Pequeña pausa para evitar sobrecarga
					Thread.sleep(100);

				} catch (IOException | InterruptedException e) {
					System.out.println("Error sending image " + image.getName() + ": " + e.getMessage());
				}
			}
		}
	}

	public static void sendSpecificImage(ClientHandler clientHandler, String imageName) {
		File image = new File("src/images/" + imageName);
		if (!image.exists()) {
			System.out.println("Image " + imageName + " not found");
			return;
		}

		try {
			byte[] imageData = Files.readAllBytes(image.toPath());
			String base64Image = Base64.getEncoder().encodeToString(imageData);

			// Enviamos el nombre del archivo y los datos de la imagen
			clientHandler.sendMessage("singleImage," + image.getName() + "," + base64Image);

		} catch (IOException e) {
			System.out.println("Error sending image " + imageName + ": " + e.getMessage());
		}
	}

}
