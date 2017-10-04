import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class EncUtils {
    byte[] buf = new byte[1024];
    Cipher dcipher;
    Cipher ecipher;
    int iterationCount = 19;
    byte[] salt = new byte[]{(byte) -87, (byte) -101, (byte) -56, (byte) 50, (byte) 86, (byte) 53, (byte) -29, (byte) 3};

    public EncUtils() {
        try {
            Key generateSecret = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec("hideitpro".toCharArray(), this.salt, this.iterationCount));
            this.ecipher = Cipher.getInstance(generateSecret.getAlgorithm());
            this.dcipher = Cipher.getInstance(generateSecret.getAlgorithm());
            AlgorithmParameterSpec pBEParameterSpec = new PBEParameterSpec(this.salt, this.iterationCount);
            this.ecipher.init(1, generateSecret, pBEParameterSpec);
            this.dcipher.init(2, generateSecret, pBEParameterSpec);
        } catch (InvalidAlgorithmParameterException e) {
        } catch (InvalidKeySpecException e2) {
        } catch (NoSuchPaddingException e3) {
        } catch (NoSuchAlgorithmException e4) {
        } catch (InvalidKeyException e5) {
        }
    }

    public boolean encryptAndCopy(File file, File file2) {
        try {
            file2.createNewFile();
            try {
                if (!encrypt(new FileInputStream(file), new FileOutputStream(file2))) {
                    return false;
                }
                file.delete();
                return true;
            } catch (FileNotFoundException e) {
                return false;
            }
        } catch (IOException e2) {
            return false;
        }
    }

    public boolean decryptAndCopy(File file, File file2) {
        try {
            file2.createNewFile();
            try {
                if (!decrypt(new FileInputStream(file), new FileOutputStream(file2))) {
                    return false;
                }
                file.delete();
                return true;
            } catch (FileNotFoundException e) {
                return false;
            }
        } catch (IOException e2) {
            return false;
        }
    }

    public boolean encrypt(InputStream inputStream, OutputStream outputStream) {
        try {
            OutputStream cipherOutputStream = new CipherOutputStream(outputStream, this.ecipher);
            while (true) {
                int read = inputStream.read(this.buf);
                if (read >= 0) {
                    cipherOutputStream.write(this.buf, 0, read);
                } else {
                    cipherOutputStream.close();
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean decrypt(InputStream inputStream, OutputStream outputStream) {
        try {
            InputStream cipherInputStream = new CipherInputStream(inputStream, this.dcipher);
            while (true) {
                int read = cipherInputStream.read(this.buf);
                if (read >= 0) {
                    outputStream.write(this.buf, 0, read);
                } else {
                    outputStream.close();
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
