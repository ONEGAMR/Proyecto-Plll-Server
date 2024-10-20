package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import domain.User;

public class LogicServer {

	public static List<String> separarPalabras(String texto) {
        // Usa el método split() para separar por comas
        String[] palabrasArray = texto.split(","); 
        // Convierte el arreglo a una lista y la retorna
        return new ArrayList<>(Arrays.asList(palabrasArray));
    }
	
	public static int validateUser(User user, ArrayList<?> info) {
	    if (user != null) {
	        
	        // Usar equals() para comparar cadenas
	        if (!user.getPassword().equals(info.get(2))) {
	        	System.out.println(info.get(2));
	            return 1;
	        }
	        
	        if (user.getPassword().equals(info.get(2))) {
	            return -10;
	        }
	    }

	    return -1;  // Si no hay coincidencia
	}

}
