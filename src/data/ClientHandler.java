package data;

import domain.Meal;
import domain.Orders;
import domain.Recharge;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private Socket clienteSocket;
    private BufferedReader entrada;
    private PrintWriter salida;

    public ClientHandler(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

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

    // Método para enviar mensajes al cliente
    public  void sendMessage(String message) {
        if (salida != null) {
            salida.println(message);  // Enviar mensaje al cliente
        }
    }

    public void receiveMessages() {
        try {
            String answer;
            while ((answer = entrada.readLine()) != null) {
                ArrayList<String> info = (ArrayList<String>) LogicServer.separarPalabras(answer);

                System.out.println(answer);
                if ("user".equals(info.get(0))) {
                    int validationResult = LogicServer.validateUser(LogicBD.getUserValidate(info.get(1)), info);
                    System.out.println(validationResult);

                    if(LogicBD.getUserValidate(info.get(1)) != null) {
                        if (validationResult == -10) {  // Si la autenticación es exitosa
                            // Primero enviamos todas las imágenes
                            LogicServer.sendAllImages(this);

                            // Luego enviamos la información del usuario
                            LogicServer.message(this, validationResult, info.get(1), info.get(2),
                                    LogicBD.getUserValidate(info.get(1)).getType());

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
                    System.out.println(answer+" para U");
                    sendMessage("us_confirm,"+ LogicServer.updateUser(answer));
                }

                if ("listMeals".equals(info.get(0))) {
                    System.out.println(info.get(0));
                    for(Meal m : LogicServer.getListMeals(answer)){
                        sendMessage("listMeals,"+ m.toStringMealData());
                    }
                }

                if("foodOrder".equals(info.get(0))){
                    System.out.println(info.get(0));
                    LogicServer.saveOrder(answer);
                }

                if("listOrder".equals(info.get(0))){
                    System.out.println(info.get(0) + "dentra a orders");
                    for(Orders o : LogicServer.getListMealsOrderClient(answer)){
                        sendMessage("listOrder,"+ o.toString());
                        System.out.println(o.toString());
                    }
                }

                if("listRecharge".equals(info.get(0))){
                    for(Recharge r : LogicServer.getListRecharges(answer)){
                        sendMessage("listRecharge,"+r.toStringRecharge());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al recibir mensajes del cliente: " + e.getMessage());
        }
    }
}
