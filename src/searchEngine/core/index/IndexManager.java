package searchEngine.core.index;

import searchEngine.core.Crawler;
import searchEngine.core.documentStore.DocumentStore;

import java.util.Scanner;
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

    public void start() {
        // starting crawler thread
        startCrawlerThread();
        crawler.addPath("/Users/macbookpro/Desktop/test1/history.txt");
        crawler.addPath("/Users/macbookpro/Desktop/test");
    }

    private void startCrawlerThread() {
        crawlerThread.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                crawler.crawl();
            }
        }, 0, 5000);
    }

    private void stopCrawlerThread() {
        crawlerThread.cancel();
    }

    public static void main(String[] args) {
        IndexManager indexManager = new IndexManager("/Users/macbookpro/Desktop/workingDir");
        indexManager.init();
        indexManager.start();
        Index index = indexManager.index;


        new Thread(new Runnable() {
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
        }).start();
    }
}

