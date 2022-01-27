package pc;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Client extends Thread {

	private static Socket socket; // Our socket (connection) to the whatever server
	private static String username;
	private static boolean connected = true;
	private static ClientWindow clientWindow;

	public static void main(String[] args) throws IOException {
		
		// if jar file has been renamed to debug.jar, open console
		String fileName = new java.io.File(Client.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
		if("debug.jar".equals(fileName)) {
			Console con = System.console();
			if(con == null) {
				try {
					String launcherName = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
					Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start", "cmd", "/k", "java -jar " + launcherName});
					System.exit(0);
				} catch (IOException e) {}
			}
		}
		
		
		clientWindow = new ClientWindow();
		
		clientWindow.print("Checking client version...", Data.systemFont);
		
		//check version
		if(Float.parseFloat(Data.clientVersion.substring(2)) > Float.parseFloat(Data.getLatestClientVer(Data.clientVersion).substring(2))) {
			clientWindow.print("Warning: This client is invalid and the latest version should be downloaded", Data.systemErrorFont);
		}
		else if(!Data.clientVersion.equals(Data.getLatestClientVer(Data.clientVersion))) {
			clientWindow.print("Warning: This client is outdated and the new version should be downloaded", Data.systemErrorFont);
		}
		
		// Receive host ip
		startup();
		// Create our msgReceiver and msgSender threads, which basically handles
		// everything
		msgSender();
		msgReceiver();
	}

	public static void startup() {
		clientWindow.print("Insert server IP with port. 'file' to connect with server connection file. 'local:[PORT]' for current IP.", Data.systemFont);
		boolean insertHost = false;
		
		while (!insertHost) { // Loop to retry multiple times
			String[] input = clientWindow.awaitNextInput().split(":");
			
			if(input.length == 0) {
				
			}
			
			else if ("file".equals(input[0])) { // Set IP to testing server
				clientWindow.print("Connecting with server file...", Data.systemFont);
				String[] serverFile = Data.accessToServerFile().split(":");
				if (!serverFile[0].isEmpty()) {
					insertHost = connect(serverFile[0], serverFile[1]);
				} else {
					clientWindow.print("Failed to access server connection file (missing or faulty access files)",
							Data.systemErrorFont);
				}
			}
			
			else if (input[0].isEmpty()) {
				clientWindow.print("Please include the IP.", Data.systemFont);
			}
			
			else if (input.length == 1) {
				clientWindow.print("Please include the port.", Data.systemFont);
			}

			else if ("local".equals(input[0])) { // Set IP to device IP
				String ip = Data.getPrivateIP(); // Grabs IP of local device

				if ("-1".equals(ip)) { // This is when it failed to get the IP
					clientWindow.print("Failed to find device's IP, possible internet connection problem.", Data.systemErrorFont);
					exit("error");
				}

				else { // Set to local IP
					clientWindow.print("Connecting to " + ip + ":" + input[1] + "...", Data.systemFont);
					insertHost = connect(ip, input[1]);
				}
			}
			
			else { // Set IP to inputed IP
				clientWindow.print("Connecting to " + input[0] + ":" + input[1] + "...", Data.systemFont);
				insertHost = connect(input[0], input[1]);

			}
		}

		clientWindow.print("Insert username", Data.systemFont);
		String input = clientWindow.awaitNextInput();
		username = input;
		clientWindow.setTitle(username);
	}

	public static void msgSender() { // Our new thread that sends messages and handles our input
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendToServer("\"" + username + "\" connected");

				while (connected) { // Actual reading
					String input = clientWindow.awaitNextInput(); // Client input
					sendToServer(username + ": " + input);
				}
			}

			public void sendToServer(Object object) {
				try { // Create our outputting thing
					PrintWriter pr = new PrintWriter(socket.getOutputStream());
					pr.println(object);
					pr.flush();
				} catch (IOException ioe) {
					clientWindow.print("Failed to get output stream of server, '" + object + "' was not sent.",
							Data.systemErrorFont);
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
					print("Failed to get input stream of server");
					exit("error");
				}

				while (connected) {
					try {
						String[] input = bf.readLine().split("\\\\", 2); // Split incoming string into font and actual
																			// text
						clientWindow.print(input[1], input[0]); // Read incoming text
					} catch (SocketException se) { // Socket disconnected / error
						print("Server connection severed");
						exit("error");
					} catch (IOException ioe) { // IO error, usually doesnt happen
						clientWindow.print("Failed to receive text", Data.systemErrorFont);
					}
				}
			}
		}).start();
	}

	static int retries = 0; // Retry counter

	public static boolean connect(String ip, String stringPort) {
		try {
			int port = Integer.parseInt(stringPort);
			// Create a socket to the ip and port of 888
			socket = new Socket(ip, port);
			// If managed to connect, it'll move to here
			clientWindow.print("Successfully connected", Data.systemFont);
			return true;
		}

		catch (IOException ioe) { // Failed to connect go here

			if (retries < 5) { // If retry under 5 times, this prevents infinite loop
				retries++;
				clientWindow.print("Failed to connect, retrying... " + retries, Data.systemFont);
				connect(ip, stringPort); // Try to connect again
			}

			else { // If already retried 5 times
				clientWindow.print("Failed to connect after 5 retries, please try again.", Data.systemFont);
				retries = 0;
				return false;
			}
		}
		
		catch (IllegalArgumentException iae) {
			clientWindow.print("Port should be an integer from 0 to 65353.", Data.systemErrorFont);
		}
		return false;
	}

	public static void print(Object object) {
		System.out.println(object);
	}

	public static void exit(String exitType) {
		connected = false;
		try {
			socket.close();
		} catch (IOException | NullPointerException e) {
			// do nothing lol
		}

		switch (exitType) {

		case "safe":
			print("Exiting...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			System.exit(0);

		case "error":
			print("Press 'enter' to continue...");
			clientWindow.setVisible(false);
			try {
				System.in.read();
			} catch (IOException e) {
			}
			System.exit(0);

		case "restart":
			clientWindow.dispose();
			clientWindow = new ClientWindow();
			startup();
			break;
		}
	}
}