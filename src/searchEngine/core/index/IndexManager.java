package searchEngine.core.index;

import searchEngine.core.Crawler;
import searchEngine.core.documentStore.DocumentStore;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by macbookpro on 5/17/16.
 */
public class IndexManager {
    private static final int FILES_TO_INDEX_CAPACITY = 100;
    private final String workingDir;

    private Index index;
    private BlockingDeque<String> filesToIndex;
    private DocumentStore documentStore;
    private Crawler crawler;
    private Timer crawlerThread;

    public IndexManager(String workingDir) {
        this.workingDir = workingDir;
        this.filesToIndex = new LinkedBlockingDeque<>(FILES_TO_INDEX_CAPACITY);
        crawlerThread = new Timer();
    }

    public void init() {
        index = Index.create(workingDir);
        documentStore = index.getDocumentStore();
        crawler = new Crawler(index);
    }

    public void load() {

    }

    public void start(String workingDir) {
        // starting crawler thread
        startCrawlerThread();
    }

    private void startCrawlerThread() {
        crawlerThread.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                crawler.crawlRootFolders();
            }
        }, 0, 2000);
    }

    private void stopCrawlerThread() {
        crawlerThread.cancel();
    }
}
