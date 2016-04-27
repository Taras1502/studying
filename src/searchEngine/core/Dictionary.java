package searchEngine.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taras.Mykulyn on 25.04.2016.
 */
public class Dictionary {
    private String path;
    private Map<String, Map<Integer, Integer>> dictionary;
    private boolean updated;

    private Dictionary(String path, Map<String, Map<Integer, Integer>> dictionary) {
        this.path = path;
        this.dictionary = dictionary;
        updated = false;
    }

    public static Dictionary load(String path) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            Map<String, Map<Integer, Integer>> dic = (Map<String, Map<Integer, Integer>>) ois.readObject();
            return new Dictionary(path, dic);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                ois.close();
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
        ObjectOutputStream ous = null;
        try {
            ous = new ObjectOutputStream(new FileOutputStream(path));
            ous.writeObject(dictionary);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                ous.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToken(String token, int segmentId, int pos) {
        Map<Integer, Integer> res = dictionary.get(token);
        if (res == null) {
            res = new HashMap<>();
            dictionary.put(token, res);
        }
        res.put(segmentId, pos);
    }

    public Map<Integer, Integer> getTokenData(String token) {
        return dictionary.get(token);
    }
}
