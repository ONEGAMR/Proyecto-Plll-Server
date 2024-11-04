package data;

import java.sql.Connection;  // Importa la interfaz Connection
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionB {

    private static final String database = "orders";
    private static final String user = "root";
    private static final String pass = "";
    
    private static final int port = 3306;
    private static final String host = "localhost"; 
    private static final String url = "jdbc:mysql://" + host + ":" + port + "/" + database; 
    
    private static Connection con;

    public static Connection getConnection() {
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

        return con;
    }

    public static boolean testConnection() {
        try {
            // Si ya existe una conexión, verificamos si es válida
            if (con != null && !con.isClosed()) {
                // Intentamos hacer una consulta simple para verificar la conexión
                try (Statement stmt = con.createStatement()) {
                    stmt.execute("SELECT 1");
                   // System.out.println("Conexión existente verificada exitosamente");
                    return true;
                }
            }

            // Si no hay conexión, intentamos crear una nueva
            Connection testCon = DriverManager.getConnection(url, user, pass);
            if (testCon != null) {
                //System.out.println("Nueva conexión establecida exitosamente");
                testCon.close();
                return true;
            }
            return false;

        } catch (SQLException e) {
           // System.err.println("Error al verificar la conexión: " + e.getMessage());
            return false;
        }
    }
}

	
	
	

