package server;

import communication.CommunicationAdapter;
import database.FilesDB;
import database.RepositoriesDB;
import database.UsersDB;
import shared.Constants;
import shared.OmniRepository;
import shared.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class OmniServer extends CommunicationAdapter {
    final private int port;
    private ServerSocket serverSocket;
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
        Socket socket;

        try {
            ProcessRepository pr = new ProcessRepository(port,this);
            pr.start();
            while (true) try {
                socket = serverSocket.accept();
                socket.setSoTimeout(Constants.TIMEOUT);
                ProcessClient pc = new ProcessClient(socket, this);
                pc.start();
            } catch (IOException ioe) {
                System.out.println("Error: " + ioe.getMessage());
                return;
            }
        } finally {
            try{
                serverSocket.close();
            }catch (IOException ioe) {
                System.out.println("Error closing ServerSocket: " + ioe);
            }
        }
    }

    public boolean login(User user) {
        return usersDB.login(user);
    }
}