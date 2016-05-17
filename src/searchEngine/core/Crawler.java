package searchEngine.core;

import searchEngine.core.documentStore.*;
import searchEngine.core.documentStore.DocumentStore;
import searchEngine.core.index.Index;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by macbookpro on 5/16/16.
 */
public class Crawler {
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Arrays.asList("", "txt"));
    private searchEngine.core.documentStore.DocumentStore documentStore;
    private Index index;
    private Set<String> rootFolders;
    private volatile long lastCrawled;
    private volatile boolean stop;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public Crawler(Index index) {
        this.index = index;
        documentStore = index.getDocumentStore();

        rootFolders = Collections.synchronizedSet(new HashSet<String>()); ;
        lastCrawled = 0;
        stop = false;
    }

    public void stopCrawling() {
        stop = true;
    }

    public void addRootFolder(String folderPath) {
        rootFolders.add(folderPath);
    }

    public void removeRootFolder(String path) {
        rootFolders.remove(path);
    }

    public void crawlRootFolders() {
        Iterator<String> it = rootFolders.iterator();
        while (it.hasNext() && !stop) {
            String rootFolder = it.next();
            if (Files.isDirectory(Paths.get(rootFolder))) {
                crawlFolder(rootFolder);
            }
        }
        lastCrawled = new Date().getTime();
    }

    private void crawlFolder(String folder) {
        File[] files = lastModified(folder);
        for (File f: files) {
            IndexTask t1 = new IndexTask(index, documentStore, f.getPath());
            executorService.submit(t1);
        }

        File[] subDirs = getSubDirs(folder);
        for (File subDir: subDirs) {
            crawlFolder(subDir.getPath());
        }
    }

    private File[] lastModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                String filePath = file.getPath();
                String ext = "";
                int i = filePath.lastIndexOf('.');
                if (i > 0) {
                    ext = filePath.substring(i + 1);
                }
                return file.isFile() && SUPPORTED_EXTENSIONS.contains(ext) &&
                        (documentStore.contains(file.getPath()) == -1 || file.lastModified() > lastCrawled);
            }
        });

        return files;
    }

    private File[] getSubDirs(String dir) {
        File fl = new File(dir);
        File[] subDirs = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        return subDirs;
    }
}
