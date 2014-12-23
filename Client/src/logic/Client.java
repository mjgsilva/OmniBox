package logic;

import communication.CommunicationAdapter;
import logic.state.StateInterface;
import logic.state.WaitAuthentication;
import shared.Constants;
import shared.OmniFile;
import shared.User;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;


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
    private OmniFile fileToUpload = null;
    private Socket repositorySocket = null;
    private User user;


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
                if (args[1].equalsIgnoreCase("$multicast$")) {
                    try {
                        temp.port = Integer.parseInt(args[0]);
                        if (temp.port <= 0)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    temp.defineMulticastRequest();
                } else
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

    /**
     *
     * @return current state.
     */
    public StateInterface getCurrentState() {
        return currentState;
    }
    public void setCurrentState(StateInterface currentState) {this.currentState = currentState;}

    @Override
    public void defineAuthentication(String username, String password) throws IOException, InterruptedException, ClassNotFoundException {
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

    /**
     * @return server port.
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @param port server port.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     *
     * @return server IP address.
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     *
     * @param serverIP new server IP address
     */
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     *
     * @return local directory path
     */
    public String getLocalDirectoryPath() {
        return localDirectoryPath;
    }

    /**
     * Sets local directory path.
     * Just use this if this directory changes (For some reason...).
     *
     * @param localDirectoryPath local Direcotry Path
     */
    public void setLocalDirectoryPath(String localDirectoryPath) {
        this.localDirectoryPath = localDirectoryPath;
    }

    /**
     *
     * @return server socket.
     */
    public Socket getServerSocket() {
        return serverSocket;
    }

    /**
     * File get is the one that is going to be sent.
     * Should be set to null when file upload is complete.
     *
     * @return file to upload.
     */
    public OmniFile getFileToUpload() {
        return fileToUpload;
    }

    /**
     * File set is the one that is going to be sent.
     * Should be set to null when file upload is complete.
     *
     * @param fileToUpload file to upload.
     */
    public void setFileToUpload(OmniFile fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    /**
     * This repository socket may vary. This should be set to null, when repository socket is
     * disconnected.
     *
     * @return current repository socket.
     */
    public Socket getRepositorySocket() {
        return this.repositorySocket;
    }

    /**
     * This repository socket may vary. This should be set to null, when repository socket is
     * disconnected.
     *
     * @param repositorySocket set current repository socket.
     */
    public void setRepositorySocket(Socket repositorySocket) {
        this.repositorySocket = repositorySocket;
    }

    /**
     *
     * @return current logged in user. It might not be a valid login.
     */
    public User getUser() {
        return user;
    }

    /**
     *
     * @param user logged in user data. It might not be a valid login.
     */
    public void setUser(User user) {
        this.user = user;
    }
}
