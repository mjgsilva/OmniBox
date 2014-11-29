package shared;
import communication.CommunicationAdapter;
import shared.FileOperations;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;

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
    public boolean firstStart=true;


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

    public DatagramSocket getSocketUDP() {
        return socketUDP;
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


    public OmniFile getOmniFileByName(String fileName){
        for (OmniFile omniFile : fileList) {
            if(omniFile.getFileName().equalsIgnoreCase(fileName)){
                return omniFile;
            }
        }
        return null;
    }
    //END-Gets


    public void sendNotification(int operation, int status){
        try {
            ArrayList<Object> tempList = new ArrayList<Object>();
            tempList.add(operation);
            tempList.add(status);
            Request reqTemp = new Request(Constants.CMD.cmdNotification,tempList);

            sendUDPMessage(socketUDP,reqTemp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String fileName){

        sendNotification(Constants.OP_DELETE,Constants.OP_S_STARTED);
        //find file and delete
        for(OmniFile file : fileList){
            if(file.getFileName().equalsIgnoreCase(fileName))
            {
                file.delete();
                fileList.remove(file);
            }
        }
        sendNotification(Constants.OP_DELETE,Constants.OP_S_FINISHED);
    }

    public void sendFile(Socket socket,OmniFile omnifile) throws IOException, InterruptedException {

        sendNotification(Constants.OP_SEND_FILE,Constants.OP_S_STARTED);
        //find file and send
        for(OmniFile file : fileList){
            if(file.equals(omnifile))
            {
                FileOperations.readFileToSocket(socket, file);
                break;
            }
        }
        sendNotification(Constants.OP_SEND_FILE,Constants.OP_S_FINISHED);
    }

    public void getFile(Socket socket, String fileName) throws IOException, InterruptedException, ClassNotFoundException {

        sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_STARTED);

        OmniFile tempFile= null;
        tempFile = (OmniFile) FileOperations.saveFileFromSocket(socket, filesDirectory + File.separator + fileName);

        fileList.add(tempFile);
        sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_FINISHED);
    }
}
