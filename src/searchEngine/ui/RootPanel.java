package searchEngine.ui;

import searchEngine.a_core.index.Index;
import searchEngine.a_core.index.IndexManager;

import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Taras.Mykulyn on 20.05.2016.
 */
public class RootPanel extends JPanel {
    private static final Dimension SEARCH_FIELD_DIMENSION = new Dimension(300, 40);
    private static final Dimension DOC_LIST_PANEL_DIMENSION = new Dimension(300, 300);
    private static final Dimension DOC_DETAILS_PANEL_SIZE = new Dimension(380, 400);

    private JTextField searchField;
    public DocListPanel docListPanel;
    public DocDetailsPanel docDetailsPanel;
    private IndexManager indexManager;

    public BlockingDeque<String> searchQueue;

    public RootPanel(IndexManager indexManager) {
        searchQueue = new LinkedBlockingDeque<>();
        this.indexManager = indexManager;
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets.set(3, 3, 3, 3);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.weightx = 0.5;

        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        searchField.setBackground(new Color(242, 242, 242));
        searchField.setPreferredSize(SEARCH_FIELD_DIMENSION);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
//                if(e.getSource()==searchField) {
                if (key == KeyEvent.VK_ENTER) {
                    Toolkit.getDefaultToolkit().beep();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            docListPanel.clear();
                        }
                    });
                    try {
                        searchQueue.put(searchField.getText());
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

//                    }
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_DOWN) {
                    searchField.transferFocusDownCycle();
                    docListPanel.selectNext();
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP) {
                    searchField.transferFocusDownCycle();
                    docListPanel.selectPrev();
                }
            }
        });

        JLabel docLabel = new JLabel("       DOCUMENTS LIST");


        docDetailsPanel = new DocDetailsPanel(indexManager);
        docDetailsPanel.setPreferredSize(DOC_DETAILS_PANEL_SIZE);

        docListPanel = new DocListPanel(docDetailsPanel);
        docListPanel.setPreferredSize(DOC_LIST_PANEL_DIMENSION);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(searchField, constraints);

        constraints.gridy++;
        add(docLabel, constraints);

        constraints.gridy++;
        JScrollPane scrollPane = new JScrollPane(docListPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(DOC_LIST_PANEL_DIMENSION);
        add(scrollPane, constraints);

        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.gridheight = 3;
        add(docDetailsPanel, constraints);

        setVisible(true);
    }





}
