package common.storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileDataStorage implements DataStorage {

    @Override
    public void save(Object obj, String filename) {
        try {
            File file = new File(filename);
            // ✅ Ensure parent folder exists (fixes “system cannot find path”)
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(obj);
            }

        } catch (IOException e) {
            System.out.println("⚠️ Error saving object: " + e.getMessage());
        }
    }

    @Override
    public Object load(String filename) {
        File file = new File(filename);
        if (!file.exists()) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        } catch (Exception e) {
            System.out.println("⚠️ Error loading object: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Object> loadAll(String filename) {
        Object data = load(filename);
        if (data instanceof List<?>) {
            return (List<Object>) data;
        }
        return new ArrayList<>();
    }

    @Override
    public void delete(String id, String filename) {
        List<Object> list = loadAll(filename);
        list.removeIf(obj -> obj.toString().contains(id));
        saveAll(list, filename);
    }

    @Override
    public boolean fileExists(String filename) {
        return new File(filename).exists();
    }

    @Override
    public void clearFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void saveAll(List<?> objects, String filename) {
        save(objects, filename);
    }
}
