package searchEngine;

import java.io.*;
import java.util.*;

/**
 * T: implementation of PostList
 */
public class Partition {
    private int partitionID;
    private String path;
    private Map<String, PostList> index;

    public Partition(String path) {
        partitionID = path.hashCode();
        this.path = path;
        index = new HashMap<>();
    }

    public int getId() {
        return partitionID;
    }

    public void addToken(String token, int id, int pos) {
        PostList postList = index.get(token);
        if (postList == null) {
            postList = new PostList();
            index.put(token, postList);
        }
        postList.addPost(id, pos);
    }

    public ReadablePartition writeToDisk(Map<String, List<String>> dictionary) {
        BufferedOutputStream indOS;
        DataOutputStream indDOS = null;

        int pos = 0;
        try {
            indOS = new BufferedOutputStream(new FileOutputStream(path));
            indDOS = new DataOutputStream(indOS);

            for (Map.Entry<String, PostList> e: index.entrySet()) {
                byte[] postList = e.getValue().toBytes();

                indDOS.writeShort(postList.length); //size =
                indDOS.write(postList);

                dictionary.putIfAbsent(e.getKey(), new ArrayList<>());
                dictionary.get(e.getKey()).add(partitionID + " " + pos);
                pos += postList.length + 2;
            }
            return new ReadablePartition(path, partitionID);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                indDOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public void readFromDisk() {
        BufferedInputStream indIS = null;
        DataInputStream indDIS = null;

        try {
            indIS = new BufferedInputStream(new FileInputStream(path));
            indDIS = new DataInputStream(indIS);

            while(indDIS.available() > 0) {
                short postListSize = indDIS.readShort();
                byte[] postList = new byte[postListSize];
                indDIS.readFully(postList);

                index.put(null, PostList.fromBytes(postList));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public String toString() {
        return index.toString();
    }


}
