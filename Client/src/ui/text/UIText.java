package ui.text;

import logic.Client;
import logic.state.StateInterface;
import logic.state.WaitAnswer;
import logic.state.WaitAuthentication;
import logic.state.WaitRequest;

import java.io.IOException;
import java.util.Scanner;

/**
 * Text Interface
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class UIText {

    Client client;
    StateInterface currentState;
    Scanner sc = new Scanner(System.in);

    /**
     * This program is intended to receive:
     *      - port
     *      - serverIpAddress [OPTIONAL]
     *      - directoryToSaveFilesTo [OPTIONAL]
     *
     * @param args
     */
    public static void main (String[] args) {
        if (args.length < 1) {
            System.out.println("Missing arguments! Should be something like this...\n>java UIText port [ServerIP] [LocalDirectory]");
            System.exit(0);
        } else
            new UIText().startInterface(args);
    }

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
                    this.client=Client.buildClient(new String[] {args[0], "$multicast$"});
                } catch (IOException e1) {
                    System.out.println("[ERROR] There was an error when trying to retrieve server address. Try again later.");
                    return;
                }
            } else {
                System.out.println("System shutting down...");
                System.exit(0);
            }
        }

        printHeader("Client");

        while (true) {
            currentState = client.getCurrentState();
            if (currentState instanceof WaitAuthentication)
                waitAuthentication();
            else if (currentState instanceof WaitRequest)
                waitRequest();
            else if (currentState instanceof WaitAnswer)
                waitAnswer();
        }
    }

    private void printHeader(String message) {
        System.out.println("-------------------------------------------");
        System.out.println("\t\t\t- OmniBox -");
        System.out.println("\t\t\t  " + message);
        System.out.println("-------------------------------------------");
    }

    /**
     * Prints available files to the user.
     *
     * Defined return values:
     *  -1: exit
     *   0: refresh file list
     *
     * @return the selected option
     */
    private int printMenu() {
        printHeader("Choose an option");
        System.out.println("");
        System.out.println("R - Refresh\tE - Exit");
        System.out.println("File List: ");
        client.getFileListToString();

        return sc.nextInt();
    }

    private void waitAuthentication() {
        String username, password;
        System.out.println("::Login Form::");
        System.out.print("Username: ");
        username = sc.next();
        System.out.print("Password: ");
        password = sc.next();

        client.defineAuthentication(username, password);
    }

    private void waitRequest() {
        switch (printMenu()) {

        }
    }

    private void waitAnswer() {

    }
}
