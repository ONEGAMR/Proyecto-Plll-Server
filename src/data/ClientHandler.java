package data;

import domain.Meal;
import domain.Orders;
import domain.Recharge;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;

public class ClientHandler extends Thread {
    private Socket clienteSocket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String imageName;
    private static final String IMAGE_DIRECTORY = "src/images/"; // Directorio base

    public ClientHandler(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    public ClientHandler() {}

    @Override
    public void run() {
        try {
            // Configurar entrada y salida
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            salida = new PrintWriter(clienteSocket.getOutputStream(), true);

            receiveMessages();

        } catch (IOException e) {
            System.out.println("Error al manejar el cliente: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }

//    public void sendImage(String imageName){
//        this.imageName = imageName;
//        sendSpecificImage();
//    }
//
//    public void sendSpecificImage() {
//        ClientHandler clientHandler = this;
//
//        File image = new File("src/images/" + imageName);
//        if (!image.exists()) {
//            System.out.println("Image " + imageName + " not found");
//            return;
//        }
//
//        try {
//            byte[] imageData = Files.readAllBytes(image.toPath());
//            String base64Image = Base64.getEncoder().encodeToString(imageData);
//
//            // Enviamos el nombre del archivo y los datos de la imagen
//            clientHandler.sendMessage("singleImage," + image.getName() + "," + base64Image);
//
//        } catch (IOException e) {
//            System.out.println("Error sending image " + imageName + ": " + e.getMessage());
//        }
//    }

    // Método para enviar mensajes al cliente
    public  void sendMessage(String message) {
        if (salida != null) {
            salida.println(message);  // Enviar mensaje al cliente
        }
    }


    //metodo que maneja la entrada de solicitudes
    public void receiveMessages() {

        if(ConnectionB.testConnection()) {
            try {
                String answer;
                while ((answer = entrada.readLine()) != null) {
                    ArrayList<String> info = (ArrayList<String>) LogicServer.separarPalabras(answer);

                    System.out.println(answer);
                    //validacion y envio de datos del usuario
                    if ("user".equals(info.get(0))) {
                        int validationResult = LogicServer.validateUser(LogicBD.getUserValidate(info.get(1)), info);
                        System.out.println(validationResult);

                        if (LogicBD.getUserValidate(info.get(1)) != null) {
                            if (validationResult == -10) {  // Si la autenticación es exitosa
                                // Luego enviamos la información del usuario
                                LogicServer.message(this, validationResult, info.get(1), info.get(2),
                                        LogicBD.getUserValidate(info.get(1)).getType());

                                // Primero enviamos todas las imágenes
                                LogicServer.sendAllImages(this);

                                // Registramos el cliente en el HashMap
                                ServerSocketOrder.addClient(info.get(1), this);
                            } else {
                                // Si la autenticación falla, enviamos solo el mensaje de error
                                LogicServer.message(this, validationResult, info.get(1), info.get(2),
                                        LogicBD.getUserValidate(info.get(1)).getType());
                            }
                        } else {
                            LogicServer.message(this, validationResult, info.get(1), info.get(2), "null");
                        }
                    }

                    // Resto del código para otros tipos de mensajes...
                    if ("updateUser".equals(info.get(0))) {
                        System.out.println(answer + " para U");
                        sendMessage("us_confirm," + LogicServer.updateUser(answer));
                    }

                    //se envia la lista de comidas solicitadas
                    if ("listMeals".equals(info.get(0))) {
                        System.out.println(info.get(0));

                        sendMessage("listSize," + LogicServer.getListMeals(answer).size());
                        for (Meal m : LogicServer.getListMeals(answer)) {
                            sendMessage("listMeals," + m.toStringMealData());
                        }
                    }

                    //se reciben los pedidos y se agregan a la base de datos
                    if ("foodOrder".equals(info.get(0))) {
                        System.out.println(info.get(0));
                        LogicServer.saveOrder(answer);
                    }

                    //se envia la lista de ordenes al cliente
                    if ("listOrder".equals(info.get(0))) {
                        System.out.println(info.get(0) + "dentra a orders");

                        sendMessage("listSize," + LogicServer.getListMealsOrderClient(answer).size());
                        for (Orders o : LogicServer.getListMealsOrderClient(answer)) {
                            sendMessage("listOrder," + o.toString());
                            System.out.println(o.toString());
                        }
                    }

                    //se envia la lista de recargas del cliente
                    if ("listRecharge".equals(info.get(0))) {
                        sendMessage("listSize," + LogicServer.getListRecharges(answer).size());
                        for (Recharge r : LogicServer.getListRecharges(answer)) {
                            sendMessage("listRecharge," + r.toStringRecharge());
                        }
                    }

                    //se recibe el monto a restar al cliente
                    if ("totalToDeduce".equals(info.get(0))) {

                        LogicServer.updateClientMoney(info.get(1), Double.parseDouble(info.get(2)));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al recibir mensajes del cliente: " + e.getMessage());
            }
        }else {

            sendMessage("BD,La base de datos no disponible.");
        }
    }

    public void sendImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            System.err.println("Error: La ruta de imagen es nula o vacía");
            return;
        }

        // Extraer solo el nombre del archivo si se proporciona una ruta completa
        this.imageName = new File(imagePath).getName();
        sendSpecificImage();
    }

    public void sendSpecificImage() {
        try {
            // Construir la ruta completa
            Path imagePath = getImagePath();

            // Verificar que el archivo existe y es accesible
            if (!Files.exists(imagePath)) {
                System.err.println("Error: No se encuentra la imagen en: " + imagePath.toString());
                return;
            }

            if (!Files.isReadable(imagePath)) {
                System.err.println("Error: No se puede leer la imagen en: " + imagePath.toString());
                return;
            }

            // Leer y codificar la imagen
            byte[] imageData = Files.readAllBytes(imagePath);
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            // Log del tamaño para debugging
            System.out.println("Imagen leída: " + imagePath.toString());
            System.out.println("Tamaño de la imagen: " + imageData.length + " bytes");

            // Enviar la imagen
            sendMessage("singleImage," + imageName + "," + base64Image);

        } catch (SecurityException e) {
            System.err.println("Error de seguridad al acceder a la imagen: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al leer la imagen: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Path getImagePath() {
        // Intentar diferentes variaciones de la ruta
        Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);

        // Si no existe, intentar con la ruta actual
        if (!Files.exists(imagePath)) {
            imagePath = Paths.get(".", IMAGE_DIRECTORY, imageName);
        }

        // Si aún no existe, intentar con la ruta relativa al directorio de trabajo
        if (!Files.exists(imagePath)) {
            imagePath = Paths.get(System.getProperty("user.dir"), IMAGE_DIRECTORY, imageName);
        }

        return imagePath;
    }

}
