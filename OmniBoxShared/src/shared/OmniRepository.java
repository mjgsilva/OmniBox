package shared;
import communication.CommunicationAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.*;

/**
 * Created by OmniBox on 02/11/14.
 */

public class OmniRepository extends CommunicationAdapter implements Serializable{
    private int port;
    private String addressServer;
    private int serverPort;
    private transient ServerSocket socket;
    private InetAddress serverAddr;
    private String localAddr;
    private transient DatagramSocket socketUDP;
    private String filesDirectory;
    private final HashSet<OmniFile> fileList = new HashSet();
    private boolean notifyWatcher=true;


    private int oppNum = 0;

    public OmniRepository(int port, String addressServer, String filesDirectory, String myIp) throws IOException {
        this.port = port;
        this.serverPort = 6000;//port;
        this.addressServer = addressServer;
        this.filesDirectory = filesDirectory;
        this.localAddr = myIp;
        socket = new ServerSocket(port);
        setUDPSocket(port);
    }

    public OmniRepository(int port) throws IOException {
        this.port = port;
        this.serverPort = port;

        this.addressServer = sendMulticastMessage(Constants.REQUEST_SERVER_IP_ADDRESS, this.serverPort);
        this.filesDirectory = "";
        socket = new ServerSocket(port);
        setUDPSocket(port);
    }


    public void setLocalAdd(String addr) {this.localAddr = addr;}

    private void setUDPSocket(int port) throws SocketException, UnknownHostException {
        //Socket Udp to communicate operations
        serverAddr = InetAddress.getByName(addressServer);
        socketUDP = new DatagramSocket(port, serverAddr);
        //socketUDP.setSoTimeout(Constants.TIMEOUT);
    }

    //Gets
    public boolean getNotifyWatcher() {
        return notifyWatcher;
    }
    public void setNotifyWatcher(boolean notifyWatcher) {
        this.notifyWatcher = notifyWatcher;
    }

    public String getLocalAddr() {return localAddr;}
    public InetAddress getServerAddr() {return serverAddr;}
    public DatagramSocket getSocketUDP() {
        return socketUDP;
    }

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


    //public void sendNotification(int operation, int status,String fileName,User user,Boolean isSuccessful){
    public void sendNotification(int operation, int status,OmniFile omniFile,User user,Boolean isSuccessful){
            try {
            ArrayList<Object> tempList = new ArrayList<Object>();
            tempList.add(operation);
            tempList.add(status);
            tempList.add(omniFile);
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

    public synchronized void deleteFile(OmniFile omniFile,User user){
        notifyWatcher = false;
        oppNum++;
        sendNotification(Constants.OP_DELETE,Constants.OP_S_STARTED,omniFile,user,true);

        for(OmniFile file : fileList){
            if(file.equals(omniFile))
                file.delete();
        }

        fileList.remove(omniFile);

        oppNum--;
        sendNotification(Constants.OP_DELETE,Constants.OP_S_FINISHED,omniFile, user,true);
        notifyWatcher = true;
    }

    public synchronized void sendFile(Socket socket,OmniFile omnifile,User user) throws IOException, InterruptedException {

        oppNum++;
        sendNotification(Constants.OP_UPLOAD,Constants.OP_S_STARTED,omnifile,user,true);
        //find file and send
        if(fileList.contains(omnifile))
        {
            try {
                FileOperations.readFileToSocket(socket, omnifile);
                socket.close();
            }catch (Exception e){
                socket.close();
                // sendNotification(Constants.OP_UPLOAD,Constants.OP_S_FINISHED,omnifile.getFileName(),user,false);
                sendNotification(Constants.OP_UPLOAD,Constants.OP_S_FINISHED,omnifile,user,false);
            }
        }
        oppNum--;
        sendNotification(Constants.OP_UPLOAD,Constants.OP_S_FINISHED,omnifile,user,true);
    }

    public synchronized void getFile(Socket socket, String fileName,User user) throws IOException, InterruptedException, ClassNotFoundException {

        oppNum++;
        sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_STARTED,null,user,true);

        OmniFile tempFile= null;
        try {
            tempFile = FileOperations.saveFileFromSocket(socket, filesDirectory + fileName);
            socket.close();
        }catch (Exception e){
            tempFile.delete();
            socket.close();
            sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_FINISHED,tempFile,user,false);
        }

        System.out.println("GetFile-> TempSize:" + tempFile.getFileSize());

        fileList.add(tempFile);
        oppNum--;
        sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_FINISHED,tempFile,user,true);

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
