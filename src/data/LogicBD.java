package data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import domain.Meal;
import domain.Orders;
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
            user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));  // id y password, type
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
            stmt.setString(3, user.split(",")[10]);  // type 10
            stmt.setString(4, user.split(",")[11]);  // routePhoto 11

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

    public static void saveOrder(String order){
        String[] foodOrders = order.split(",");
        try {
            // Preparar la llamada al procedimiento almacenado
            CallableStatement stmt = cn.prepareCall("{call spSaveOrder(?, ?, ?, ?, ?)}");
            stmt.setString(1, foodOrders[1]);  // nameP
            stmt.setInt(2, Integer.parseInt(foodOrders[2]));  // cantidad
            stmt.setInt(3, Integer.parseInt(foodOrders[3]));  // total
            stmt.setString(4, "Pendiente"); //estado
            stmt.setString(5, foodOrders[4]);//  id

            // Falta ejecutar la consulta
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("LogicBD.updateUserPassword: " + e.getMessage());
        }
    }

    public static void saveUser(User user){

        try {
            // Preparar la llamada al procedimiento almacenado
            CallableStatement stmt = cn.prepareCall("{call spSaveInStaffClients(?, ?, ?, ?)}");
            stmt.setString(1, user.getId());  // id_person
            stmt.setString(2, user.getPassword());  // password
            stmt.setString(3, user.getType());  // type
            stmt.setString(4, "Sin foto"); //photoRoute

            // Falta ejecutar la consulta
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("LogicBD.updateUserPassword: " + e.getMessage());
        }
    }

    public static void deleteUser(String id_person){

        try {
            // Preparar la llamada al procedimiento almacenado
            CallableStatement stmt = cn.prepareCall("{call spDeleteInStaffClients(?)}");
            stmt.setString(1, id_person);  // nameP

            // Ejecutar la consulta
            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("User eliminado correctamente.");
            } else {
                System.out.println("No se encontró el pedido para eliminar.");
            }

        } catch (SQLException e) {
            System.out.println("LogicBD.deleteOrder: " + e.getMessage());
        }
    }
    public static void deleteOrder(Orders order){

        try {
            // Preparar la llamada al procedimiento almacenado
            CallableStatement stmt = cn.prepareCall("{call spDeleteOrder(?, ?, ?, ?)}");
            stmt.setString(1, order.getName());  // nameP
            stmt.setInt(2, order.getCantidad());  // cantidad
            stmt.setString(3, order.getStatus());  // status
            stmt.setString(4, order.getIdStudent()); //carnet


            // Ejecutar la consulta
            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("Pedido eliminado correctamente.");
            } else {
                System.out.println("No se encontró el pedido para eliminar.");
            }

        } catch (SQLException e) {
            System.out.println("LogicBD.deleteOrder: " + e.getMessage());
        }
    }

    public static ArrayList<?> getListOrderClient(String studentId, String status) {
        ArrayList<Meal> listOrder = new ArrayList<>();
        CallableStatement stmt = null;
        try {

            if(status.equals("Todos")) {
                stmt = cn.prepareCall("{call spListOrdersID(?)}");
                stmt.setString(1, studentId); // Pasar el parámetro al procedimiento almacenado
            }

            if(status.equals("Preparando") || status.equals("Pendiente") || status.equals("Entregado") || status.equals("Listo")) {
                stmt = cn.prepareCall("{call spListOrdersStatusID(?, ?)}");
                stmt.setString(1, studentId);
                stmt.setString(2, status); // Pasar el parámetro al procedimiento almacenado
            }

            if(stmt != null) {
                ResultSet rs = stmt.executeQuery();

                // Usar while para recorrer todos los resultados
                while (rs.next()) {
                    // Acceder a los valores solo si hay resultados
                    Meal meal = new Meal(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4));  // nombre, cantidad, total, status
                    listOrder.add(meal);
                }
            }
        } catch (SQLException e) {
            System.out.println("LogicBD.getUserID: " + e.getMessage());
        }
        return listOrder;
    }

    public static ArrayList<Orders> getListOrders() {
        ArrayList<Orders> listOrder = new ArrayList<>();
        try {
                CallableStatement stmt = cn.prepareCall("{call spListOrders()}");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Acceder a los valores solo si hay resultados
                    Orders order = new Orders(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5));  // nombre, cantidad, total, status
                    listOrder.add(order);
                }

        } catch (SQLException e) {
            System.out.println("LogicBD.getUserID: " + e.getMessage());
        }
        return listOrder;
    }

    public static ArrayList<Orders> getListOrdersStatus(String status) {
        ArrayList<Orders> listOrder = new ArrayList<>();
        try {
            CallableStatement stmt = cn.prepareCall("{call spListOrdersStatus(?)}");
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Acceder a los valores solo si hay resultados
                Orders order = new Orders(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5));  // nombre, cantidad, total, status, id
                listOrder.add(order);
            }

        } catch (SQLException e) {
            System.out.println("LogicBD.getUserID: " + e.getMessage());
        }
        return listOrder;
    }

    public static boolean updateOrderBD(Orders order){
        boolean isUpdated = false;

            try {
                // Preparar la llamada al procedimiento almacenado
                CallableStatement stmt = cn.prepareCall("{call spUpdateStatus(?, ?, ?, ?)}");
                stmt.setString(1, order.getName());  // name
                stmt.setInt(2, order.getCantidad());  // number
                stmt.setString(3, order.getStatus());  // status
                stmt.setString(4, order.getIdStudent());  // carnet

                // Ejecutar la consulta de actualización
                int rowsAffected = stmt.executeUpdate();

                // Verificar si se actualizó alguna fila
                if (rowsAffected > 0) {
                    isUpdated = true;  // Indicar que la actualización fue exitosa
                }
            } catch (SQLException e) {
                System.out.println("LogicBD.spUpdateStatus: " + e.getMessage());
            }

        return isUpdated;
    }

}
