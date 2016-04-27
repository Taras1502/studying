//package searchEngine.core;
//
//import searchEngine.core.segments.SegmentManager;
//import searchEngine.core.Dictionary;
//import searchEngine.core.DocumentStore;
//import searchEngine.core.segments.MemorySegment;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.Queue;
//import java.util.concurrent.ExecutorService;
//
///**
// * Created by Taras.Mykulyn on 26.04.2016.
// */
//public class IndexManager extends Thread {
//    private String workingDir;
//    private DocumentStore documentStore;
//    private SegmentManager segmentManager;
//    private Dictionary dictionary;
//
//    private Queue<String> fileQueue;
//    private ExecutorService executor;
//    private volatile boolean alive;
//
//
//    public IndexManager(String workingDir, Queue<String> fileQueue) {
//        this.workingDir = workingDir;
//        this.fileQueue = fileQueue;
//        alive = true;
//    }
//
//    public static IndexManager create(String workingDir, Queue<String> fileQueue) {
//        IndexManager indexManager = new IndexManager(workingDir, fileQueue);
//        try {
//            if (!Files.exists(new File(workingDir).toPath())) {
//                Files.createDirectory(new File(workingDir).toPath());
//            }
//        } catch (IOException e) {
//            System.err.println("FAILED TO CREATE WORKING DIRECTORY");
//            return null;
//        }
//        indexManager.segmentManager = SegmentManager.create(workingDir);
//        indexManager.dictionary = Dictionary.create(workingDir + "/dictionary");
//        indexManager.documentStore = DocumentStore.create(workingDir + "/docStore");
//        return indexManager;
//    }
//
//    public IndexManager load(String workingDir, Queue<String> fileQueue) {
//        IndexManager indexManager = new IndexManager(workingDir, fileQueue);
//        indexManager.workingDir = workingDir;
//
//        indexManager.segmentManager = SegmentManager.load(workingDir);
//        indexManager.dictionary = Dictionary.load(workingDir + "/dictionary");
//        indexManager.documentStore = DocumentStore.load(workingDir + "/docStore");
//        return indexManager;
//    }
//
//    @Override
//    public void run() {
//        while (alive) {
//            String filePath;
//            while ((filePath = fileQueue.poll()) != null) {
//                indexFile(filePath);
//            }
//            try {
//                this.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void indexFile(String path) {
//        File file = new File(path);
//        // TODO: more serious logic needed to calculate approx. space needed
//        long freeSpaceNeeded = file.length() / 3;
//        MemorySegment memorySegment = segmentManager.getInMemorySegment(freeSpaceNeeded);
//
//        // doc registration
//        int docId = documentStore.addDocument(path, memorySegment.getId());
//
//        IndexTask indexTask = new IndexTask(memorySegment, path, docId);
//        executor.submit(indexTask);
//    }
//
//    public void close() {
//        // TODO: implement mechanism for stopping indexation process
//    }
//}
