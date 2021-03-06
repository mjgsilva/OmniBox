package ui.graphic;

import logic.Client;
import logic.ClientModel;
import logic.state.WaitAnswer;
import logic.state.WaitAuthentication;
import logic.state.WaitRequest;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Main Frame.
 *
 * This is the principal frame of the application, is constituted by the following panels:
 *      - ButtonsPanel
 *      - ListPanel
 *
 * Created by OmniBox on 08-11-2014.
 */
public class MainFrame extends JFrame implements Observer {
    private final ClientModel cm;
    private JPanel listPanel;
    private JPanel buttonsPanel;

    /**
     * This constructor expects that the client object has been previously built, under all
     * necessary restrictions and configurations defined on the project.
     *
     * This also builds the main frame window for this client application.
     *
     * @param client
     */
    public MainFrame(Client client) {
        // Create model with the given client
        cm = new ClientModel(client);
        // Add this frame as a observer
        cm.addObserver(this);

        setFrameLayout();

        setSize(500, 500);
        setTitle("OmniBox - Client");
        setVisible(true);
        setResizable(false); // So it's not necessary to create anchors
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cm.sendNotification();
    }

    /**
     * Sets layout configurations and add components to content Panes.
     */
    private void setFrameLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        listPanel = new ListPanel(cm);
        buttonsPanel = new ButtonsPanel(cm);

        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);
        buttonsPanel.setAlignmentY(CENTER_ALIGNMENT);

        // Add listPanel to Center and buttonsPanel to South
        contentPane.add(listPanel, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Authentication process is made on this update.
     *
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        if (cm.getCurrentState() instanceof WaitAuthentication) {
            // Show dialog with login form
            // Disable parent window
            setEnabled(false);

            JPanel panel = new JPanel(new BorderLayout(5, 5));

            JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
            label.add(new JLabel("Username:", SwingConstants.RIGHT));
            label.add(new JLabel("Password:", SwingConstants.RIGHT));
            panel.add(label, BorderLayout.WEST);

            JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
            JTextField username = new JTextField("");
            controls.add(username);
            JPasswordField password = new JPasswordField("");
            controls.add(password);
            panel.add(controls, BorderLayout.CENTER);

            int value = JOptionPane.showConfirmDialog(this, panel, "Login - OmniBox", JOptionPane.OK_CANCEL_OPTION);

            // Validate data
            try {
                if (value == JOptionPane.CANCEL_OPTION) {
                    try {
                        cm.closeServerSocket();
                    } catch (IOException e) {
                        System.out.println("Error closing server socket...");
                    }
                    System.out.println("OmniBox is shutting down...");
                    System.exit(0);
                }
                // Validation is done inside the state. Is done via exceptions.
                cm.defineAuthentication(username.getText(), new String(password.getPassword()));
            } catch (IOException e) {
                new ErrorDialog(null, e.getMessage());
                cm.sendNotification();
            } catch (InterruptedException e) {
                new ErrorDialog(null, e.getMessage());
                cm.sendNotification();
            } catch (Exception e) {
                new ErrorDialog(null, e.getMessage());
                cm.sendNotification();
            }
        } else if (cm.getCurrentState() instanceof WaitRequest) {
            // Enable components
            setEnabled(true);
            cm.setFileToUpload(null);
        } else if (cm.getCurrentState() instanceof WaitAnswer) {
            //setEnabled(false);
        }
    }
}
