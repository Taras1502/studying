package searchEngine.core.documentDtore;

import searchEngine.newStructure.*;

/**
 * Created by macbookpro on 4/28/16.
 */
public class DocStoreTest {
    public static void main(String[] args) {
        String[] paths = {"1", "2", "3", "4", "5"};
        final DocumentStore documentStore = DocumentStore.create("/Users/macbookpro/Desktop/test");
        int id = documentStore.registerDocument("/Users/macbookpro/Desktop/test", 10);
        System.out.println(documentStore.getSegmentId(id) + " " + documentStore.getDocPath(id));
        documentStore.updateSegmentId(id, 11);
        System.out.println(documentStore.getSegmentId(id) + " " + documentStore.getDocPath(id));
//        documentStore.commit();
    }


}
