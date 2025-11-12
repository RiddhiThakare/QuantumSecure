import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;
import org.openquantumsafe.Pair;

public class CryptoService {

    private static final File projectRoot = new File(System.getProperty("user.dir"));
    private static final File outputDir  = new File(projectRoot, "output");
    private static final File keysDir    = new File(projectRoot, "keys_generated");

    public static byte[] deriveAESKey(byte[] sharedSecret) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        return sha256.digest(sharedSecret);
    }

    private static String sha256Base64(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(md.digest(data));
    }

    // ============================================================
    // ✅ ENCRYPT + SIGN
    // ============================================================
    public static void processFile(File inputFile) throws Exception {
        outputDir.mkdirs();
        keysDir.mkdirs();

        String fileName = inputFile.getName();
        byte[] plain = AESUtil.readFile(inputFile);

        long plainSize = plain.length;

        MetricsLogger.log("=== ENCRYPT ===");
        MetricsLogger.log("File=" + fileName);
        MetricsLogger.log("PlainSize=" + plainSize);

        // -------------------------------------------
        // ✅ Kyber: KeyGen
        long t1 = System.nanoTime();
        byte[][] kyberKeys = KyberUtil.generateKeyPair();
        long t2 = System.nanoTime();
        double kyberKeygenMS = (t2 - t1) / 1e6;

        byte[] pub  = kyberKeys[0];
        byte[] priv = kyberKeys[1];
        Files.write(new File(keysDir, fileName + ".kyber.pub").toPath(), pub);
        Files.write(new File(keysDir, fileName + ".kyber.priv").toPath(), priv);

        // -------------------------------------------
        // ✅ Kyber: Encaps
        t1 = System.nanoTime();
        Pair<byte[], byte[]> enc = KyberUtil.encapsulate(pub);
        t2 = System.nanoTime();
        double kyberEncapsMS = (t2 - t1) / 1e6;

        byte[] left = enc.getLeft();
        byte[] right = enc.getRight();
        byte[] sharedSecret;
        byte[] ct;

        if (left.length == 32) {
            sharedSecret = left;
            ct = right;
        } else {
            sharedSecret = right;
            ct = left;
        }

        Files.write(new File(keysDir, fileName + ".kyber.ct").toPath(), ct);

        // -------------------------------------------
        // ✅ AES key derivation
        byte[] aesKey = deriveAESKey(sharedSecret);
        Files.write(new File(keysDir, fileName + ".aes.key").toPath(), aesKey);

        // -------------------------------------------
        // ✅ AES encryption
        t1 = System.nanoTime();
        byte[] encrypted = AESUtil.encrypt(plain, aesKey);
        t2 = System.nanoTime();
        double aesEncryptMS = (t2 - t1) / 1e6;

        File encFile = new File(outputDir, fileName + ".enc");
        Files.write(encFile.toPath(), encrypted);

        long cipherSize = encrypted.length;
        long overhead = cipherSize - plainSize;

        // -------------------------------------------
        // ✅ Dilithium signing
        Dilithium dil = new Dilithium();
        t1 = System.nanoTime();
        byte[] signature = dil.signFile(encrypted);
        t2 = System.nanoTime();
        double signMS = (t2 - t1) / 1e6;

        byte[] pubSig = dil.getPublicKey();
        boolean ok = Dilithium.verifyFile(encrypted, signature, pubSig);

        if (!ok) throw new RuntimeException("Immediate signature verify failed!");

        Files.write(new File(keysDir, fileName + ".sig").toPath(),     signature);
        Files.write(new File(keysDir, fileName + ".dil.pub").toPath(), pubSig);
        Files.write(new File(keysDir, fileName + ".dil.priv").toPath(), dil.getPrivateKey());

        // -------------------------------------------
        // ✅ LOG METRICS
        MetricsLogger.log("KyberKeygenMS=" + kyberKeygenMS);
        MetricsLogger.log("KyberEncapsMS=" + kyberEncapsMS);
        MetricsLogger.log("AESEncryptMS=" + aesEncryptMS);
        MetricsLogger.log("SignMS=" + signMS);
        MetricsLogger.log("CipherSize=" + cipherSize);
        MetricsLogger.log("Overhead=" + overhead);
        MetricsLogger.log("--------------------------------");
    }

    // ============================================================
    // ✅ DECRYPT + VERIFY
    // ============================================================
    public static void decryptFile(File encryptedFile, File outputFile) throws Exception {

        String base = encryptedFile.getName().replace(".enc", "");
        byte[] encData = Files.readAllBytes(encryptedFile.toPath());
        long cipherSize = encData.length;

        MetricsLogger.log("=== DECRYPT ===");
        MetricsLogger.log("CipherFile=" + encryptedFile.getName());
        MetricsLogger.log("CipherSize=" + cipherSize);

        byte[] signature = Files.readAllBytes(new File(keysDir, base + ".sig").toPath());
        byte[] pubSig    = Files.readAllBytes(new File(keysDir, base + ".dil.pub").toPath());

        // ✅ SIGN VERIFY
        long t1 = System.nanoTime();
        boolean ok = Dilithium.verifyFile(encData, signature, pubSig);
        long t2 = System.nanoTime();
        double verifyMS = (t2 - t1) / 1e6;

        if (!ok) throw new SecurityException("Signature verification failed.");

        // ✅ Recover AES key
        byte[] aesKey = Files.readAllBytes(new File(keysDir, base + ".aes.key").toPath());

        // ✅ AES DECRYPT
        t1 = System.nanoTime();
        byte[] decData = AESUtil.decrypt(encData, aesKey);
        t2 = System.nanoTime();
        double aesDecryptMS = (t2 - t1) / 1e6;

        outputFile.getParentFile().mkdirs();
        Files.write(outputFile.toPath(), decData);

        // ✅ LOG METRICS
        MetricsLogger.log("SignVerifyMS=" + verifyMS);
        MetricsLogger.log("AESDecryptMS=" + aesDecryptMS);
        MetricsLogger.log("--------------------------------");
    }
}
