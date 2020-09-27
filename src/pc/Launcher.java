package pc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Launcher {
	
	static String downloadHome = "https://github.com/crowwastaken/private-chat/releases/download/";
	static String testHome = "https://github.com/crowwastaken/testRepo/releases/download/";
	static String currentDir = System.getProperty("user.dir") + "\\";
	
	public static void main(String[] args) {
		try {
			InputStream testInternet = new URL("https://google.com").openStream();
			testInternet.close();
		} catch (IOException e) {
			print("No internet connection (or google is down), please try again");
		}
		
		File[] files = new File(currentDir).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				if (fileName.startsWith("client-") && fileName.contains(".jar") && !fileName.contains("-test")) {
					return true;
				}
				else {
					return false;
				}
			}
		});
		

		String currentVer = "1.0.0";
		String latestVer = "";
		boolean checking = true;
		if(files.length != 0) {
			currentVer = files[0].toString().replace(currentDir, "").replace("client-", "").replace(".jar", "");
			if(!checkClientVer(currentVer)) {
				currentVer = "0.0.0";
				print("Invalid current version");
			}
			else {
				print(currentVer + " version found");
				print("Checking for updates...");
			}
		}
		else {
			currentVer = "0.0.0";
			print("No release ver found");
		}
		
		float checkingVer = Float.parseFloat(currentVer.substring(2));
		while(checking) {
			latestVer = "1." + checkingVer;
			checkingVer += 0.1;
			checking = checkClientVer("1." + checkingVer);
		}
		
		if(latestVer.equals(currentVer)) {
			print("Up to date!");
		}
		else {
			print("Downloading latest client... (" + latestVer + ")");
			downloadClient(latestVer);
			print("Updated!");
		}
		print("Launching...");
		try {
			@SuppressWarnings("unused")
			Process p = Runtime.getRuntime().exec("java -jar client-" + latestVer + ".jar");
		} catch (IOException e) {
			print("Failed to start client");
			e.printStackTrace();
		}
	}
	
	public static boolean checkClientVer(String ver) {
		try {
			InputStream test = new URL(downloadHome + ver + "/client-" + ver + ".jar").openStream();
			test.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static void downloadClient(String ver) {
		try {
			BufferedInputStream in = new BufferedInputStream(new URL(downloadHome + ver + "/client-" + ver + ".jar").openStream());
			FileOutputStream fileOutputStream = new FileOutputStream("client-" + ver + ".jar");
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
		        fileOutputStream.write(dataBuffer, 0, bytesRead);
		    }
			fileOutputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void print(Object object) {
		System.out.println(object);
	}
}
