package ui.graphic;

import logic.ClientModel;
import logic.ListController;
import logic.state.WaitRequest;
import shared.OmniFile;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by OmniBox on 08-11-2014.
 */
public class ListPanel extends JPanel implements Observer {
    private ClientModel cm;
    private ListController lc;
    private FileInfoPanel fileInfoPanel;
    Box vertical;
    private static JList<OmniFile> filesList;
    CustomListModel listModel = new CustomListModel();
    public static boolean isListControllerStarted = false;

    public ListPanel(ClientModel cm) {
        this.cm = cm;
        this.lc = new ListController(cm.getClient(), this);
        cm.addObserver(this);
        buildLayout();
    }

    private void buildLayout() {
        fileInfoPanel = new FileInfoPanel(cm);


        Box horizontal = Box.createHorizontalBox();

        setBackground(Color.LIGHT_GRAY);

        horizontal.setPreferredSize(new Dimension(500, 800));
        horizontal.setAlignmentX(CENTER_ALIGNMENT);
        horizontal.setAlignmentY(CENTER_ALIGNMENT);

        vertical = Box.createVerticalBox();

        listModel.add(0, new OmniFile("You're not logged in."));

        filesList = new JList<OmniFile>(listModel);
        filesList.setMinimumSize(new Dimension(200, 0));
        filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allows one selection per time
        filesList.setVisibleRowCount(20); // Set max items visible without scrolling
        final JScrollPane listScroller = new JScrollPane();
        listScroller.setViewportView(filesList);// Set scroller to list view

        JLabel title = new JLabel("Server files list: ");
        title.setFont(new Font(Font.SERIF, 0, 20));

        vertical.setMinimumSize(new Dimension(300, 800));
        vertical.add(title);
        vertical.add(Box.createRigidArea(new Dimension(0, 10)));
        vertical.add(listScroller);
        vertical.setAlignmentX(CENTER_ALIGNMENT);
        vertical.setAlignmentY(TOP_ALIGNMENT);

        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //add(vertical);
        horizontal.add(vertical);
        horizontal.add(fileInfoPanel);

        add(horizontal);

        filesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (listSelectionEvent.getSource() != null && listModel.files.size() > 0)
                    fileInfoPanel.setFileInfoAttributes(((OmniFile)((JList)listSelectionEvent.getSource()).getSelectedValue()).getFileName(),
                        "" + ((OmniFile)((JList)listSelectionEvent.getSource()).getSelectedValue()).getFileSize(),
                        ((OmniFile)((JList)listSelectionEvent.getSource()).getSelectedValue()).getFileExtension(),
                        //"not implemented yet");
                            new Date(((OmniFile)((JList)listSelectionEvent.getSource()).getSelectedValue()).getLastModified()).toString());
                else
                    fileInfoPanel.setFileInfoAttributes("", "", "", "");
            }
        });
    }

    public static synchronized JList<OmniFile> getFilesList() {
        return filesList;
    }

    public static synchronized void setFilesList(JList<OmniFile> l) {
        filesList = l;
    }

    public void delElements() {
        listModel.removeAllElements();
    }

    public void addItemToList(OmniFile item) {
        listModel.addElement(item);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (cm.getCurrentState() instanceof WaitRequest && !isListControllerStarted) {
            lc.startListController();
            isListControllerStarted = true;
        }
    }

    class CustomListModel extends AbstractListModel<OmniFile> {
        public CustomListModel() {
            super();
        }

        private ArrayList<OmniFile> files = new ArrayList<OmniFile>();
        @Override
        public int getSize() {
            return files.size();
        }

        @Override
        public OmniFile getElementAt(int index) {
            return files.get(index);
        }

        public int indexOf(Object elem) {
            return files.indexOf((OmniFile)elem);
        }

        public OmniFile elementAt(int index) {
            return files.get(index);
        }

        public void addElement(OmniFile element) {
            files.add(element);
            fireContentsChanged(this, 0, 0);
        }

        public void removeAllElements() {
            files.removeAll(files);
            fireIntervalRemoved(this, 0, 0);
        }

        public boolean removeElement(Object obj) {
            return files.remove((OmniFile)obj);
        }

        public void add(int index, OmniFile element) {
            files.add(element);
            fireContentsChanged(this, 0, 0);
        }

        public OmniFile remove(int index) {
            OmniFile aux = files.remove(index);
            fireIntervalRemoved(this, 0, 0);
            return aux;
        }

        public void clear() {
            files.clear();
        }
    }
}

