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
import java.net.ServerSocket;
import java.net.Socket;

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
        repositoriesDB = new RepositoriesDB();
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
            while (true)
                try {
                socket = serverSocket.accept();
                socket.setSoTimeout(Constants.TIMEOUT);
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

    @Override
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException { return super.getTCPMessage(socket); }

    @Override
    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException { super.sendTCPMessage(socket, cmd); }

    @Override
    public Request getUDPMessage(DatagramSocket datagramSocket) throws InterruptedException, IOException, ClassNotFoundException { return super.getUDPMessage(datagramSocket); }

    @Override
    public void sendUDPMessage(DatagramSocket datagramSocket, Request cmd) throws InterruptedException, IOException { super.sendUDPMessage(datagramSocket,cmd); }

    public boolean login(User user) {
        return usersDB.login(user);
    }

    public OmniRepository getLessWorkloadedRepository() { return repositoriesDB.getRepositoriesAvailability(); }

    public boolean fileExists(OmniFile omniFile) { return filesDB.fileExists(omniFile); }

    public void removeExpiredRepositories() { repositoriesDB.removeExpiredRepositories(); }
}