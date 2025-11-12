import static spark.Spark.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Server {

    public static void main(String[] args) {

        port(8080);

        // ✅ Serve Frontend UI
        String base = System.getProperty("user.dir");
        staticFiles.externalLocation(base + "/web");
        System.out.println("✅ Serving UI from → " + base + "/web");

        File inputDir     = new File(base, "input");
        File outputDir    = new File(base, "output");
        File decryptedDir = new File(base, "decrypted");

        inputDir.mkdirs();
        outputDir.mkdirs();
        decryptedDir.mkdirs();

        System.out.println("📁 input     → " + inputDir.getAbsolutePath());
        System.out.println("📁 output    → " + outputDir.getAbsolutePath());
        System.out.println("📁 decrypted → " + decryptedDir.getAbsolutePath());

        // =============================================================================================
        //  UPLOAD + ENCRYPT ✅ (COMBINED)
        // =============================================================================================
        post("/process-folder", (req, res) -> {

            req.attribute("org.eclipse.jetty.multipartConfig",
                    new javax.servlet.MultipartConfigElement("/temp"));

            try {
                var part = req.raw().getPart("file");
                String name = part.getSubmittedFileName();

                File uploadedFile = new File(inputDir, name);

                try (InputStream is = part.getInputStream()) {
                    Files.copy(is, uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                System.out.println("✅ UPLOADED → " + uploadedFile.getAbsolutePath());

                // ✅ Encrypt here
                CryptoService.processFile(uploadedFile);

                File encFile = new File(outputDir, name + ".enc");

                if (!encFile.exists()) {
                    System.out.println("❌ Encryption produced no file");
                    res.status(500);
                    return "{\"status\":\"failed\",\"message\":\"Encryption failed\"}";
                }

                System.out.println("✅ ENCRYPTED → " + encFile.getAbsolutePath());

                return "{\"status\":\"success\",\"message\":\"Encrypted → " + encFile.getName() + "\"}";

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"status\":\"failed\",\"message\":\"" + e.getMessage() + "\"}";
            }
        });

        // =============================================================================================
        //  DOWNLOAD + DECRYPT ✅
        // =============================================================================================
        post("/download", (req, res) -> {

            String name = req.queryParams("file_name");
            if (name == null) return "Missing filename";

            File encFile = new File(outputDir, name);
            if (!encFile.exists()) return "Encrypted file not found";

            File decFile = new File(decryptedDir, name.replace(".enc", ""));

            try {
                System.out.println("🔓 Decrypting → " + encFile.getAbsolutePath());
                CryptoService.decryptFile(encFile, decFile);

                System.out.println("✅ Decrypted → " + decFile.getAbsolutePath());

                res.type("application/octet-stream");
                res.header("Content-Disposition",
                        "attachment; filename=\"" + decFile.getName() + "\"");

                OutputStream os = res.raw().getOutputStream();
                Files.copy(decFile.toPath(), os);
                os.flush();
                return "";

            } catch (Exception e) {
                e.printStackTrace();
                return "Decryption failed → " + e.getMessage();
            }
        });

        // =============================================================================================
        //  ROOT → load UI ✅
        // =============================================================================================
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });
    }
}
