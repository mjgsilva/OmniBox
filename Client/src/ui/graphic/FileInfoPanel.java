package ui.graphic;

import logic.ClientModel;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by OmniBox on 08-11-2014.
 */
public class FileInfoPanel extends JPanel implements Observer {
    private ClientModel cm;
    private Box vertical;
    private JLabel fileName;
    private JLabel fileSize;
    private JLabel fileExtension;
    private JLabel lastModification;

    public FileInfoPanel(ClientModel cm) {
        this.cm = cm;
        
        buildLayout();
    }

    private void buildLayout() {
        vertical = Box.createVerticalBox();

        vertical.add(new JLabel("File attributes:"));
        // Creates file description with "-" because no file is selected at first
        vertical.add(fileName = new JLabel("Name: -"));
        vertical.add(fileSize = new JLabel("Size: -"));
        vertical.add(fileExtension = new JLabel("Extension: -"));
        vertical.add(lastModification = new JLabel("Last modification: -"));
        add(vertical);
    }

    public void setFileName(String name) {
        fileName.setText("Name: " + name);
    }

    public void setFileSize(String size) {
        fileSize.setText("Size: " + size);
    }

    public void setFileExtension(String extension) {
        fileExtension.setText("Extension: " + extension);
    }

    public void setFileLastModification(String lm) {
        lastModification.setText("Name: " + lm);
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
