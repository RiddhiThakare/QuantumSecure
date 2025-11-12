import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;

public class AESUtil {
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH_BIT = 128;

    public static byte[] encrypt(byte[] plaintext, byte[] key) throws Exception {
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_MODE);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] encrypted = cipher.doFinal(plaintext);

        // Combine IV + ciphertext
        byte[] output = new byte[IV_SIZE + encrypted.length];
        System.arraycopy(iv, 0, output, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, output, IV_SIZE, encrypted.length);
        return output;
    }

    public static byte[] decrypt(byte[] combined, byte[] key) throws Exception {
        if (combined.length < IV_SIZE + 16) {
            throw new IllegalArgumentException("Ciphertext too short");
        }

        byte[] iv = new byte[IV_SIZE];
        byte[] ciphertext = new byte[combined.length - IV_SIZE];
        System.arraycopy(combined, 0, iv, 0, IV_SIZE);
        System.arraycopy(combined, IV_SIZE, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(AES_MODE);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        return cipher.doFinal(ciphertext);
    }

    public static byte[] readFile(File file) throws Exception {
        return Files.readAllBytes(file.toPath());
    }
}
