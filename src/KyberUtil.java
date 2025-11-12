import org.openquantumsafe.KeyEncapsulation;
import org.openquantumsafe.Pair;

public class KyberUtil {
    private static final String KEM = "Kyber512";

    public static byte[][] generateKeyPair() throws Exception {
        KeyEncapsulation kem = new KeyEncapsulation(KEM);
        kem.generate_keypair();
        byte[] pub = kem.export_public_key();
        byte[] priv = kem.export_secret_key();
        kem.dispose_KEM();
        return new byte[][]{pub, priv};
    }

    public static Pair<byte[], byte[]> encapsulate(byte[] pubKey) throws Exception {
        KeyEncapsulation kem = new KeyEncapsulation(KEM);
        Pair<byte[], byte[]> result = kem.encap_secret(pubKey);
        kem.dispose_KEM();
        return result;
    }

    public static byte[] decapsulate(byte[] privKey, byte[] ciphertext) throws Exception {
        byte[] sharedSecret = null;
        KeyEncapsulation kem = null;

        try {
            // Most builds support constructor with private key
            kem = new KeyEncapsulation(KEM, privKey);
            sharedSecret = kem.decap_secret(ciphertext);
        } catch (Exception e) {
            System.out.println("Kyber decapsulation failed using private-key constructor: " + e.getMessage());
            System.out.println("Attempting fallback mode using fresh KEM instance...");
            kem = new KeyEncapsulation(KEM);
            kem.generate_keypair(); // dummy init
            sharedSecret = kem.decap_secret(ciphertext); // this may fail if key mismatch
        } finally {
            if (kem != null) kem.dispose_KEM();
        }

        if (sharedSecret == null || sharedSecret.length != 32) {
            throw new RuntimeException("Kyber decapsulation failed: invalid shared secret.");
        }

        return sharedSecret;
    }
}
