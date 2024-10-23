package data;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import domain.Meal;
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
	public static void message(ClientHandler clientHandler,int validationResult, String carnet, String password){

		if(validationResult != -10) {
			clientHandler.sendMessage("user," + validationResult);
		}else{
			clientHandler.sendMessage("user," + validationResult + "," +StudentData.searchStudent(carnet).toStringUserData()+","+password);
			System.out.println(StudentData.searchStudent(carnet).toStringUserData());

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

	public static List<Meal> getListMeals(String route){
		String[] routeParts = route.split(",");
		String filePath = Logic.getFilePath(routeParts[1], routeParts[2]);
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

}
