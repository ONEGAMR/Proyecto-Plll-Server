package data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketOrder {

	private static final int PUERTO = 12345;

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
}
