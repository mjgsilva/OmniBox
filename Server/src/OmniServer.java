import omniboxshared.communication.TCP;
import omniboxshared.communication.TCPAdapter;
import omniboxshared.communication.UDP;
import omniboxshared.communication.UDPAdapter;
import omniboxshared.shared.Constants;
import omniboxshared.shared.OmniRepository;
import org.omg.CORBA.TIMEOUT;
import threads.ProcessClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class OmniServer extends TCPAdapter {
    private int port;
    private Database usersDB;
    private ArrayList<OmniRepository> repoDB;
    private ServerSocket serverSocket;

    public OmniServer(int port, Database usersDB) {
        this.port = port;
        this.usersDB = usersDB;
        repoDB = new ArrayList<OmniRepository>();
    }

    public void omniServerStart() throws IOException{
        serverSocket = new ServerSocket(port);
        Socket socket;

        try {
            while (true) {
                try {
                    socket = serverSocket.accept();
                    socket.setSoTimeout(Constants.TIMEOUT);
                    ProcessClient pc = new ProcessClient(socket);
                    pc.start();
                } catch (IOException ioe) {
                    System.out.println("Error: " + ioe.getMessage());
                    return;
                }

            }
        } finally {
            try{
                serverSocket.close();
            }catch (IOException ioe) {
                System.out.println("Error closing ServerSocket: " + ioe);
            }
        }
    }
}