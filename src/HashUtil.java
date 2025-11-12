import java.security.MessageDigest;
import java.util.Base64;

public class HashUtil {
    public static String sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(md.digest(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
