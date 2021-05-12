package pc;

import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Launcher {
	
	
	public static void main(String[] args) {
		
		Console con = System.console();
		if(con == null) {
			try {
				String launcherName = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
				Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start", "cmd", "/k", "java -jar " + launcherName});
				System.exit(0);
			} catch (IOException e) {}
		}
		
		try {
			InputStream testInternet = new URL("https://google.com").openStream();
			testInternet.close();
		} catch (IOException e) {
			print("No internet connection (or google is down), please try again");
		}
		
		File[] files = new File(Data.currentDir).listFiles(new FileFilter() {
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
		
		// check current version of client that user has
		String currentVer = "1.0.0";
		if(files.length != 0) {
			currentVer = files[0].toString().replace(Data.currentDir, "").replace("client-", "").replace(".jar", "");
			if(!Data.checkClientVer(currentVer)) {
				currentVer = "1.0.0";
				print("Invalid current version");
			}
			else {
				print(currentVer + " version found");
				print("Checking for updates...");
			}
		}
		else {
			// if invalid version or 
			currentVer = "1.0.0";
			print("No release ver found");
		}
		
		String latestVer = Data.getLatestClientVer(currentVer);
		
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
	
	public static void downloadClient(String ver) {
		try {
			BufferedInputStream in = new BufferedInputStream(new URL(Data.downloadHome + ver + "/client-" + ver + ".jar").openStream());
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
