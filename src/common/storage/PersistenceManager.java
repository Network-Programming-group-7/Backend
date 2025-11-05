package storage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

class PersistenceManager {
    static void ensureDir(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    static <T> void save(Path file, T object) throws IOException {
        ensureDir(file.getParent());
        try (OutputStream fos = new BufferedOutputStream(Files.newOutputStream(file));
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(object);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T load(Path file, T defaultValue) {
        if (!Files.exists(file)) return defaultValue;
        try (InputStream fis = new BufferedInputStream(Files.newInputStream(file));
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (T) ois.readObject();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}