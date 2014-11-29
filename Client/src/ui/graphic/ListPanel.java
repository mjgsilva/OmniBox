package ui.graphic;

import logic.ClientModel;
import logic.ListController;

import javax.swing.*;
import java.awt.*;
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

    public ListPanel(ClientModel cm) {
        this.cm = cm;
        this.lc = new ListController(cm.getClient(), this);
        buildLayout();
    }

    private void buildLayout() {
        vertical = Box.createVerticalBox();

        for (int i = 0; i < 30; i++) {
            listModel.addElement("John Doe number " + (i + 1));
        }

        filesList = new JList<String>(listModel);
        filesList.setMinimumSize(new Dimension(300, 0));
        filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allows one selection per time
        filesList.setVisibleRowCount(20); // Set max items visible without scrolling
        JScrollPane listScroller = new JScrollPane();
        listScroller.setViewportView(filesList);// Set scroller to list view

        JLabel title = new JLabel("Server files list: ");
        title.setFont(new Font(Font.SERIF, 0, 20));

        vertical.add(title);
        vertical.add(Box.createRigidArea(new Dimension(0,10)));
        vertical.add(listScroller);
        vertical.setAlignmentX(CENTER_ALIGNMENT);
        vertical.setAlignmentY(TOP_ALIGNMENT);

        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(vertical);
    }

    public static synchronized JList<String> getFilesList() {
        return filesList;
    }

    public static synchronized void setFilesList(JList<String> l) {
        filesList = l;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}

