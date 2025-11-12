import java.io.*;
import java.util.Base64;

public class Bytes {

    public static void writeToFile(String path, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        }
    }

    public static byte[] readFromFile(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return data;
        }
    }

    public static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] fromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }
}
