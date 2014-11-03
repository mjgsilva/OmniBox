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
     *  -2: Send File
     *  -1: exit
     *   0: refresh file list
     *
     * @return the selected option
     */
    private int printMenu() {
        String selectedOption;
        int valueToReturn;
        while (true) {
            printHeader("Choose an option");
            System.out.println("");
            System.out.println("S - Send File\tR - Refresh\tE - Exit");
            System.out.println("Server file list: ");
            client.getFileListToString();

            selectedOption = sc.next();

            // Convert to default values if necessary
            if (selectedOption.equalsIgnoreCase("s"))
                selectedOption = "-2";
            else if (selectedOption.equalsIgnoreCase("r"))
                selectedOption = "0";
            else if (selectedOption.equalsIgnoreCase("e"))
                selectedOption = "-1";

            try {
                valueToReturn = Integer.parseInt(selectedOption);
            } catch (NumberFormatException e) {
                // If option selected is not a number
                System.out.println("Selected option is invalid.");
                continue;
            }

            // If valueToReturn is in this range, then it's valid so break.
            if(valueToReturn <= client.getFileListSize() && valueToReturn >= -2)
                break;
        }
        return valueToReturn;
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
        int fileIndex;

        switch ((fileIndex = printMenu())) {
            case -2:
                break;
            case -1:
                break;
            case 0:
                break;
            default:
                while (true) {
                    System.out.println("\n1 - Get File number \"" + fileIndex + "\"\n"
                            + "2 - Remove File number \"" + fileIndex + "\"\n"
                            + "0 - Go back");
                    int option = sc.nextInt();
                    if (option == 1)
                        client.defineGetRequest(client.getFile(fileIndex - 1));
                    else if (option == 2)
                        client.defineRemoveRequest(client.getFile(fileIndex - 1));
                    else if (option == 0)
                        break;
                    else {
                        System.out.println("Invalid option");
                        continue;
                    }
                    // Only breaks if selected option is valid
                    break;
                }
                break;
        }
    }

    private void waitAnswer() {

    }
}
