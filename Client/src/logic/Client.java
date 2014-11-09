package logic;

import communication.CommunicationAdapter;
import logic.state.StateInterface;
import logic.state.WaitAuthentication;
import shared.Constants;
import shared.OmniFile;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;

//import omniboxshared.shared.OmniFile;

/**
 * This class represents the client program itself.
 * It's initialized on the first state, WaitAuthentication.
 * It's part of the model.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class Client extends Observable implements ClientInterface {
    private StateInterface currentState;
    private int port;
    private String serverIP = "127.0.0.1"; // default
    private String localDirectoryPath = System.getProperty("user.dir"); // default - current directory
    private Socket serverSocket;

    private ArrayList<OmniFile> fileList = new ArrayList<OmniFile>();


    /**
     * Client Constructor
     *
     * Client instances start on WaitAuthentication state.
     */
    public Client() {
        this.currentState = new WaitAuthentication(this);
    }

    /**
     * Client factory method.
     * Returns Client instance according to introduced args.
     *
     * $multicast$ tag on args[1] means that the factory has to return an object that the address
     * was retrieved with a Multicast request to server.
     *
     * @param args
     * @return
     */
    public static Client buildClient(String[] args) throws IOException {
        Client temp = new Client();
        switch (args.length) {
            case 3:
                temp.localDirectoryPath = args[2];
            case 2:
                if (args[1].equalsIgnoreCase("$multicast$"))
                    temp.defineMulticastRequest();
                else
                    temp.serverIP = args[1];
            case 1:
                try {
                    temp.port = Integer.parseInt(args[0]);
                    if (temp.port <= 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    return null;
                }
                // If serverIP is wrong it throws an IOException, caught on interface
                temp.serverSocket = new Socket(temp.serverIP, temp.port);
                return temp;
            default:
                return null;
        }
    }

    public StateInterface getCurrentState() {
        return currentState;
    }

    @Override
    public void defineAuthentication(String username, String password) throws IOException, InterruptedException {
        currentState = currentState.defineAuthentication(username, password);
    }

    @Override
    public void defineGetRequest(final OmniFile fileToGet) throws InterruptedException, IOException, ClassNotFoundException {
        currentState = currentState.defineGetRequest(fileToGet);
    }

    @Override
    public void defineSendRequest(final OmniFile fileToSend) throws InterruptedException, IOException, ClassNotFoundException {
        currentState = currentState.defineSendRequest(fileToSend);
    }

    @Override
    public void defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException {
        currentState = currentState.defineRemoveRequest(fileToRemove);
    }

    @Override
    public void defineReturnToRequest() {
        currentState = currentState.defineReturnToRequest();
    }

    @Override
    public void defineMulticastRequest() throws IOException {
        currentState = currentState.defineMulticastRequest();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getLocalDirectoryPath() {
        return localDirectoryPath;
    }

    public void setLocalDirectoryPath(String localDirectoryPath) {
        this.localDirectoryPath = localDirectoryPath;
    }

    /**
     * Returns file list according to UIText expectancies.
     * If fileList is empty returns "No files available on server"
     *
     * @return
     */
    public String getFileListToString() {
        String list = "";

        for (int i = 0; i < fileList.size(); i++)
            list += (i + 1) + " - " + fileList.get(i).toString() + "\n";

        return fileList.size() > 0 ? list : (list = "No files available on server");
    }

    public int getFileListSize() {
        return fileList.size();
    }

    public File getFile(int index) {
        return new File(fileList.get(index).getFileName());
    }

    public Socket getServerSocket() {
        return serverSocket;
    }
}
