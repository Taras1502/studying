package searchEngine.core.documentStore;


import searchEngine.core.documentStore.DocData;
import searchEngine.core.documentStore.DocumentStore;

import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Created by macbookpro on 4/28/16.
 */
public class DocStoreTest {
    private static final Random rand = new Random();

    public static void main(String[] args) throws InterruptedException {

        final DocumentStore documentStore = DocumentStore.load("d:/workingDir");


        Runnable docRegister = new DocRegister(documentStore);
        Runnable segIdRetriever = new SegIdRetriever(documentStore);
        Runnable docPathRetriever = new DocPathRetriever(documentStore);

        new Thread(docRegister).start();
        new Thread(segIdRetriever).start();
        new Thread(docPathRetriever).start();
        Thread.sleep(4000);
        documentStore.close();
    }

    static class DocRegister implements Runnable {
        private DocumentStore documentStore;

        public DocRegister(DocumentStore documentStore) {
            this.documentStore = documentStore;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                int segId = rand.nextInt(10) + 0;
                System.out.println("registered doc with id for segId "  + segId + " " +
                        documentStore.registerDocument(new DocData().toString(), segId));
                if (i % 10 == 0) {
                    documentStore.commit();
                }
            }
        }
    }

    static class SegIdRetriever implements Runnable {
        private DocumentStore documentStore;

        public SegIdRetriever(DocumentStore documentStore) {
            this.documentStore = documentStore;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                int ran = rand.nextInt(10) + 0;
                System.out.println("segment id for " + ran + " " +
                        documentStore.getSegmentId(rand.nextInt(10) + 0));
                documentStore.removeDoc(rand.nextInt(10) + 0);
            }
        }
    }

    static class DocPathRetriever implements Runnable {
        private DocumentStore documentStore;

        public DocPathRetriever(DocumentStore documentStore) {
            this.documentStore = documentStore;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.println("docPath " +
                        documentStore.getDocPath(rand.nextInt(10) + 0));
            }
        }
    }
}
