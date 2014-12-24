package ui.graphic;

import logic.ClientModel;
import logic.state.WaitAuthentication;
import shared.OmniFile;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

/**
 * File Info Panel.
 *
 * This class defines the additional file info to show to user.
 *
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

    /**
     * This method defines the layout of layout components.
     */
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

    /**
     * This function replaces the sets methods that could be made for each variable.
     * Is simply a ways to make it easier to update selected file information.
     * @param fileName - File name
     * @param fileSize - File size
     * @param fileExTension - File extension
     * @param lastModification - File last modification
     */
    public void setFileInfoAttributes(String fileName, String fileSize, String fileExTension, String lastModification) {
        this.fileName.setText(fileName);
        this.fileSize.setText(fileSize);
        this.fileExtension.setText(fileExTension);
        this.lastModification.setText(lastModification);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (cm.getCurrentState() instanceof WaitAuthentication)
            setVisible(false);
        else {
            setVisible(true);
            // Non is selected
            if (cm.getSelectedIndex() == -1) {
                fileName.setText("-");
                fileSize.setText("-");
                fileExtension.setText("-");
                lastModification.setText("-");
            } else { // Show selected file information
                OmniFile selectedFile = cm.getSelectedFile();
                if (selectedFile == null)
                    return;
                fileName.setText(selectedFile.getFileName());
                fileSize.setText(((Long)selectedFile.getFileSize()).toString() + " bytes");
                fileExtension.setText(selectedFile.getFileExtension());
                lastModification.setText(selectedFile.getCreationDate().toString());
            }
        }
    }
}
