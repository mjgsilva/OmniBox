package shared;
import communication.CommunicationAdapter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

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

    public void deleteFile(OmniFile omniFile){

        //find file and delete
        for(OmniFile file : fileList){
            if(file.equals(omniFile))
            {
                file.delete();
                fileList.remove(file);
            }
        }
    }

    public void sendFile(Socket socket,OmniFile omnifile) throws IOException, InterruptedException {
        //find file and send
        for(OmniFile file : fileList){
            if(file.equals(omnifile))
            {
                sendFile(socket,file);
            }
        }
    }

    protected void getFile(Socket socket,OmniFile file) throws IOException, InterruptedException, ClassNotFoundException {
        OmniFile tempFile= null;
        tempFile = getFile(socket);

        fileList.add(tempFile);
    }
}
