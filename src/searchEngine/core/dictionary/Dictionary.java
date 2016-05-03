package searchEngine.core.dictionary;

import com.sun.javafx.collections.UnmodifiableObservableMap;
import searchEngine.core.PostList;
import searchEngine.core.segments.discSegment.DiscSegment;
import searchEngine.core.segments.memorySegment.MemorySegment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Taras.Mykulyn on 25.04.2016.
 */
public class Dictionary {
    private String path;
    private Map<String, Map<DiscSegment, Integer>> dictionary;
    private boolean updated;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private Dictionary(String path, Map<String, Map<DiscSegment, Integer>> dictionary) {
        this.path = path;
        this.dictionary = dictionary;
        updated = false;
    }

    public static Dictionary load(String path) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            Map<String, Map<DiscSegment, Integer>> dictionary = (Map<String, Map<DiscSegment, Integer>>) ois.readObject();
            return new Dictionary(path, dictionary);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Dictionary create(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
            Files.createFile(Paths.get(path));
            return new Dictionary(path, new HashMap<>());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        This operation writes in-memory dictionary to disc by serialization of the dictionary object.
        Originally designed to be called after creating or merging disc segments.
    */
    public boolean commit() {
        System.out.println("committing...");
        ObjectOutputStream ous = null;
        try {
            readLock.lock();

            if (!updated) {
                return true;
            }
            ous = new ObjectOutputStream(new FileOutputStream(path));
            ous.writeObject(dictionary);
            updated = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ous != null) {
                    ous.close();
                }
                readLock.unlock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToken(String token, DiscSegment discSegment, int pos) {
        try {
            writeLock.lock();
            Map<DiscSegment, Integer> res = dictionary.get(token);
            if (res == null) {
                res = new HashMap<>();
                dictionary.put(token, res);
                System.out.println("res == null");
            }
            res.put(discSegment, pos);
            updated = true;
        } finally {
            writeLock.unlock();
        }
    }

    public Map<DiscSegment, Integer> getTokenData(String token) {
        try {
            readLock.lock();
            return dictionary.get(token);
        } finally {
            readLock.unlock();
        }
    }



}
