package data;

import domain.Meal;

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

    // Método para recibir mensajes del cliente
    public void receiveMessages() {
        try {
            String answer;
            while ((answer = entrada.readLine()) != null) {
                ArrayList<String> info = (ArrayList<String>) LogicServer.separarPalabras(answer);

                System.out.println(answer);
                if ("user".equals(info.get(0))) {
                    int validationResult = LogicServer.validateUser(LogicBD.getUserValidate(info.get(1)), info);
                    System.out.println(validationResult);
                    //Envia lo datos del usuario
                    LogicServer.message(this,validationResult,info.get(1),info.get(2));

                }

                //se recibe la informacion y envia una confirmacion de update
                if ("updateUser".equals(info.get(0))) {
                    System.out.println(answer+" para U");
                    sendMessage("us_confirm,"+ LogicServer.updateUser(answer));
                }

                //Se recibe la ruta de las comidas solicitadas y se envian
                if ("listMeals".equals(info.get(0))) {
                    System.out.println(info.get(0));
                    for(Meal m : LogicServer.getListMeals(answer)){
                        sendMessage("listMeals,"+ m.toStringMealData());
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Error al recibir mensajes del cliente: " + e.getMessage());
        }
    }


}
