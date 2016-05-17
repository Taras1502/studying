package searchEngine.core.index;

import com.sun.corba.se.impl.oa.toa.TOA;
import searchEngine.core.IndexTask;
import searchEngine.core.PostList;
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
        File testDir = new File("/Users/macbookpro/Desktop/test1");


        Index index = Index.create("/Users/macbookpro/Desktop/workingDir");
        DocumentStore documentStore = index.getDocumentStore();


        Runnable i = new Runnable() {
            @Override
            public void run() {
                for (File f: testDir.listFiles()) {
                    if (f.getPath().endsWith("DS_Store")) continue;
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

                String phrase = "";
                while (in.hasNextLine()) {
                    index.search(in.nextLine());

//                    String token = in.next();
//                    if (token.startsWith("\"")) {
//                        phrase = token;
//                        System.out.println("Start of phrase found " + phrase);
//                    } else if (token.endsWith("\"")) {
//                        System.out.println("END of phrase found " + token.replace("\"", ""));
//                        PostList p1 = index.getPostList(phrase.replace("\"", ""));
//                        PostList p2 = index.getPostList(token.replace("\"", ""));
//                        phrase += token;
//                        System.out.println(phrase +
//                                "RESULT FOR PHRASE " +
//                                phrase +
//                                p1.positionalAnd(p2).toString());
//                    }
//                    index.search(token);

                }
            }
        };


        new Thread(search).start();
        new Thread(i).start();


    }
}
