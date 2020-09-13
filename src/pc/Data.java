package pc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Data { // Data or whatever not really relevant just to make things neater and easier to handle
	public static String testServer = "50.64.160.62";
	public static int port = 666;
	
	public static String grabIP() { // Grabs IP
		try {
			URL ipGrabber = new URL("http://bot.whatismyipaddress.com");
			BufferedReader br = new BufferedReader(new InputStreamReader(ipGrabber.openStream()));
			return br.readLine().trim();
		} catch (Exception e) { // If failed to grab IP
			return "-1";
		}
	}
}
