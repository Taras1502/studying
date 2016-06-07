package searchEngine.ui;

import searchEngine.a_core.index.IndexManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Taras.Mykulyn on 20.05.2016.
 */
public class DocDetailsPanel extends JPanel {
    private JTextArea textArea;
    private FileReader fileReader;
    private IndexManager indexManager;

    public DocDetailsPanel(IndexManager indexManager) {
        this.indexManager = indexManager;
        setLayout(new FlowLayout());
        createConfigPanel();
    }

    private void createDocEditor() {
        removeAll();
        textArea = new JTextArea(24, 30);
        textArea.setBackground(new Color(242, 242, 242));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder());

        Border b = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        JScrollPane p = new JScrollPane(textArea);
        p.setBorder(BorderFactory.createEmptyBorder());
        p.setBorder(b);

        add(p);
        setVisible(true);
        revalidate();
        repaint();
    }

    private void createConfigPanel() {
        removeAll();
        ConfigPanel configPanel = new ConfigPanel(indexManager);
        add(configPanel);
        setVisible(true);
        revalidate();
        repaint();
    }

    public void open(String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fileReader = new FileReader(new File(name));
                    fileReader.read();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                createDocEditor();
                                textArea.read(fileReader, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void clear() {
        createConfigPanel();
    }


}
