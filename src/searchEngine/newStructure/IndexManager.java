package searchEngine.newStructure;

import searchEngine.Partition;
import searchEngine.ReadablePartition;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by macbookpro on 4/19/16.
 */
public class IndexManager {
    private ApplicationContext appContext;

    public IndexManager(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    public void addFileToIndex(String path) {
        File file = new File(path);
        int docId = appContext.getDocumentStore().registerDocument(path);

        InMemorySegment inMemorySegment = appContext.getInMemorySegment();
        long freeSpace = appContext.getMaxInMemorySegmentSize() - inMemorySegment.getSize();
        if (file.length() / 2 > freeSpace) {
            int segId = inMemorySegment.getId();
            DiscSegment discSegment = inMemorySegment.writeToDisk(appContext.getInMemoryDictionary(),
                    appContext.formDiscSegmentPath(segId));
            appContext.addNewDiscSegment(discSegment);
            //creating new inMemory segment
            inMemorySegment = new InMemorySegment(segId + 1);
            appContext.setInMemorySegment(inMemorySegment);
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            String l;
            String[] arr;
            int pos = 0;

            while((l = br.readLine()) != null) {
                arr = l.split("[ .,:;\"\'{}()-+<>]+?");
                for (String token: arr) {
                    token = token.toLowerCase();
                    // TODO: implement token filter and steamer
                    inMemorySegment.addPostList(token, docId, pos++);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void search(String... text) {
        System.out.println(appContext.getDocumentStore().toString());

        Map<String, List<String>> dictionary = appContext.getInMemoryDictionary();
        Map<Integer, DiscSegment> discSegments = appContext.getDiscSegments();
        InMemorySegment inMemorySegment = appContext.getInMemorySegment();
        for (String token: text) {
            System.out.println("*** " + token + " ***");
            System.out.println("in memory:\n" + inMemorySegment.getPostList(token).toString());
            List<String> res = dictionary.get(token);
            if (res == null || res.isEmpty()) {
                System.out.println(token + " not found...");
            } else {
                for (String p : res) {
                    String[] elem = p.split(" ");
                    if (elem.length > 0) {
                        System.out.println("on disc:\n" + discSegments.get(Integer.valueOf(elem[0])).getPostList(Integer.valueOf(elem[1])).toString());
                    }
                }
            }
        }
    }
}
