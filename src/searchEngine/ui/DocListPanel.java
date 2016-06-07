package searchEngine.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Created by Taras.Mykulyn on 20.05.2016.
 */
public class DocListPanel extends JPanel {
    public static final int HEIGHT = 23;
    private int listCount = 0;
    private int selected;

    private DocDetailsPanel docDetailsPanel;
    private Map<String, String> files;
    private java.util.List<JLabel> docList;

    public DocListPanel(DocDetailsPanel docDetailsPanel) {
        super();
        docList = new ArrayList<>();
        files = new HashMap<>();
        this.docDetailsPanel = docDetailsPanel;

        setVisible(true);
    }


    public void addDoc(String path) {
        files.put(getFileName(path), path);
        listCount++;
        JLabel docPathPane = new JLabel();
        docPathPane.setAlignmentY(CENTER_ALIGNMENT);
        docPathPane.setOpaque(true);
        docPathPane.setPreferredSize(new Dimension(getWidth(), HEIGHT));
//        docPathPane.setBackground(new Color(230, 230, 230));
        docPathPane.setText(getFileName(path));
        docPathPane.setVisible(true);
        docPathPane.setAlignmentY(Component.CENTER_ALIGNMENT);
        docPathPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (selected >= 0 && selected < docList.size()) {
                            docList.get(selected).setBackground(null);
                            selected = docList.indexOf(docPathPane);
                            docPathPane.setBackground(new Color(217, 217, 217));
                            docDetailsPanel.open(files.get(docPathPane.getText()));
                        }
                    }
                });

            }
        });
        add(docPathPane);
        if (docList.isEmpty()) {
            docPathPane.getMouseListeners()[0].mouseClicked(null);
        }
        docList.add(docPathPane);
        setSize(new Dimension(getWidth(), Math.max(getHeight(), listCount * (HEIGHT + 5))));
        setPreferredSize(new Dimension(getWidth(), getHeight()));
        revalidate();
    }

    public void clear() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (JLabel doc : docList) {
                    doc.setVisible(false);
                    remove(doc);
                }
                docList.clear();
                files.clear();

                setSize(new Dimension(300, 300));
                docDetailsPanel.clear();
                listCount = 0;
                selected = 0;
            }
        });

    }

    public void selectNext() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                    System.out.println(selected);
                if (selected >= 0 && (selected + 1) < docList.size()) {
                    docList.get(selected + 1).getMouseListeners()[0].mouseClicked(null);
                }
            }
        });
    }

    public void selectPrev() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (selected > 0 && selected < docList.size()) {
                    docList.get(selected - 1).getMouseListeners()[0].mouseClicked(null);
                }
            }
        });
    }

    private String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }
}
