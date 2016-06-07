package searchEngine.core;
import searchEngine.core.index.Index;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by macbookpro on 5/16/16.
 */
public class Crawler {
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Arrays.asList("", "txt"));
    private searchEngine.core.documentStore.DocumentStore documentStore;
    private Index index;
    private Set<File> paths;
    private volatile long lastCrawled;
    private volatile boolean stop;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public Crawler(Index index) {
        this.index = index;
        documentStore = index.getDocumentStore();

        paths = Collections.synchronizedSet(new HashSet<File>()); ;
        lastCrawled = 0;
        stop = false;
    }

    public void stopCrawling() {
        stop = true;
    }

    public void addPath(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                paths.add(f);
            }
        } catch (Exception e) {
            Logger.error(getClass(), "File with path " + path + "does not exist.");
        }
    }

    public void removePath(String path) {
         try {
             File f = new File(path);
             paths.remove(f);
         } catch (Exception e) {
             Logger.error(getClass(), "File with path " + path + "does not exist.");
         }
    }

    public void crawl() {
        Iterator<File> it = paths.iterator();
        while (it.hasNext() && !stop) {
            File file = it.next();
            if (file.isDirectory()) {
                crawlFolder(file);
            } else {
                if (candidateToIndex(file)) {
                    IndexTask t1 = new IndexTask(index, documentStore, file.getPath());
                    executorService.submit(t1);
                }
            }
        }

        lastCrawled = new Date().getTime();
    }

    private void crawlFolder(File folder) {
        File[] files = lastModified(folder);
        for (File f: files) {
            IndexTask t1 = new IndexTask(index, documentStore, f.getPath());
            executorService.submit(t1);
        }

        File[] subDirs = getSubDirs(folder);
        for (File subDir: subDirs) {
            crawlFolder(subDir);
        }
    }

    private File[] lastModified(File dir) {
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return candidateToIndex(file);
            }
        });

        return files;
    }

    private boolean candidateToIndex(File file) {
        String filePath = file.getPath();
        String ext = "";
        int i = filePath.lastIndexOf('.');

        if (i > 0) {
            ext = filePath.substring(i + 1);
        }
        return file.isFile() && SUPPORTED_EXTENSIONS.contains(ext) &&
                (documentStore.contains(file.getPath()) == -1 || file.lastModified() > lastCrawled);
    }

    private File[] getSubDirs(File dir) {
        File[] subDirs = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        return subDirs;
    }
}
