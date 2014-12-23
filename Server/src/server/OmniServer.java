package server;

import communication.CommunicationAdapter;
import database.FilesDB;
import database.RepositoriesDB;
import database.UsersDB;
import service.StatusBoardServiceImpl;
import shared.*;
import threads.HeartBeatHandler;
import threads.ProcessClient;
import threads.ProcessMulticast;
import threads.ProcessRepository;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashSet;

public class OmniServer extends CommunicationAdapter {
    final private int port;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    final private UsersDB usersDB;
    final private RepositoriesDB repositoriesDB;
    final private FilesDB filesDB;
    private StatusBoardServiceImpl statusBoardService;

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
            ProcessMulticast processMulticast = new ProcessMulticast(port);
            ProcessRepository processRepository = new ProcessRepository(datagramSocket,this);
            HeartBeatHandler heartBeatHandler = new HeartBeatHandler(this);
            processMulticast.start();
            processRepository.start();
            heartBeatHandler.start();
            startRMIService();
            while (true)
                try {
                    socket = serverSocket.accept();
                    ProcessClient processClient = new ProcessClient(socket, this);
                    processClient.start();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
        } catch (Exception e) {e.printStackTrace();} finally {
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

    private void startRMIService() {
        try {
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            } catch (RemoteException e) {
                registry = LocateRegistry.getRegistry();
            }
            statusBoardService = new StatusBoardServiceImpl();
            registry.bind("StatusBoardService", statusBoardService);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendServiceNotification() { statusBoardService.notifyObservers(getNotification()); }

    public String getNotification() {
        StringBuilder notification = new StringBuilder();
        notification.append("*** Users: " + usersDB.getNumberOfLoggedUsers() + " / " +usersDB.getNumberOfUsers() + " ***" + "\n");
        notification.append(usersDB.getUsersActivity());
        notification.append("*** Files: " + filesDB.getNumberOfFilesBeingAccessed() + " / " + filesDB.getNumberOfFiles() + " ***" + "\n");
        for(OmniFile omniFile : filesDB.getFiles()) {
            notification.append(omniFile.getFileName() + " : " + repositoriesDB.getNumberOfReplicas(omniFile) + "\n");
        }
        notification.append("*** Repositories: " + repositoriesDB.getNumberOfRepositories() + " ***" + "\n");
        for(OmniRepository omniRepository : repositoriesDB.getRepositories()) {
            notification.append(omniRepository.getLocalAddr() + " : " + omniRepository.getPort() + "\n");
        }
        return notification.toString();
    }

    //UsersDB
    public boolean login(final User user) {
        return usersDB.login(user);
    }

    public void addUserActivity(final User user) {
        usersDB.addUserActivity(user);
    }

    public void editUserActivity(final User user, final Integer activityType) {
        usersDB.editUserActivity(user, activityType);
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

    public synchronized void notifyClients() {
        usersDB.notifyUsers(filesDB.fileList(), this);
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

    public boolean removeFile(final OmniFile omniFile) {
        return filesDB.removeFile(omniFile);
    }

    public void rebuildFileList(final OmniRepository omniRepository) { filesDB.rebuildFileList(omniRepository); }

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

    /**
     * Because delete event on watcher is catch after file is deleted from disk, it screws over
     * the references on fileList. LastMod is created specific for this situations.
     * We have to redo the fileList but not using the usual equals and hash code from OmniFile.
     *
     * <U>Note that</U> OmniFile/File lastModified saves milliseconds from epoch January first 1970
     * til the day it was modified.
     *
     * <U><H2>Important</H2></U>
     * LastMod from omniFile received, from repository, has the lastModified milliseconds from when the
     * file was deleted from the repository. So if size is equal and value of lastModified of aux
     * is inferior to the one on omniFile then we'll assume its the same file we're trying to delete.
     *
     * @param omniFile file to be excluded from file list
     * @return flag true if file was removed, false otherwise
     */
    public synchronized boolean customRemoveFile(OmniFile omniFile) {
        HashSet<OmniFile> newFileList = new HashSet<OmniFile>();
        boolean flag = false;
        for (OmniFile aux : filesDB.getFiles()) {
            if (aux.getFileName().equals(OmniFile.getOriginalFileName(omniFile.getFileName())) &&
                    aux.getFileSize() == omniFile.getFileSize() &&
                    aux.getLastModified() <= omniFile.getLastMod()) {
                // File to exclude, so do nothing.
                flag = true;
            } else
                newFileList.add(aux);
        }

        filesDB.setFiles(newFileList);

        return flag;
    }
}