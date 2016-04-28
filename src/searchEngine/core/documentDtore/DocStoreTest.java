package searchEngine.core.documentDtore;


import java.util.Random;

/**
 * Created by macbookpro on 4/28/16.
 */
public class DocStoreTest {
    public static void main(String[] args) throws InterruptedException {
        String[] paths = {"1", "2", "3", "4", "5"};
        final DocumentStore documentStore = DocumentStore.load("/Users/macbookpro/Desktop/test");
        int id = documentStore.registerDocument("/Users/macbookpro/Desktop/tes", 10);
        System.out.println(documentStore.getSegmentId(id) + " " + documentStore.getDocPath(id));
        documentStore.updateSegmentId(id, 11);
        System.out.println(documentStore.getSegmentId(id) + " " + documentStore.getDocPath(id));
//        documentStore.commit();

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
                int segId = (int) Math.random() * 10;
                System.out.println("registered doc with id for segId "  + segId + " " +
                        documentStore.registerDocument(new DocData().toString(), segId));
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
                int ran = (int) Math.random() * 10;
                System.out.println("segment id for " + ran + " " +
                        documentStore.getSegmentId((int) Math.random() * 10));
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
                        documentStore.getDocPath((int) Math.random() * 10));
            }
        }
    }
}
