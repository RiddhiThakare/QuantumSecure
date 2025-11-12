public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.out.println("Usage:");
                System.out.println("  java Main enc <file>");
                System.out.println("  java Main dec <encryptedFile>");
                return;
            }

            String mode = args[0];
            String file = args[1];

            if (mode.equalsIgnoreCase("enc")) {
                CryptoService.processFile(new java.io.File(file));
                System.out.println("✅ Encryption complete.");
            }
            else if (mode.equalsIgnoreCase("dec")) {
                String base = file.replace(".enc", "");
                CryptoService.decryptFile(
                    new java.io.File(file),
                    new java.io.File("decrypted/" + new java.io.File(base).getName())
                );
                System.out.println("✅ Decryption complete.");
            }
            else {
                System.out.println("Invalid mode. Use enc or dec.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
