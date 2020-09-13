package pc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Server extends Thread {

	public static String hostIP;
	public static ArrayList<Socket> connectedClients;

	public static void main(String[] args) {

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		connectedClients = new ArrayList<Socket>();

		// Grab IP of server host
		hostIP = Data.grabIP();
		if ("-1".equals(hostIP)) {
			print("Could not find current host device's IP, possibly internet connection problem.");
			System.exit(-1);
		} else {
			print("Host IP is: " + hostIP);
		}

		// Start accepting clients with clientAcceptor
		clientAcceptor();
		while (true) { // This is for the server commands
			switch (sc.nextLine()) {
			case "list":
				print(connectedClients);
			}
		}
	}

	// Constantly accepts new client sockets
	public static void clientAcceptor() {
		ServerSocket serverSocket = null;
		Socket client;

		try {
			serverSocket = new ServerSocket(Data.port); // Open port in server
			print("Server online");
		} catch (IOException e1) {
			print("Error is starting server, please retry after full exit. Else, port 888 has already been occupied"); // Usually
																														// server
																														// is
																														// occupied
			System.exit(-1);
		}
		while (!serverSocket.isClosed()) { // Constantly accept new clients until server socket is closed
			try {

				client = serverSocket.accept(); // Accept method and assign the socket to client
				connectedClients.add(client);
				clientHandler(client);

			} catch (Exception e) {
				print("Failed to accept a client");
			}
		}
	}

	// This is our connection between our client, server, and eventually other
	// clients. Each time this function is called, a new thread is created
	public static void clientHandler(Socket client) {
		Thread handler = new Thread() {

			public boolean connected = true;
			public InputStreamReader isr;
			public BufferedReader br;
			public PrintWriter serverWriter; // This is used to write to connect to all the clients and send messages to
												// them
			public PrintWriter clientWriter; // This is used to write to the handler's respective client

			@Override
			public void run() {
				while (connected) { // While client is still connected

					String input = readClient(); // Keeps reading and then sending to all client

					if (input == null) { // null caught from readClient means user has disconnected while trying to read input
						disconnect(0);
					} 
					else {
						if ("\\".equals(input.split("")[0])) {
							sendToClient("Command received");
						} 
						else {
							sendToAll(input);
						}
					}

				}
				// If the while loop ever stops, then it just ends
			}

			public String readClient() {
				try {
					isr = new InputStreamReader(client.getInputStream()); // Find input stream of client
					br = new BufferedReader(isr); // Buffered reader to read the input stream
					String input = br.readLine(); // Read the actual line
					return input;
				} catch (Exception e) { // This is almost always the most default way of disconnecting
					return null;
				}
			}

			public void sendToAll(Object object) {

				for (int i = 0; i < connectedClients.size(); i++) { // Find how many clients are connected and loops how
																	// many times
					try {
						serverWriter = new PrintWriter(connectedClients.get(i).getOutputStream());
						serverWriter.println(object);
						serverWriter.flush();
					} catch (IOException ioe) {
						System.out.println("Failed to sending to " + connectedClients.get(i));
					}
				}
			}

			public void sendToClient(Object object) {
				try {
					clientWriter = new PrintWriter(client.getOutputStream());
					clientWriter.println(object);
					clientWriter.flush();
				} catch (IOException ioe) {

				}
			}

			public void disconnect(int status) {
				connected = false;
				Server.connectedClients.removeAll(Collections.singleton(client));
				try {
					client.close();
				} catch (IOException e) {

				}
				if (status == 0) { // Default exit
					sendToAll(client + " has disconnected");
				} else if (status == -1) {
					sendToAll(client + " has disconnected (Error)");
				}

			}
		};

		// In case client forcefully disconnected such as closing window
		handler.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {

			}
		});

		handler.start();
	}

	public static void print(Object object) {
		System.out.println(object);
	}
}
