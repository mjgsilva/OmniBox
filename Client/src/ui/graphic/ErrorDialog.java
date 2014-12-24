package ui.graphic;

import javax.swing.*;

/**
 * ErrorDialog.
 * Prompts a dialog box with desired error message.
 *
 * Created by OmniBox on 29-11-2014.
 */
public class ErrorDialog {

    /**
     * ErrorDialog constructor.
     * Dialog title is always: OmniBox - Error
     * Dialog icon is always: JOptionPane.ERROR_MESSAGE
     *
     * @param parent if the desired behavior is to block parent send parent here. If there's no need to block, fill this with null value
     * @param message this is the message shown on the body of the dialog box.
     */
    public ErrorDialog(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "OmniBox - Error", JOptionPane.ERROR_MESSAGE);
    }
}
