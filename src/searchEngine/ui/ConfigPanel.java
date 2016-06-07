package searchEngine.ui;

import searchEngine.a_core.index.IndexManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Taras.Mykulyn on 24.05.2016.
 */
public class ConfigPanel extends JPanel {
    private static final String LABEL_TEXT_FORMAT = "<html><div WIDTH=%d>%s</div><html>";
    private static final int INDEX_FILE_SET_PANEL_WIDTH = 290;
    private static final int INDEX_FILE_SET_PANEL_MAX_HEIGHT = 150;
    private IndexManager indexManager;
    private List<String> docFileSet;
    private List<JCheckBox> docFileComponents;

    public ConfigPanel(IndexManager indexManager) {
        setLayout(new GridBagLayout());
        this.indexManager = indexManager;
        this.docFileSet = indexManager.getFilesToIndex();

        JLabel fileSetLabel = new JLabel("INDEXED FILES COLLECTION");
        docFileComponents = new ArrayList<>();

        JPanel docFileSetPanel = createIndexFileSetPanel(this.docFileSet, docFileComponents);
        JScrollPane docScrollPane = new JScrollPane(docFileSetPanel);
        docScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        docScrollPane.setPreferredSize(new Dimension(INDEX_FILE_SET_PANEL_WIDTH,
                Math.min(docFileSet.size() * 30, INDEX_FILE_SET_PANEL_MAX_HEIGHT)));
        docScrollPane.setBorder(BorderFactory.createEmptyBorder());


        JButton addFileButton = new JButton("Add");
        JButton removeFileButton = new JButton("Remove");
        removeFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelected(docFileSetPanel, docFileComponents);
                docFileSetPanel.revalidate();
                docFileSetPanel.repaint();
            }
        });

        JLabel indexPathTitleLabel = new JLabel("Index Collection Path:\n" + indexManager.getWorkingDir());
        indexPathTitleLabel.setPreferredSize(new Dimension(290, 40));

        JLabel indexedFilesNum = new JLabel("Number of indexed files: " + indexManager.getIndex().getDocumentStore().getDocNum());
        indexedFilesNum.setPreferredSize(new Dimension(290, 40));


        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(0,0,0,0);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 1;

        constraints.gridwidth = 2;
        add(fileSetLabel, constraints);

        constraints.gridy++;
        add(docScrollPane, constraints);

        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        add(addFileButton, constraints);

        constraints.gridx++;
        constraints.anchor = GridBagConstraints.WEST;
        add(removeFileButton, constraints);


        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy++;
        add(indexPathTitleLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.WEST;
        add(indexedFilesNum, constraints);

        setVisible(true);
    }

    public JPanel createIndexFileSetPanel(List<String> elements, List<JCheckBox> components) {
        JPanel indexFileSetPane = new JPanel();
//        indexFileSetPane.setPreferredSize(INDEX_FILE_SET_PANEL_DIMENSION);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(0,0,0,0);
        constraints.weighty = 0.1;
//        constraints.gridheight = 30;
        constraints.gridy = 0;
        indexFileSetPane.setLayout(layout);
        for (String elem: elements) {
            JCheckBox filePathComponent = createPathLabel(elem, 250);
            indexFileSetPane.add(filePathComponent, constraints);
            constraints.gridy++;
            components.add(filePathComponent);
        }
        return indexFileSetPane;
    }

    public void removeSelected(JPanel panel, List<JCheckBox> components) {
        for (JCheckBox filePathComponent: components) {
            if (filePathComponent.isSelected()) {
                panel.remove(filePathComponent);
            }
        }
        panel.setPreferredSize(new Dimension(INDEX_FILE_SET_PANEL_WIDTH,
                Math.min(docFileSet.size() * 30, INDEX_FILE_SET_PANEL_MAX_HEIGHT)));
    }

    private JCheckBox createPathLabel(String path, int labelMaxWidth) {
        JCheckBox pathElem = new JCheckBox();
        pathElem.setText(String.format(LABEL_TEXT_FORMAT, labelMaxWidth, path));
        System.out.println(pathElem.getHeight());
        return pathElem;
    }


    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setSize(new Dimension(300, 300));
        List list = Arrays.asList("private JPanel getJContentPane() {rivate JPanel getJContentPane() ");
//        jFrame.setContentPane(new ConfigPanel(list, "lalalalalala"));
        jFrame.setVisible(true);
    }

}
