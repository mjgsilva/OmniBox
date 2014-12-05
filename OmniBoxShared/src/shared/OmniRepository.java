package shared;
import communication.CommunicationAdapter;
import shared.FileOperations;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by OmniBox on 02/11/14.
 */

public class OmniRepository extends CommunicationAdapter implements Serializable{
    private int port;
    private String addressServer;
    private int serverPort;
    private transient ServerSocket socket;
    private InetAddress serverAddr;
    private InetAddress localAddr;
    private transient DatagramSocket socketUDP;
    private transient DatagramPacket packet;
    private String filesDirectory;
    private final HashSet<OmniFile> fileList = new HashSet();


    private int oppNum = 0;

    public OmniRepository(int port, String addressServer, String filesDirectory) throws IOException {
        this.port = port;
        this.serverPort = 6000;//port;
        this.addressServer = addressServer;
        this.filesDirectory = filesDirectory;
        this.localAddr = InetAddress.getLocalHost();
        socket = new ServerSocket(port);
        setUDPSocket();
    }

    public OmniRepository(int port) throws IOException {
        this.port = port;
        this.serverPort = port;
        this.addressServer = sendMulticastMessage(Constants.REQUEST_SERVER_IP_ADDRESS, port);
        this.filesDirectory = "";
        this.localAddr = InetAddress.getLocalHost();
        socket = new ServerSocket(port);
        setUDPSocket();
    }

    public InetAddress getLocalAddr() {return localAddr;}
    public InetAddress getServerAddr() {return serverAddr;}
    public DatagramSocket getSocketUDP() {
        return socketUDP;
    }

    private void setUDPSocket() throws SocketException, UnknownHostException {
        //Socket Udp to communicate operations
        serverAddr = InetAddress.getByName(addressServer);
        socketUDP = new DatagramSocket();
        socketUDP.setSoTimeout(Constants.TIMEOUT);
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


    public void sendNotification(int operation, int status,String fileName,User user,Boolean isSuccessful){
        try {
            ArrayList<Object> tempList = new ArrayList<Object>();
            tempList.add(operation);
            tempList.add(status);
            tempList.add(new OmniFile(fileName));
            tempList.add(user);
            tempList.add(isSuccessful);
            tempList.add(this);
            Request reqTemp = new Request(Constants.CMD.cmdNotification,tempList);

            sendUDPMessage(socketUDP,serverAddr,serverPort,reqTemp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String fileName,User user){

        oppNum++;
       // sendNotification(Constants.OP_DELETE,Constants.OP_S_STARTED,fileName,user,true);
        //find file and delete
        for(OmniFile file : fileList){
            if(file.getFileName().equalsIgnoreCase(fileName))
            {
                file.delete();
                fileList.remove(file);
            }
        }
        sendNotification(Constants.OP_DELETE,Constants.OP_S_FINISHED,fileName,user,true);
        oppNum--;
    }

    public void sendFile(Socket socket,OmniFile omnifile,User user) throws IOException, InterruptedException {

        oppNum++;
        //sendNotification(Constants.OP_UPLOAD,Constants.OP_S_STARTED,omnifile.getFileName(),user,true);
        //find file and send
        for(OmniFile file : fileList){
            if(file.equals(omnifile))
            {
                try {
                    FileOperations.readFileToSocket(socket, file);
                }catch (Exception e){
                    sendNotification(Constants.OP_UPLOAD,Constants.OP_S_FINISHED,omnifile.getFileName(),user,false);
                }
                break;
            }
        }
        sendNotification(Constants.OP_UPLOAD,Constants.OP_S_FINISHED,omnifile.getFileName(),user,true);
        oppNum--;
    }

    public void getFile(Socket socket, String fileName,User user) throws IOException, InterruptedException, ClassNotFoundException {

        oppNum++;
        //sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_STARTED,fileName,user,true);

        OmniFile tempFile= null;
        try {
            tempFile = (OmniFile) FileOperations.saveFileFromSocket(socket, filesDirectory + fileName);
        }catch (Exception e){
            sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_FINISHED,tempFile.getFileName(),user,false);
        }

        sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_FINISHED,fileName,user,true);

        fileList.add(tempFile);
        oppNum--;
    }

    public boolean fileExists(OmniFile omniFile) {
        return fileList.contains(omniFile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OmniRepository that = (OmniRepository) o;

        if (port != that.port) return false;
        if (!localAddr.equals(that.localAddr)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + localAddr.hashCode();
        return result;
    }
}
