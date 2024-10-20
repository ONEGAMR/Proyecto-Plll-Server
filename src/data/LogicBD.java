package data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import domain.User;


public class LogicBD {

public static Connection cn = ConnectionB.getConnection();
	
	
public static User getUserID(String id_person) {
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

}
