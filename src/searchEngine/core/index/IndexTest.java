package searchEngine.core.index;

import searchEngine.core.IndexTask;
import searchEngine.core.documentStore.DocumentStore;
import searchEngine.core.segments.memorySegment.MemorySegment;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Created by Taras.Mykulyn on 05.05.2016.
 */
public class IndexTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        File testDir = new File("D:/docs");


        Index index = Index.create("D:/workingDir");
        DocumentStore documentStore = index.getDocumentStore();


        Runnable i = new Runnable() {
            @Override
            public void run() {
                for (File f: testDir.listFiles()) {
                    Future<MemorySegment> futureSegment = index.getMemorySegment(f.getPath());
                    try {
                        MemorySegment memorySegment = futureSegment.get();
//                        int docId1 = documentStore.registerDocument(f.getPath(), memorySegment.getId());
                        IndexTask t1 = new IndexTask(index, documentStore, f.getPath());
                        executorService.submit(t1);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Runnable search = new Runnable() {
            @Override
            public void run() {
                Scanner in = new Scanner(System.in);

                while (in.hasNext()) {
                    String token = in.next();
                    index.getPostList(token);
                }
            }
        };


        new Thread(search).start();
        new Thread(i).start();


    }
}
