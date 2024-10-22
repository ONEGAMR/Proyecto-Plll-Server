package data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import domain.User;


public class LogicBD {

public static Connection cn = ConnectionB.getConnection();
	
	
public static User getUserValidate(String id_person) {
    User user = null;  // Inicializar la variable user
    try {
        CallableStatement stmt = cn.prepareCall("{call spSearchStaffUser(?)}");
        stmt.setString(1, id_person);  // Pasar el parámetro al procedimiento almacenado

        ResultSet rs = stmt.executeQuery();

        // Mover el cursor al primer resultado
        if (rs.next()) {
            // Acceder a los valores solo si hay resultados
            user = new User(rs.getString(1), rs.getString(2));  // id y password
        }
    } catch (SQLException e) {
        System.out.println("LogicBD.getUserID: " + e.getMessage());
    }
    return user;
}
public static boolean updateUserBD(String user){
    boolean isUpdated = false;

    if(getUserValidate(user.split(",")[1]).getId().equals(user.split(",")[1])){

        try {
            // Preparar la llamada al procedimiento almacenado
            CallableStatement stmt = cn.prepareCall("{call spUpdateInStaffClients(?, ?, ?, ?)}");
            stmt.setString(1, user.split(",")[1]);  // ID
            stmt.setString(2, user.split(",")[9]);  // Pasword
            stmt.setString(3, null);  // type 10
            stmt.setString(4, null);  // routePhoto 11

            // Ejecutar la consulta de actualización
            int rowsAffected = stmt.executeUpdate();

            // Verificar si se actualizó alguna fila
            if (rowsAffected > 0) {
                isUpdated = true;  // Indicar que la actualización fue exitosa
            }
        } catch (SQLException e) {
            System.out.println("LogicBD.updateUserPassword: " + e.getMessage());
        }

    }

    return isUpdated;
}

}
