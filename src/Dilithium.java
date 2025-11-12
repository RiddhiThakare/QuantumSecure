import org.openquantumsafe.Signature;

public class Dilithium {
    private final Signature sig;
    private final byte[] pubKey;
    private final byte[] privKey;

    public Dilithium() throws Exception {
        sig = new Signature("Dilithium2");
        sig.generate_keypair();
        pubKey = sig.export_public_key();
        privKey = sig.export_secret_key();
    }

    public byte[] signFile(byte[] data) throws Exception {
        return sig.sign(data);
    }

    public static boolean verifyFile(byte[] data, byte[] signature, byte[] pubKey) throws Exception {
        Signature verifier = new Signature("Dilithium2");
        return verifier.verify(data, signature, pubKey);
    }

    public byte[] getPublicKey() { return pubKey; }
    public byte[] getPrivateKey() { return privKey; }
}
