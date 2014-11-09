package ui.graphic;

import logic.Client;

import java.io.IOException;
import java.util.Scanner;

/**
 * Graphical interface starts normally if port and server ip address are given via terminal args.
 * If not, then the user stays in terminal interface until server ip address is identified, via
 * multicast in this case.
 *
 * Created by OmniBox on 08-11-2014.
 */
public class UIGraphic {
    Client client;
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Missing arguments! Should be something like this...\n>java UIText port [ServerIP] [LocalDirectory]");
            System.exit(0);
        } else {
            System.out.println("Starting OmniBox Client...\n");
            System.out.println("Configuring client setup...\n");
            new UIGraphic().startInterface(args);
        }

    }

    /**
     * Responsible to build the Client object configurations
     *
     * Graphical interface (MainFrame) is started here, if no errors were found.
     *
     * @param args
     */
    private void startInterface(String[] args) {
        try {
            if ((this.client = Client.buildClient(args)) == null) {
                System.err.println("[ERROR] Invalid arguments! ");
                return;
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Unable to connect to server on default address 127.0.0.1 "
                    + ".\nTry to find server IP address via Multicast? (Y/N)");
            if (sc.next().equalsIgnoreCase("y")) {
                try {
                    this.client = Client.buildClient(new String[]{args[0], "$multicast$"});
                } catch (IOException e1) {
                    System.out.println("[ERROR] There was an error when trying to retrieve server address. Try again later.");
                    return;
                }
            } else {
                System.out.println("System shutting down...");
                System.exit(0);
            }
        }

        System.out.println("Starting graphical interface...\n");
        new MainFrame(client);
    }
}
