package pc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class Data { // Data or whatever not really relevant just to make things neater and easier to handle
	public static String systemFont = "color:black;font-family:'Courier New'";
	public static String systemErrorFont = "color:red;font-family:'Courier New'";
	public static String clientFont = "color:black;font-family:'Comic Sans MS'";
	

	public static String releasesHome = "https://github.com/crowwastaken/private-chat/releases/";
	public static String downloadHome = "https://github.com/crowwastaken/private-chat/releases/download/";
	public static String testHome = "https://github.com/crowwastaken/testRepo/releases/download/";
	public static String currentDir = System.getProperty("user.dir") + "\\";
	
	public static String clientVersion = "1.0.6";
	
	public static String getPublicIP() { // Grabs IP
		try {
			URL ipGrabber = new URL("http://checkip.amazonaws.com");
			BufferedReader br = new BufferedReader(new InputStreamReader(ipGrabber.openStream()));
			return br.readLine().trim();
		} catch (Exception e) { // If failed to grab IP
			return "-1";
		}
	}
	
	public static String getPrivateIP() { // Grabs IP
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) { // If failed to grab IP
			return "-1";
		}
	}
	
	public static String getLatestClientVer(String startVersion) {
		
		if(!checkClientVer(startVersion)) {
			startVersion = "1.0.0";
		}
		
		float checkingVer = Float.parseFloat(startVersion.substring(2));
		String latestVer = "1.0.0";
		
		boolean checking = true;
		while(checking) {
			latestVer = "1." + checkingVer;
			checkingVer += 0.1;
			checking = checkClientVer("1." + checkingVer);
		}
		
		
		
		return latestVer;
	}
	
	public static boolean checkClientVer(String ver) {
		try {
			InputStream test = new URL(releasesHome + ver).openStream();
			test.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static void generateServerFile(String hostIP, int hostPort) throws Exception {
		
		String address = hostIP + ":" + hostPort;
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		PublicKey publicKey;
		PrivateKey privateKey;

		// generate keys
		KeyPair keyPair = keyPairGen.generateKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
		
		// encrypt
		byte[] content = address.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		cipher.update(content);
		byte[] encrypted = cipher.doFinal();
		
		// save file
		FileOutputStream encryptedWriter = new FileOutputStream("privateChatServerFile");
		encryptedWriter.write(encrypted);
		encryptedWriter.close();
		
		// save key
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		FileOutputStream keyWriter = new FileOutputStream("privateChatServerKey.key");
		keyWriter.write(pkcs8EncodedKeySpec.getEncoded());
		keyWriter.close();
	}
	
	public static String accessToServerFile() {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			
			File privateKeyFile = new File("privateChatServerKey.key");
			FileInputStream keyReader = new FileInputStream("privateChatServerKey.key");
			byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];
			keyReader.read(encodedPrivateKey);
			keyReader.close();
			
			File serverAccessFile = new File("privateChatServerFile");
			FileInputStream accessReader = new FileInputStream("privateChatServerFile");
			byte[] encodedAccess = new byte[(int) serverAccessFile.length()];
			accessReader.read(encodedAccess);
			accessReader.close();
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			cipher.update(encodedAccess);
			String decrypted = new String(cipher.doFinal(), "UTF-8");

			return decrypted;
		} catch (Exception e) {
			return "";
		}
	}
}
