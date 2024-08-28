import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.zip.*;

public class ZipFileUpdater {

    public static void main(String[] args) {
        String url = "http://stuffstuff554.github.io/RCSSoftware/Downloads/Local.zip"; // URL of the zip file
        String localFolderPath = "Local"; // Local folder where the zip file should be unzipped

        try {
            // Create a temporary file to download the zip file
            Path tempZipFile = Files.createTempFile("downloaded", ".zip");

            // Download the file
            downloadFile(url, tempZipFile);

            // Create a path for the local zip file
            Path localZipFilePath = Paths.get(localFolderPath, "downloaded.zip");

            // Compare checksums
            if (!Files.exists(localZipFilePath) || !compareChecksums(tempZipFile, localZipFilePath)) {
                // If the file is new or different, unzip it and replace the existing files
                unzipFile(tempZipFile, Paths.get(localFolderPath));

                // Replace the old zip file with the new one
                Files.move(tempZipFile, localZipFilePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Updated with the new zip file.");
            } else {
                System.out.println("The local zip file is up-to-date.");
            }

            // Clean up the temporary file if it wasn't moved
            if (Files.exists(tempZipFile)) {
                Files.delete(tempZipFile);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String urlString, Path destination) throws IOException {
        try (InputStream in = new URL(urlString).openStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static boolean compareChecksums(Path file1, Path file2) throws IOException, NoSuchAlgorithmException {
        return calculateChecksum(file1).equals(calculateChecksum(file2));
    }

    private static String calculateChecksum(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(file);
             DigestInputStream dis = new DigestInputStream(is, md)) {
            byte[] buffer = new byte[4096];
            while (dis.read(buffer) != -1) ;
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static void unzipFile(Path zipFilePath, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newFilePath = destDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(newFilePath);
                } else {
                    Files.createDirectories(newFilePath.getParent());
                    try (OutputStream fos = Files.newOutputStream(newFilePath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}
