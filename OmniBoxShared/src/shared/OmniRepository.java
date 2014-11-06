package shared;
import communication.CommunicationAdapter;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;

/**
 * Created by OmniBox on 02/11/14.
 */

public class OmniRepository extends CommunicationAdapter{
    private int port;
    private String addressServer;
    private int serverPort;
    public  ServerSocket socket;
    private String filesDirectory;


    private final HashSet<OmniFile> fileList = new HashSet();

    private final int oppNum = 0;

    public OmniRepository(int port, String addressServer, String filesDirectory) throws IOException {
        this.port = port;
        this.addressServer = addressServer;
        this.filesDirectory = filesDirectory;
    }

    public OmniRepository(int port) throws IOException {
        this.port = port;
        this.addressServer = sendMulticastMessage(Constants.REQUEST_SERVER_IP_ADDRESS, this.port);
        this.filesDirectory = "";
    }
}
