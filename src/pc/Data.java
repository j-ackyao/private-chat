package pc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class Data { // Data or whatever not really relevant just to make things neater and easier to handle
	public static String systemFont = "color:black;font-family:'Courier New'";
	public static String systemErrorFont = "color:red;font-family:'Courier New'";
	public static String clientFont = "color:black;font-family:'Comic Sans MS'";
	
	public static String grabIP() { // Grabs IP
		try {
			URL ipGrabber = new URL("http://bot.whatismyipaddress.com");
			BufferedReader br = new BufferedReader(new InputStreamReader(ipGrabber.openStream()));
			return br.readLine().trim();
		} catch (Exception e) { // If failed to grab IP
			return "-1";
		}
	}
	
	public static String accessToServerFile() {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			
			File privateKeyFile = new File("privateChatServerKey.key");
			FileInputStream keyReader = new FileInputStream("privateChatServerKeys.key");
			byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];
			keyReader.read(encodedPrivateKey);
			keyReader.close();
			
			File serverAccessFile = new File("privateChatServerConnection");
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
