package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSocketOrder {

	private static final int PUERTO = 12345;
	private static Socket clienteSocket;
	private static BufferedReader entrada;
	private static PrintWriter salida;
	
	
	// Método que inicia el hilo para conectar el servidor
	public static void connectToServer() {
	    
	    new Thread(() -> { initThread(); receiveMessages(); }).start();
	}

	// Método principal donde está toda la lógica del servidor
	private static void initThread() {
	    try (ServerSocket servidorS = new ServerSocket(PUERTO)) {
	        System.out.println("Servidor iniciado y esperando conexiones en el puerto " + PUERTO);
	        clienteSocket = servidorS.accept();  // Esperar conexión de un cliente
	        entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
	        salida = new PrintWriter(clienteSocket.getOutputStream(), true);
	        
	        System.out.println("Cliente conectado desde: " + clienteSocket.getInetAddress());
	        
	       //salida.println("127.0.0.1@Adan@Hola&");  // Enviar mensaje al cliente
	    } catch (IOException e) {
	        System.out.println("Error en el servidor: " + e.getMessage());
	    }
	}
	
	public static void sendMessage(String message) {
        if (salida != null) {
            salida.println(message);  // Enviar mensaje al servidor
        }
    }
	
	
	public static void receiveMessages() {
	    try {
	        String answer;
	        while ((answer = entrada.readLine()) != null) {
	            ArrayList<String> info = (ArrayList<String>) LogicServer.separarPalabras(answer);
	            
	            System.out.println(answer);
	            // Cambiar == por equals() para comparar cadenas
	            if ("user".equals(info.get(0))) {
	            	
	            	
	            	
	                // Asegúrate de que el método getUserID() retorne un User válido
	                int validationResult = LogicServer.validateUser(LogicBD.getUserID(info.get(1)), info);
	                System.out.println(validationResult);
	                sendMessage("user," + validationResult);
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error al recibir mensajes del servidor: " + e.getMessage());
	    }
	}

	
	

}
