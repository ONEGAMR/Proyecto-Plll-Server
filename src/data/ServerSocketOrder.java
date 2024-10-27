package data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerSocketOrder {

	private static final int PUERTO = 12345;
	private static Map<Integer, ClientHandler> clientes = new HashMap<>();

	public static void runServer() {
		// Ejecuta el servidor en un hilo separado para no bloquear la interfaz gráfica
		new Thread(() -> startServer()).start();
	}

	// Método que inicia el servidor y acepta conexiones
	public static void startServer() {
		try (ServerSocket servidorS = new ServerSocket(PUERTO)) {
			System.out.println("Servidor iniciado y esperando conexiones en el puerto " + PUERTO);

			while (true) {
				// Aceptar una conexión de cliente
				Socket clienteSocket = servidorS.accept();
				System.out.println("Cliente conectado desde: " + clienteSocket.getInetAddress());

				// Crear un nuevo hilo para manejar al cliente
				new ClientHandler(clienteSocket).start();
			}
		} catch (IOException e) {
			System.out.println("Error en el servidor: " + e.getMessage());
		}
	}

	//crear un id unico para el cliente, o guardarlo
	public static void addClient(String clientId, ClientHandler clientHandler) {
		clientes.put(Integer.valueOf(clientId), clientHandler);
		System.out.println("Cliente con ID " + clientId + " añadido al servidor.");
	}

	//enviar un mensaje a un cliente en especifico
	public static void sendMessageToClient(String clientId, String message) {
		ClientHandler client = clientes.get(Integer.parseInt(clientId));

		if (client != null) {
			client.sendMessage(message);
			System.out.println("Mensaje enviado al cliente con ID: " + clientId);
		} else {
			System.out.println("Cliente con ID " + clientId + " no encontrado.");
		}
	}

	//enviar un mensaje a todos los clientes conectados
	public static void sendMessageToAll(String message) {
		for (ClientHandler client : clientes.values()) {
			client.sendMessage(message);
		}
		System.out.println("Mensaje enviado a todos los clientes.");
	}
}

