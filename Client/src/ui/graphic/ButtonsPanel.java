package ui.graphic;

import logic.ClientModel;
import shared.OmniFile;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Buttons Panel.
 *
 * This panel contains:
 *      - Send Button
 *      - Get Button
 *      - Remove Button
 *
 * Created by OmniBox on 08-11-2014.
 */
public class ButtonsPanel extends JPanel implements Observer {
    private ClientModel cm;
    private Box horizontal;
    private JButton sendButton;
    private JButton getButton;
    private JButton removeButton;

    public ButtonsPanel(ClientModel cm) {
        this.cm = cm;
        cm.addObserver(this);
        buildLayout();
    }

    /**
     * This method defines the layout of layout components.
     * Also calls the register listeners method.
     */
    private void buildLayout() {
        horizontal = Box.createHorizontalBox();

        sendButton = new JButton("Send");
        getButton = new JButton("Get");
        removeButton = new JButton("Remove");

        horizontal.add(sendButton);
        horizontal.add(getButton);
        horizontal.add(removeButton);

        horizontal.setAlignmentX(CENTER_ALIGNMENT);

        //setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(horizontal);

        registerListeners();
    }

    /**
     * All this components maintain the same behaviour through this panel life cycle.
     * So this method is reserved to declare static listeners of this components.
     */
    private void registerListeners() {
        // Send button listener - OnClick
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open FileChooser first, so user can select file to send.
                JFileChooser fc = new JFileChooser();
                int returnValue = fc.showOpenDialog(ButtonsPanel.this);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        OmniFile omniFile = new OmniFile(fc.getSelectedFile().getAbsolutePath());
                        cm.setFileToUpload(omniFile);
                        cm.defineSendRequest(omniFile);
                    } catch (InterruptedException e1) {
                        new ErrorDialog(null, e1.getMessage());
                    } catch (IOException e1) {
                        new ErrorDialog(null, e1.getMessage());
                    } catch (ClassNotFoundException e1) {
                        new ErrorDialog(null, e1.getMessage());
                    }
                }
            }
        });

        // Get button listener - OnClick
        getButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                String filePath = "";
                try {
                    int index = ListPanel.getFilesList().getSelectedIndex();
                    if (index != -1) {
                        cm.defineGetRequest(ListPanel.getFilesList().getSelectedValue());
                    } else {
                        new ErrorDialog(null, "No file selected.\n" +
                                "Please select one from the list.");
                    }
                } catch (InterruptedException e1) {
                    new File(filePath).delete(); // If file exists, deletes it
                    new ErrorDialog(null, e1.getMessage());
                } catch (IOException e1) {
                    new File(filePath).delete(); // If file exists, deletes it
                    new ErrorDialog(null, e1.getMessage());
                } catch (ClassNotFoundException e1) {
                    new File(filePath).delete(); // If file exists, deletes it
                    new ErrorDialog(null, e1.getMessage());
                }
            }
        });

        // Remove button listener - OnClick
        removeButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int index = ListPanel.getFilesList().getSelectedIndex();
                    if (index != -1) {
                        cm.defineRemoveRequest(ListPanel.getFilesList().getSelectedValue());
                    } else {
                        new ErrorDialog(null, "No file selected.\nPlease select one from the list.");
                    }
                } catch (IOException e1) {
                    new ErrorDialog(null, e1.getMessage());
                } catch (InterruptedException e1) {
                    new ErrorDialog(null, e1.getMessage());
                }
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
