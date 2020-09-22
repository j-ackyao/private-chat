package pc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client extends Thread {

	private static Socket socket; // Our socket (connection) to the whatever server
	private static Scanner sc;
	private static String username;
	private static boolean connected = true;
	private static ClientWindow clientWindow;
	
	public static void main(String[] args) throws IOException {
		
		clientWindow = new ClientWindow();
		
		// Receive host ip
		clientWindow.print("Insert server IP. 'test' for testing server. 'local' to connect to device's current IP");
		boolean insertHost = false;
		while (!insertHost) { // Loop to prevent empty input
			String input = clientWindow.awaitNextInput();
			
			if (!input.isEmpty()) { // If not empty
				
				if ("test".equals(input)) { // Set IP to testing server
					clientWindow.print("Connecting to testing server...");
					insertHost = connect(Data.testServer);
				} 
				
				else if ("local".equals(input)) { // Set IP to device IP
					String ip = Data.grabIP(); // Grabs IP of local device
					
					if ("-1".equals(ip)) { // This is when it failed to get the IP
						clientWindow.print("Failed to find device's IP, possible internet connection problem");
						System.exit(-1);
					} 
					
					else { // Set to desired IP
						clientWindow.print("Connecting to " + ip + "...");
						insertHost = connect(ip);
					}
				} 
				
				else { // Set IP to inputed IP
					clientWindow.print("Connecting to " + input + "...");
					insertHost = connect(input);
				}
			}
			
			else { // In case input is empty
				clientWindow.print("Input empty, please insert a valid IP");
			}
		}

		clientWindow.print("Insert username");
		boolean insertUsername = false;
		while (!insertUsername) { // Loop to prevent empty input
			String input = clientWindow.awaitNextInput();

			if (!input.isEmpty()) { // If not empty
				insertUsername = true;
				username = input;
			}

			else { // In case input is empty
				clientWindow.print("Input empty, please insert a valid username");
			}
		}
		clientWindow.print("Logged on as '" + username + "'");
		// Create our msgReceiver and msgSender threads, which basically handles everything
		msgSender();
		msgReceiver();
	}
	
	
	
	public static void msgSender() { // Our new thread that sends messages and handles our input
		new Thread(new Runnable() {
			@Override
			public void run() {
				PrintWriter pr = null;
				try { // Create our outputting thing
					pr = new PrintWriter(socket.getOutputStream());
				} catch (Exception e) {
					clientWindow.print("Failed to get output stream of server");
				}
				
				pr.println("'" + username + "' connected");
				pr.flush();
				
				while (connected) { // Actual reading
					String input = sc.nextLine(); // Client input
					if (!input.isEmpty()) { // Check if input is empty

						if ("\\".equals(input.split("")[0])) { // If input starts with backslash (will be our commands)
							switch (input.substring(1)) { // Remove slash
							
							case "usercount":
								pr.println("\\usercount");
								pr.flush();
								break;
							
							case "exit": // Proper exit
								pr.println(username + " has disconnected");
								pr.flush();
								exit();
								break;
							}
						} else { // If not commands, send to receiver
							pr.println(username + ": " + input);
							pr.flush();
						}
					}
				}
			}
		}).start();
	}

	public static void msgReceiver() { // This will be our Thread that constantly receives messages from the server /
										// other people
		new Thread(new Runnable() {
			@Override
			public void run() {

				InputStreamReader isr;
				BufferedReader bf = null;
				try {
					isr = new InputStreamReader(socket.getInputStream());
					bf = new BufferedReader(isr);
				} catch (IOException e) {

				}

				while (connected) {
					try {
						clientWindow.print(bf.readLine()); // Read incoming text
					} catch (SocketException se) { // Socket disconnected / error
						clientWindow.print("Server connection severed");
						exit();
					} catch (IOException ioe) { // IO error, usually doesnt happen
						clientWindow.print("Failed to receive text");
					}
				}
			}
		}).start();
	}

	static int retries = 0; // Retry counter

	public static boolean connect(String ip) {
		try {
			// Create a socket to the ip and port of 8888
			socket = new Socket(ip, Data.port);
			// If managed to connect, it'll move to here
			clientWindow.print("Successfully connected");
			return true;
		}

		catch (IOException ioe) { // Failed to connect go here

			if (retries < 5) { // If retry under 5 times, this prevents infinite loop
				retries++;
				clientWindow.print("Failed to connect, retrying... " + retries);
				connect(ip); // Try to connect again
			}

			else { // If already retried 5 times
				clientWindow.print("Failed to connect after 5 retries, please try again.");
				retries = 0;
				return false;
			}
		}
		return false;
	}

	public static void exit() {
		clientWindow.print("Exiting...");
		connected = false;
		try {
			socket.close();
		} catch (IOException | NullPointerException e) {
			//do nothing lol
		}
	}
}