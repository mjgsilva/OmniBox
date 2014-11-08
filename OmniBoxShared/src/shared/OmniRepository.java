package shared;
import communication.CommunicationAdapter;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by OmniBox on 02/11/14.
 */

public class OmniRepository extends CommunicationAdapter{
    private int port;
    private String addressServer;
    private int serverPort;
    private ServerSocket socket;
    private InetAddress serverAddr;
    private DatagramSocket socketUDP;
    private DatagramPacket packet;
    private String filesDirectory;
    private final HashSet<OmniFile> fileList = new HashSet();


    private final int oppNum = 0;

    public OmniRepository(int port, String addressServer, String filesDirectory) throws IOException {
        this.port = port;
        this.addressServer = addressServer;
        this.filesDirectory = filesDirectory;
        setUDPSocket();
    }

    public OmniRepository(int port) throws IOException {
        this.port = port;
        this.addressServer = sendMulticastMessage(Constants.REQUEST_SERVER_IP_ADDRESS, this.port);
        this.filesDirectory = "";
        setUDPSocket();
    }

    private void setUDPSocket() throws SocketException, UnknownHostException {
        //Socket Udp to communicate operations
        serverAddr = InetAddress.getByName(addressServer);
        socketUDP = new DatagramSocket();
        socketUDP.setSoTimeout(Constants.TIMEOUT *1000);
    }

    //Gets

    public ServerSocket getSocket() {
        return socket;
    }

    public int getPort() {
        return port;
    }

    public String getAddressServer() {
        return addressServer;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getFilesDirectory() {
        return filesDirectory;
    }

    public HashSet<OmniFile> getFileList() {
        return fileList;
    }

    public int getOppNum() {
        return oppNum;
    }

    //END-Gets

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
