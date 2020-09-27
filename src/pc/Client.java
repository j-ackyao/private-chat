package pc;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
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
		
		clientWindow = new ClientWindow();
		clientWindow.addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {new Thread(()-> {exit("safe");}).start();} // dont worry about this
		});
		
		// Receive host ip
		clientWindow.print("Insert server IP. 'test' for testing server. 'local' to connect to device's current IP", Data.systemFont);
		boolean insertHost = false;
		while (!insertHost) { // Loop to retry multiple times
			String input = clientWindow.awaitNextInput();

			if ("test".equals(input)) { // Set IP to testing server
				clientWindow.print("Connecting to testing server...", Data.systemFont);
				insertHost = connect(Data.testServer);
			}

			else if ("local".equals(input)) { // Set IP to device IP
				String ip = Data.grabIP(); // Grabs IP of local device

				if ("-1".equals(ip)) { // This is when it failed to get the IP
					clientWindow.print("Failed to find device's IP, possible internet connection problem", Data.systemFont);
					exit("error");
				}

				else { // Set to desired IP
					clientWindow.print("Connecting to " + ip + "...", Data.systemFont);
					insertHost = connect(ip);
				}
			}

			else { // Set IP to inputed IP
				clientWindow.print("Connecting to " + input + "...", Data.systemFont);
				insertHost = connect(input);
			}
		}

		clientWindow.print("Insert username", Data.systemFont);
		String input = clientWindow.awaitNextInput();
		username = input;
		// Create our msgReceiver and msgSender threads, which basically handles everything
		msgSender();
		msgReceiver();
	}
	
	public static void msgSender() { // Our new thread that sends messages and handles our input
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendToServer("'" + username + "' connected");

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
					clientWindow.print("Failed to get output stream of server, '" + object + "' was not sent.", Data.systemErrorFont);
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
						String[] input = bf.readLine().split("\\\\", 2); // Split incoming string into font and actual text
						clientWindow.update(); // Idk why the update works when here
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

	public static boolean connect(String ip) {
		try {
			// Create a socket to the ip and port of 8888
			socket = new Socket(ip, Data.port);
			// If managed to connect, it'll move to here
			clientWindow.print("Successfully connected", Data.systemFont);
			return true;
		}

		catch (IOException ioe) { // Failed to connect go here

			if (retries < 5) { // If retry under 5 times, this prevents infinite loop
				retries++;
				clientWindow.print("Failed to connect, retrying... " + retries, Data.systemFont);
				connect(ip); // Try to connect again
			}

			else { // If already retried 5 times
				clientWindow.print("Failed to connect after 5 retries, please try again.", Data.systemFont);
				retries = 0;
				return false;
			}
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
		switch(exitType) {
		
		case "safe":
			System.out.println("Exiting...");
			connected = false;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			System.exit(0);
			
		case "error":
			System.out.println("Press 'enter' to exit");
			clientWindow.setVisible(false);
			try {
				System.in.read();
			} catch (IOException e) {}
			System.exit(0);
		}
	}
}