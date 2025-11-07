package common.storage;

import common.utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of DataStorage
 * Uses Java serialization for object persistence
 */
public class FileManager implements DataStorage {

    private static FileManager instance;
    /**
     * Constructor - creates data directory if not exists
     */
    private FileManager() {
        File dataDir = new File(Constants.DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("📁 Data directory created: " + Constants.DATA_DIR);
            }
        }
    }
    
    @Override
    public void save(Object obj, String filename) {
        String filepath = Constants.DATA_DIR + filename;
        
        // Load existing objects
        List<Object> existingObjects = loadAll(filename);
        
        // Add new object
        existingObjects.add(obj);
        
        // Save all objects
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath))) {
            oos.writeObject(existingObjects);
            System.out.println("✅ Saved to: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Error saving to file: " + filename);
            e.printStackTrace();
        }
    }
    
    @Override
    public Object load(String filename) {
        String filepath = Constants.DATA_DIR + filename;
        
        if (!fileExists(filename)) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath))) {
            Object obj = ois.readObject();
            
            // If it's a list, return first element
            if (obj instanceof List) {
                List<?> list = (List<?>) obj;
                return list.isEmpty() ? null : list.get(0);
            }
            
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading from file: " + filename);
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> loadAll(String filename) {
        String filepath = Constants.DATA_DIR + filename;
        List<Object> objects = new ArrayList<>();
        
        if (!fileExists(filename)) {
            return objects;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath))) {
            Object obj = ois.readObject();
            
            // If the stored object is already a list
            if (obj instanceof List) {
                objects = (List<Object>) obj;
            } else {
                objects.add(obj);
            }
            
            System.out.println("📖 Loaded " + objects.size() + " objects from: " + filename);
            
        } catch (EOFException e) {
            // End of file - normal condition
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error loading all from file: " + filename);
            e.printStackTrace();
        }
        
        return objects;
    }
    
    @Override
    public void delete(String id, String filename) {
        List<Object> objects = loadAll(filename);
        
        // Remove object with matching id
        objects.removeIf(obj -> {
            // This is a simplified version
            // In real implementation, check object type and get its id
            return obj.toString().contains(id);
        });
        
        // Save remaining objects
        clearFile(filename);
        saveAll(objects, filename);
        
        System.out.println("🗑️ Deleted object with id: " + id + " from: " + filename);
    }
    
    @Override
    public boolean fileExists(String filename) {
        File file = new File(Constants.DATA_DIR + filename);
        return file.exists() && file.length() > 0;
    }
    
    @Override
    public void clearFile(String filename) {
        File file = new File(Constants.DATA_DIR + filename);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("🗑️ Cleared file: " + filename);
            }
        }
    }
    
    @Override
    public void saveAll(List<?> objects, String filename) {
        String filepath = Constants.DATA_DIR + filename;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath))) {
            oos.writeObject(objects);
            System.out.println("✅ Saved " + objects.size() + " objects to: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Error saving all to file: " + filename);
            e.printStackTrace();
        }
    }
    
    /**
     * Update an existing object in file
     * @param oldObj The object to replace
     * @param newObj The new object
     * @param filename The file name
     */
    public void update(Object oldObj, Object newObj, String filename) {
        List<Object> objects = loadAll(filename);
        
        // Find and replace
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).equals(oldObj)) {
                objects.set(i, newObj);
                break;
            }
        }
        
        // Save all
        clearFile(filename);
        saveAll(objects, filename);
        
        System.out.println("✏️ Updated object in: " + filename);
    }
    
    /**
     * Get count of objects in file
     * @param filename The file name
     * @return Number of objects
     */
    public int getCount(String filename) {
        return loadAll(filename).size();
    }

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }
}