package ui.graphic;

import logic.ClientModel;
import logic.ListController;
import logic.state.WaitRequest;
import shared.OmniFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by OmniBox on 08-11-2014.
 */
public class ListPanel extends JPanel implements Observer {
    private ClientModel cm;
    private ListController lc;
    Box vertical;
    private static JList<String> filesList;
    DefaultListModel<String> listModel = new DefaultListModel<String>();
    public static boolean isListControllerStarted = false;

    public ListPanel(ClientModel cm) {
        this.cm = cm;
        this.lc = new ListController(cm.getClient(), this);
        cm.addObserver(this);
        buildLayout();
    }

    private void buildLayout() {
        vertical = Box.createVerticalBox();

        filesList = new JList<String>(listModel);
        filesList.setMinimumSize(new Dimension(300, 0));
        filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allows one selection per time
        filesList.setVisibleRowCount(20); // Set max items visible without scrolling
        JScrollPane listScroller = new JScrollPane();
        listScroller.setViewportView(filesList);// Set scroller to list view

        JLabel title = new JLabel("Server files list: ");
        title.setFont(new Font(Font.SERIF, 0, 20));

        vertical.add(title);
        vertical.add(Box.createRigidArea(new Dimension(0, 10)));
        vertical.add(listScroller);
        vertical.setAlignmentX(CENTER_ALIGNMENT);
        vertical.setAlignmentY(TOP_ALIGNMENT);

        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(vertical);

       /* filesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                cm.setSelectedIndex(filesList.getSelectedIndex());
                cm.sendNotification();
            }
        });*/

        filesList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList l = (JList)e.getSource();
                ListModel m = l.getModel();
                int index = l.locationToIndex(e.getPoint());
                if( index>-1 ) {
                    //l.setToolTipText(m.getElementAt(index).toString());
                    l.setToolTipText(messageToShow(cm.getFilesList().get(index)));
                }
            }

            private String messageToShow(OmniFile omniFile) {
                return "Name: " + omniFile.getFileName() +
                        "\n Extension: " + omniFile.getFileExtension() +
                        "\n Size: " + omniFile.getFileSize() + "bytes"; // +
                        //"\nModified at: " + omniFile.getLastModified();
            }
        });
    }

    public static synchronized JList<String> getFilesList() {
        return filesList;
    }

    public static synchronized void setFilesList(JList<String> l) {
        filesList = l;
    }

    public void delElements() {
        listModel.removeAllElements();
    }

    public void addItemToList(String item) {
        listModel.addElement(item);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (cm.getCurrentState() instanceof WaitRequest && !isListControllerStarted) {
            lc.startListController();
            isListControllerStarted = true;
        }
    }
}

