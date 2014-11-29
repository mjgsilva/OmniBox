package ui.graphic;

import javax.swing.*;

/**
 * ErrorDialog.
 * Prompts a dialog box with desired error message.
 *
 * Created by OmniBox on 29-11-2014.
 */
public class ErrorDialog {

    public ErrorDialog(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "OmniBox - Error", JOptionPane.ERROR_MESSAGE);
    }
}
