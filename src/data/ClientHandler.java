package data;

import domain.Meal;
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
                    if(LogicBD.getUserValidate(info.get(1)) != null) {

                        System.out.println("hola");
                        System.out.println(LogicBD.getUserValidate(info.get(1)).getType().toString() + " en user enviar");
                        //se envia la informacion del cliente
                        LogicServer.message(this, validationResult, info.get(1), info.get(2), LogicBD.getUserValidate(info.get(1)).getType());

                        // Usar el carnet como clientId y guardar el cliente en el HashMap
                        ServerSocketOrder.addClient(info.get(1), this);
                    }else{

                        LogicServer.message(this, validationResult, info.get(1), info.get(2), "null");
                    }

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

                //se guarda una orden del cliente
                if("foodOrder".equals(info.get(0))){
                    System.out.println(info.get(0));

                    LogicServer.saveOrder(answer);
                }

                //se envia el menu dependiendo del filtro solicitado
                if("listOrder".equals(info.get(0))){
                    System.out.println(info.get(0) + "dentra a orders");

                    for(Meal m : LogicServer.getListMealsOrderClient(answer)){
                        sendMessage("listOrder,"+ m.toStringMealOrder());
                        System.out.println(m.toStringMealOrder());
                    }

                }

                //se envian las recargas registradas del cliente
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
