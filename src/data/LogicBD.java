package data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import domain.Orders;
import domain.User;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.util.Arrays;


public class LogicBD {

public static Connection cn = ConnectionB.getConnection();
private static final String LLAVE_SECRETA = "UniversidadNacional";
	
	
public static User getUserValidate(String id_person) {
    User user = null;
    try {
        CallableStatement stmt = cn.prepareCall("{call spSearchStaffUser(?)}");
        stmt.setString(1, id_person);  // Pasar el parámetro al procedimiento almacenado

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // Desencriptar
            String passwordOriginal = desencriptar(rs.getString(2));
            user = new User(rs.getString(1), passwordOriginal, rs.getString(3), rs.getString(4));  // id y password, type
        }
    } catch (SQLException e) {
        System.out.println("LogicBD.getUserValidate: " + e.getMessage());
    }

    return user;
}
public static boolean updateUserBD(String user){
    boolean isUpdated = false;

    if(getUserValidate(user.split(",")[1]).getId().equals(user.split(",")[1])){

        try {
            //Encriptar la contraseña
            String passwordEncriptada = encriptar(user.split(",")[9]);

            // Preparar la llamada al procedimiento almacenado
            CallableStatement stmt = cn.prepareCall("{call spUpdateInStaffClients(?, ?, ?, ?)}");
            stmt.setString(1, user.split(",")[1]);  // ID
            stmt.setString(2, passwordEncriptada);  // Pasword
            stmt.setString(3, user.split(",")[10]);  // type 10
            stmt.setString(4, user.split(",")[11]);  // routePhoto 11

            // Ejecutar la consulta de actualización
            int rowsAffected = stmt.executeUpdate();

            // Verificar si se actualizó alguna fila
            if (rowsAffected > 0) {
                isUpdated = true;  // Indicar que la actualización fue exitosa
            }
        } catch (SQLException e) {
            System.out.println("LogicBD.updateUserBD: " + e.getMessage());
        }

    }

    return isUpdated;
}
    public static boolean updateUserBDUs(User user){
        boolean isUpdated = false;

            try {
                //Encriptar la contraseña
                String passwordEncriptada = encriptar(user.getPassword());

                // Preparar la llamada al procedimiento almacenado
                CallableStatement stmt = cn.prepareCall("{call spUpdateInStaffClients(?, ?, ?, ?)}");
                stmt.setString(1, user.getId());  // ID
                stmt.setString(2, passwordEncriptada);  // Pasword
                stmt.setString(3, user.getType());  // type 10
                stmt.setString(4, user.getPhotoRoute());  // routePhoto 11

                // Ejecutar la consulta de actualización
                int rowsAffected = stmt.executeUpdate();

                // Verificar si se actualizó alguna fila
                if (rowsAffected > 0) {
                    isUpdated = true;  // Indicar que la actualización fue exitosa
                }
            } catch (SQLException e) {
                System.out.println("LogicBD.updateUserBDUs: " + e.getMessage());
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
            stmt.setDouble(3, Double.parseDouble(foodOrders[3]));  // total
            stmt.setString(4, "Pendiente"); //estado
            stmt.setString(5, foodOrders[4]);//  id

            // Falta ejecutar la consulta
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("LogicBD.saveOrder: " + e.getMessage());
        }
    }

    private static SecretKeySpec generateKey() {
        try {
            byte[] claveBytes = LLAVE_SECRETA.getBytes("UTF-8");
            // Usar SHA-1 para obtener una clave de longitud fija
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            claveBytes = sha.digest(claveBytes);
            // Usar solo los primeros 16 bytes para AES-128
            claveBytes = Arrays.copyOf(claveBytes, 16);
            return new SecretKeySpec(claveBytes, "AES");
        } catch (Exception e) {
            System.out.println("Error al generar la clave: " + e.getMessage());
            return null;
        }
    }

    // Método para encriptar
    public static String encriptar(String textoPlano) {
        try {
            SecretKeySpec secretKey = generateKey();
            if (secretKey == null) return null;

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] textoCifrado = cipher.doFinal(textoPlano.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(textoCifrado);

        } catch (Exception e) {
            System.out.println("Error al encriptar: " + e.getMessage());
            return null;
        }
    }

    // Método para desencriptar
    public static String desencriptar(String textoCifrado) {
        try {
            SecretKeySpec secretKey = generateKey();
            if (secretKey == null) return null;

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] textoDescifrado = cipher.doFinal(Base64.getDecoder().decode(textoCifrado));
            return new String(textoDescifrado, "UTF-8");

        } catch (Exception e) {
            System.out.println("Error al desencriptar: " + e.getMessage());
            return null;
        }
    }

    public static void saveUser(User user){

        try {
            //Encriptar la contraseña
            String passwordEncriptada = encriptar(user.getPassword());

            // Preparar la llamada al procedimiento almacenado
            CallableStatement stmt = cn.prepareCall("{call spSaveInStaffClients(?, ?, ?, ?)}");
            stmt.setString(1, user.getId());  // id_person
            stmt.setString(2, passwordEncriptada);  // password
            stmt.setString(3, user.getType());  // type
            stmt.setString(4, "Sin foto"); //photoRoute

            // Falta ejecutar la consulta
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("LogicBD.saveUser: " + e.getMessage());
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
                System.out.println("No se encontró el perfil para eliminar.");
            }

        } catch (SQLException e) {
            System.out.println("LogicBD.deleteUser: " + e.getMessage());
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
        ArrayList<Orders> listOrder = new ArrayList<>();
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
                    Orders meal = new Orders(rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));  // nombre, cantidad, total, status
                    listOrder.add(meal);
                }
            }
        } catch (SQLException e) {
            System.out.println("LogicBD.getListOrderClient: " + e.getMessage());
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
                    Orders order = new Orders(rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4), rs.getString(5));  // nombre, cantidad, total, status, image
                    listOrder.add(order);
                }

        } catch (SQLException e) {
            System.out.println("LogicBD.getListOrders: " + e.getMessage());
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
                Orders order = new Orders(rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getString(4), rs.getString(5));  // nombre, cantidad, total, status, id, image
                listOrder.add(order);
            }

        } catch (SQLException e) {
            System.out.println("LogicBD.getListOrdersStatus: " + e.getMessage());
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
                System.out.println("LogicBD.updateOrderBD: " + e.getMessage());
            }

        return isUpdated;
    }

}
