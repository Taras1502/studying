package searchEngine.ui;


import searchEngine.a_core.documentStore.DocumentStore;
import searchEngine.a_core.index.Index;
import searchEngine.a_core.index.IndexManager;
import searchEngine.a_core.segments.memorySegment.MemorySegment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Taras.Mykulyn on 20.05.2016.
 */
public class MainWindow extends JFrame {
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_WIDTH_SMALL = 700 - 370;
    private static final int WINDOW_HEIGHT = 430;
    private static final String WINDOW_TITLE = "SEARCH";
    private IndexManager indexManager;

    public RootPanel rootPanel;

    public MainWindow(IndexManager indexManager) {
        this.indexManager = indexManager;
        this.rootPanel = new RootPanel(indexManager);

        this.setContentPane(rootPanel);
        this.setTitle(WINDOW_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension((WINDOW_WIDTH), WINDOW_HEIGHT));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    public void start() {
        new Thread(new Task(indexManager.getIndex(), rootPanel.searchQueue, rootPanel.docListPanel)).start();
    }

    public static void main(String[] args) throws InterruptedException {
        IndexManager indexManager = new IndexManager("/Users/macbookpro/Desktop/workingDir");
        indexManager.init();
        indexManager.start();
        indexManager.addFileToIndex("/Users/macbookpro/Desktop/test1");
//

        MainWindow mainWindow = new MainWindow(indexManager);
        mainWindow.setVisible(true);
        mainWindow.start();



//        mainWindow.rootPanel.docListPanel.addDoc("144meg.txt");
//        mainWindow.rootPanel.docListPanel.addDoc("9600.txt");


//        mainWindow.rootPanel.docListPanel.removeAll();
//        mainWindow.setSize(new Dimension(WINDOW_WIDTH, WINDOW_WIDTH_SMALL));
    }


    class Task implements Runnable {
        Index index;
        BlockingDeque<String> searchQueue;
        DocListPanel docListPanel;

        public Task(Index index, BlockingDeque<String> searchQueue, DocListPanel docListPanel) {
            this.index = index;
            this.docListPanel = docListPanel;
            this.searchQueue = searchQueue;
        }
        @Override
        public void run() {
            while(true) {
                try {
                    String text = searchQueue.take();
                    java.util.List<String> res = index.search(text);
                    if (res.isEmpty()) {
                        docListPanel.clear();
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                for (String f : res) {
                                    docListPanel.addDoc(f);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
