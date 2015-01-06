package ui.graphic;

import logic.ClientModel;
import logic.ListController;
import logic.state.WaitRequest;
import shared.OmniFile;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * List Panel.
 *
 * This panel includes the JList and the FileInfoPanel.
 *
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

    /**
     * This method defines the layout of layout components.
     * Also registers listeners for the JList selection event.
     */
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
                            new Date(((OmniFile)((JList)listSelectionEvent.getSource()).getSelectedValue()).getLastModified()).toString());
                else
                    fileInfoPanel.setFileInfoAttributes("", "", "", "");
            }
        });
    }

    /**
     * @return returns JList that contains all files
     */
    public static synchronized JList<OmniFile> getFilesList() {
        return filesList;
    }

    /**
     * @deprecated
     * Sets JList with a new one.
     * Is not advised to use this function, because it may mess up the application behavior above the JList.
     * @param l
     */
    public static synchronized void setFilesList(JList<OmniFile> l) {
        filesList = l;
    }

    /**
     * Delete all elements on the JList.
     */
    public void delElements() {
        listModel.removeAllElements();
    }

    /**
     * Add new item to the JList.
     * JList is not sorted, new items are added to the "head" of the JList.
     * @param item new item to be added.
     */
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

    /**
     * Custom List Model Inner class.
     * This class handles OmniFiles list, can be applied to a JList or a Dropdown box.
     * Does not sort items.
     *
     * Only getSiz() and getElementAt() have been redifined from the ListModel class, using
     * an AbstractListModel<OmniFile>
     */
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

        /**
         * Returns element index in list.
         * @param elem element to be found must be an OmniFile, otherwise
         * ClassNotFoundException will be thrown.
         * @return index of elem
         */
        public int indexOf(Object elem) {
            return files.indexOf((OmniFile)elem);
        }

        /**
         * Return element with the indicated index.
         * @param index desired index
         * @return OmniFile with index = index
         */
        public OmniFile elementAt(int index) {
            return files.get(index);
        }

        /**
         * Add new element to array.
         * It fires fireContentsChange on all JList [0,0]
         * @param element new element to be added.
         */
        public void addElement(OmniFile element) {
            files.add(element);
            fireContentsChanged(this, 0, 0);
        }

        /**
         * Remove all elements from the array.
         * This fires fireIntervalRemoved on all JList: [0,0]
         */
        public void removeAllElements() {
            files.removeAll(files);
            fireIntervalRemoved(this, 0, 0);
        }

        /**
         * @deprecated
         * Removes only one element.
         * This method does not fires fireIntervalRemovel, so its more safe to use
         * removeAllElements method to update JList, and all files should be re added.
         * @param obj
         * @return
         */
        public boolean removeElement(Object obj) {
            return files.remove((OmniFile)obj);
        }

        /**
         * @deprecated
         * Add new element at desired index.
         * This is deprecated because using this method we could ruin the order of the
         * JList. addElement(...) is the more advised way to add a new OmniFile to list.
         * @param index
         * @param element
         */
        public void add(int index, OmniFile element) {
            files.add(element);
            fireContentsChanged(this, 0, 0);
        }

        /**
         * @deprecated
         * Removes only one element.
         * This method does not fires fireIntervalRemovel, so its more safe to use
         * removeAllElements method to update JList, and all files should be re added.
         * @param index
         * @return
         */
        public OmniFile remove(int index) {
            OmniFile aux = files.remove(index);
            fireIntervalRemoved(this, 0, 0);
            return aux;
        }

        /**
         * @deprecated
         * Cleares arrayList on this model.
         * To remove all OmniFiles from array is more advised to use removeAllElements.
         */
        public void clear() {
            files.clear();
        }
    }
}

