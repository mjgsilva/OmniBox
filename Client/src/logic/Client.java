package logic;

import communication.CommunicationAdapter;
import logic.state.StateInterface;
import logic.state.WaitAuthentication;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

//import omniboxshared.shared.OmniFile;

/**
 * This class represents the client program itself.
 * It's initialized on the first state, WaitAuthentication.
 * It's part of the model.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class Client extends CommunicationAdapter implements ClientInterface {
    private StateInterface currentState;
    private int port;
    private String serverIP = "127.0.0.1"; // default
    private String localDirectoryPath = System.getProperty("user.dir"); // default - current directory
    private Socket serverSocket;


    // PROVISORY; THIS VALUES ARE GOING TO BE ON OMNIBOXSHARED
    //private final String MULTICAST_ADDRESS = "230.30.30.30";
    //private final int MAX_SIZE = 4000;
    // PROVISORY: MISSING OMNIFILE ON LIBRARY
    private ArrayList<String> fileList = new ArrayList<String>();

    /**
     * Client Constructor
     *
     * Client instances start on WaitAuthentication state.
     */
    private Client() {
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
                    temp.findServerIPByMulticast();
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
    public void defineAuthentication(String username, String password) {
        currentState = currentState.defineAuthentication(username, password);
    }

    @Override
    public void defineGetRequest(File fileToGet) {
        currentState = currentState.defineGetRequest(fileToGet);
    }

    @Override
    public void defineSendRequest(File fileToSend) {
        currentState = currentState.defineSendRequest(fileToSend);
    }

    @Override
    public void defineRemoveRequest(File fileToRemove) {
        currentState = currentState.defineRemoveRequest(fileToRemove);
    }

    @Override
    public void defineReturnToRequest() {
        currentState = currentState.defineReturnToRequest();
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
     * This method only gets called, if the user does not insert a valid server IP address
     *
     * Is sended a String object to server, and server response is also a String.
     * This response received is the server IP address.
     *
     * @throws IOException
     */
    public void findServerIPByMulticast() throws IOException {
        // Save value for server IP address
        this.serverIP = sendMulticastMessage("request_server_ip_address", this.port);
    }

    public String getFileListToString() {
        String list = "";

        return list;
    }

    public int getFileListSize() {
        return fileList.size();
    }

    public File getFile(int index) {
        return new File(fileList.get(index));
    }
}
