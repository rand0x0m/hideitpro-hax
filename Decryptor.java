import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Base64.Decoder;

public class Decryptor {
	private static final String prefix = "1";
	
	public static void main(String[] args) throws Exception {
		walkAndDecrypt(System.getProperty("user.dir"));
	}

	public static void walkAndDecrypt(String path) throws Exception {
		File root = new File(path);
		File[] list = root.listFiles();
		EncUtils eu = new EncUtils();

		if (list == null)
			return;

		for (File enc : list) {
			if (enc.isDirectory()) {
				walkAndDecrypt(enc.getAbsolutePath());
			} else if (enc.exists() && enc.canRead()) {
				String encName = enc.getName();
				//String encPath = enc.getAbsolutePath();
				File dec;
				String decPath = enc.getParentFile().getAbsolutePath();
				String decName;

				if (encName.endsWith(".java") || encName.endsWith(".class")) {
					continue;
				}

				String type;
				try {
					if (encName.endsWith("~") && isBase64(encName.substring(0, encName.length() - 1))) {
						decName = decryptBase64(encName.substring(0, encName.length() - 1));
					} else if (isBase64(encName)) {
						decName = decryptBase64(encName);
					} else if ((type = URLConnection.guessContentTypeFromStream(new FileInputStream(enc))) != null) {
						decName = encName + "." + type;
					} else {
						decName = encName;
					}
					
					dec = new File(decPath + File.separator + decName);
					
					if (isBase64(encName) || isBase64(encName.substring(0, encName.length() - 1))) {
						Process p = Runtime.getRuntime().exec("cp " + encName + " " + Decryptor.prefix + decName);
						p.waitFor();
						System.out.println("Decrypting " + encName + " name to " + Decryptor.prefix + decName + ".");
					}
					
					if (eu.decrypt(new FileInputStream(enc), new FileOutputStream(dec))) {
						System.out.println("Decrypting file " + encName + " to " + decName);
					} else {
						dec.delete();
						System.out.println("File " + encName + " possibly not encrypted.");
					}
				} catch (Exception e) {
					System.out.println("Skipped " + encName + "with exception ");
				}
			}
		}
	}

	private static String decryptBase64(String s) throws Exception {
		Decoder b64 = Base64.getDecoder();
		return new String(b64.decode(s));
	}

	private static boolean isBase64(String s) throws Exception {
		Decoder b64 = Base64.getDecoder();
		try {
			b64.decode(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

