package searchEngine.core;


import searchEngine.core.index.Index;
import searchEngine.core.segments.memorySegment.MemorySegment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public class IndexTask implements Runnable {
    private static final String TOKEN_SPLITTERS = " [ .,:;\"\'{}()-+<>]+?";
    private Index index;
    private searchEngine.core.documentStore.DocumentStore documentStore;
    private String filePath;

    public IndexTask(Index index, searchEngine.core.documentStore.DocumentStore documentStore, String filePath) {
        this.index = index;
        this.documentStore = documentStore;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String l;
            String[] arr;
            int pos = 0;
            char[] buff = new char[1024];
            StringBuilder sb;
            MemorySegment memorySegment = getMemorySegment();

            int docId = documentStore.registerDocument(filePath, memorySegment.getId());
            while(br.read(buff) != -1) {
                sb = new StringBuilder(512);
                sb.append(buff);

                StringTokenizer stringTokenizer = new StringTokenizer(sb.toString(), TOKEN_SPLITTERS);
                while (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken().toLowerCase();
                    if (TokenFilter.needToIndex(token)) {
                        if (token.equals("test")) {
                            System.out.println(token);
                        }
                        while (!memorySegment.addPostList(token, docId, pos)) {
                            memorySegment = getMemorySegment();
                            if (memorySegment != null) {
                                documentStore.addSegmentId(docId, memorySegment.getId());
                            }
                        }
                        pos++;
                    }
                }
            }


//            while((l = br.readLine()) != null) {
//                StringTokenizer stringTokenizer = new StringTokenizer(l, TOKEN_SPLITTERS);
//                while (stringTokenizer.hasMoreTokens()) {
//                    String token = stringTokenizer.nextToken().toLowerCase();
//                    if (!TokenFilter.needToIndex(token)) continue;
//                    memorySegment.addPostList(token, docId, pos++);
//                }
//
////                arr = l.split(TOKEN_SPLITTERS);
////                for (String token: arr) {
////                    token = token.toLowerCase();
////                    if (!TokenFilter.needToIndex(token)) continue;
////                    // TODO: implement token filter and steamer
////                    memorySegment.addPostList(token, docId, pos++);
////                }
//            }
//            memorySegment.markDocIndexFinished();
            Logger.info(getClass(), "FINISHED INDEXING " + filePath + "... time = " + (System.currentTimeMillis() - start));
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

    private MemorySegment getMemorySegment() {
        try {
            return index.getMemorySegment(filePath).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
