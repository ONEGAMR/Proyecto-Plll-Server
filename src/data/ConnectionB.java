package data;

import java.sql.Connection;  // Importa la interfaz Connection
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionB {

    private static final String database = "orders";
    private static final String user = "root";
    private static final String pass = "";
    
    private static final int port = 3306;
    private static final String host = "localhost"; 
    private static final String url = "jdbc:mysql://" + host + ":" + port + "/" + database; 
    
    private static Connection con;  // Cambiar el tipo a Connection

    public static Connection getConnection() {  // Cambiar el tipo de retorno a Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Conectando a la base de datos...");
            con = DriverManager.getConnection(url, user, pass);  // Sin cast
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }

        return con;  // Retornar la conexión
    }
}

	
	
	

