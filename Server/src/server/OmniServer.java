package server;

import communication.CommunicationAdapter;
import database.FilesDB;
import database.RepositoriesDB;
import database.UsersDB;
import shared.*;
import threads.HeartBeatHandler;
import threads.ProcessClient;
import threads.ProcessRepository;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OmniServer extends CommunicationAdapter {
    final private int port;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    final private UsersDB usersDB;
    final private RepositoriesDB repositoriesDB;
    final private FilesDB filesDB;

    public OmniServer(int port, UsersDB usersDB) {
        this.port = port;
        this.usersDB = usersDB;
        repositoriesDB = new RepositoriesDB(this);
        filesDB = new FilesDB();
    }

    public void omniServerStart() throws IOException{
        serverSocket = new ServerSocket(port);
        datagramSocket = new DatagramSocket(port);
        Socket socket;

        try {
            ProcessRepository processRepository = new ProcessRepository(datagramSocket,this);
            HeartBeatHandler heartBeatHandler = new HeartBeatHandler(this);
            processRepository.start();
            heartBeatHandler.start();
            while (true)
                try {
                    socket = serverSocket.accept();
                    ProcessClient processClient = new ProcessClient(socket, this);
                    processClient.start();
                } catch (IOException ioe) {
                    System.out.println("Error: " + ioe.getMessage());
                    return;
                }
        } finally {
            try{
                if(serverSocket!=null)
                    serverSocket.close();
                usersDB.serializeDB();
            }catch (IOException ioe) {
                System.out.println("Error closing ServerSocket: " + ioe);
            }
        }
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    @Override
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException { return super.getTCPMessage(socket); }

    @Override
    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException { super.sendTCPMessage(socket, cmd); }

    @Override
    public Request getUDPMessage(DatagramSocket datagramSocket) throws InterruptedException, IOException, ClassNotFoundException { return super.getUDPMessage(datagramSocket); }

    @Override
    public void sendUDPMessage(DatagramSocket datagramSocket,InetAddress inetAddress,int port,Request cmd) throws InterruptedException, IOException { super.sendUDPMessage(datagramSocket,inetAddress,port,cmd); }

    //UsersDB
    public boolean login(final User user) {
        return usersDB.login(user);
    }

    public void addUserActivity(final User user) {
        usersDB.addUserActivity(user);
    }

    public void editUserActivity(final User user, final Integer activityType) {
        usersDB.editUserActivity(user,activityType);
    }

    public void removeUserActivity(final User user) {
        usersDB.remoteUserActivity(user);
    }

    public void addSocket(final User user,final Socket socket) {
        usersDB.addSocket(user, socket);
    }

    public void removeSocket(final User user) {
        usersDB.removeSocket(user);
    }

    public void notifyClients() {
        usersDB.notifyUsers(filesDB.fileList(),this);
    }

    //RepositoriesDB
    public void addRepository(final OmniRepository omniRepository) {
        repositoriesDB.addRepo(omniRepository);
    }

    public HashSet<OmniRepository> getRepositories() { return repositoriesDB.getRepositories(); }

    public int getNumberOfRepositories() {
        return repositoriesDB.getNumberOfRepositories();
    }

    public OmniRepository getLessWorkloadedRepository() {
        return repositoriesDB.getLessWorkLoadedRepository();
    }

    public void removeExpiredRepositories() {
        repositoriesDB.removeExpiredRepositories();
    }

    public OmniRepository getDownloadSource(final OmniFile omniFile) {
        return repositoriesDB.getDownloadSource(omniFile);
    }

    public void deleteBroadcast(final Request response) {
        repositoriesDB.deleteBroadcast(response);
    }

    public void replicationProcess(final OmniFile omniFile) {
        repositoriesDB.replicationProcess(omniFile);
    }

    //FilesDB
    public boolean fileExists(final OmniFile omniFile) {
        return filesDB.fileExists(omniFile);
    }

    public void addFile(final OmniFile omniFile) {
        filesDB.addFile(omniFile);
    }

    public void removeFile(final OmniFile omniFile) {
        filesDB.removeFile(omniFile);
    }

    public void rebuildFileList(final OmniRepository omniRepository) { filesDB.rebuildFileList(omniRepository); }

    public void rebuildFileList(final HashSet<OmniRepository> omniRepositories) { filesDB.rebuildFileList(omniRepositories); }

    public boolean fileBeingAccessed(final OmniFile omniFile) {
        return filesDB.isFileBeingAccessed(omniFile);
    }

    public void addAccessToFile(final User user, final OmniFile omniFile) {
        filesDB.addAccessToFile(user,omniFile);
    }

    public void removeAccessToFile(final User user) {
        filesDB.removeAccessToFile(user);
    }

    public ArrayList getFileList() {
        return filesDB.fileList();
    }

}