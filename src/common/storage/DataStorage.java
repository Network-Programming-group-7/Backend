package common.storage;

import java.util.List;

/**
 * Interface for data storage operations
 * Implementations can be file-based, database-based, etc.
 */
public interface DataStorage {
    
    /**
     * Save a single object to storage
     * @param obj The object to save
     * @param filename The file/collection name
     */
    void save(Object obj, String filename);
    
    /**
     * Load a single object from storage
     * @param filename The file/collection name
     * @return The loaded object or null if not found
     */
    Object load(String filename);
    
    /**
     * Load all objects from storage
     * @param filename The file/collection name
     * @return List of all objects
     */
    List<Object> loadAll(String filename);
    
    /**
     * Delete an object by id
     * @param id The object identifier
     * @param filename The file/collection name
     */
    void delete(String id, String filename);
    
    /**
     * Check if file exists
     * @param filename The file/collection name
     * @return true if file exists
     */
    boolean fileExists(String filename);
    
    /**
     * Clear all data from file
     * @param filename The file/collection name
     */
    void clearFile(String filename);
    
    /**
     * Save a list of objects
     * @param objects List of objects to save
     * @param filename The file/collection name
     */
    void saveAll(List<?> objects, String filename);
}