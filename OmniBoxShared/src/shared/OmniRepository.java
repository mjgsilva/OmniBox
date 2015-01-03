package shared;
import communication.CommunicationAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.*;

/**
 * OmniRepository class.
 * This class represents the repository itself. It contains all data from an individual repository.
 * Variables that do not implement Serializable are marked as transient, because repository sends it
 * self to server at some situations.
 *
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
    private HashSet<OmniFile> fileList = new HashSet();
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


    /**
     * Method that defines a notification to be sent to the server
     *
     * @param operation
     * @param status
     * @param omniFile
     * @param user
     * @param isSuccessful
     */
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

    /**
     * Delete file and notify the server
     *
     * @param omniFile
     * @param user
     */
    public synchronized void deleteFile(OmniFile omniFile,User user){
        notifyWatcher = false;
        oppNum++;
        sendNotification(Constants.OP_DELETE,Constants.OP_S_STARTED,omniFile,user,true);

        for(OmniFile file : fileList){
            if(file.equals(omniFile))
                file.delete();
        }

        // Erase file from disk if it exists in this repository directory
        // If it does not exists delete() function returns false.
        (new OmniFile(filesDirectory + omniFile.getFileName())).delete();
        //fileList.remove(omniFile);
        customRemoveFromFileList(omniFile);

        oppNum--;
        sendNotification(Constants.OP_DELETE,Constants.OP_S_FINISHED,omniFile, user,true);
        notifyWatcher = true;
    }

    /**
     * Send a file and notify the server
     *
     * @param socket
     * @param omnifile
     * @param user
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized void sendFile(Socket socket,OmniFile omnifile,User user) throws IOException, InterruptedException {
        OmniFile auxReal = omnifile;
        oppNum++;

        sendNotification(Constants.OP_UPLOAD,Constants.OP_S_STARTED,omnifile,user,true);
        //find file and send
        if(fileList.contains(omnifile))
        {
            try {
                // Get real file name, it migth contain $ signs
                for (Iterator<OmniFile> it = fileList.iterator(); it.hasNext(); ) {
                    OmniFile aux = it.next();
                    if (aux.equals(omnifile)) {
                        auxReal = new OmniFile(aux.getDirectory() + aux.getFileName());
                        break;
                    }
                }

                FileOperations.readFileToSocket(socket, auxReal);
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

    /**
     * get a file and notify the server
     *
     * @param socket
     * @param fileName
     * @param user
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    public synchronized void getFile(Socket socket, OmniFile fileName,User user) throws IOException, InterruptedException, ClassNotFoundException {
        // Critic section (Watcher)
        notifyWatcher = false;

        oppNum++;
        sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_STARTED,null,user,true);

        OmniFile tempFile = null, aux = fileName;
        String fileNameDelimitator = ""; // To differentiate files with the same name but different modification dates
        try {
            // If this is true, then there is a file on disk with the same name, but is different somehow
            if (new OmniFile(filesDirectory + fileName.getFileName()).exists() &&
                    !fileList.contains(fileName))
                while((aux = new OmniFile(filesDirectory + fileName.getFileName() + fileNameDelimitator)).exists())
                    fileNameDelimitator += "$"; // Adds dollar signs until file is unique

            tempFile = FileOperations.saveFileFromSocket(socket, filesDirectory + aux.getFileName());

            socket.close();
        }catch (Exception e){
            tempFile.delete();
            socket.close();
            sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_FINISHED,tempFile,user,false);
        }

        System.out.println("GetFile-> TempSize:" + tempFile.getFileSize());

        // Update last file added to list, so it has the same last Modified date. Because directory watcher is
        // going to assume the setLastModified(...) bellow as a change on the directory.
        tempFile.setLastModified(fileName.getLastModified());
        fileList.add(tempFile);


        oppNum--;
        sendNotification(Constants.OP_DOWNLOAD,Constants.OP_S_FINISHED,tempFile,user,true);

        notifyWatcher = true;
    }

    /**
     * verify if file exists on repository
     * @param omniFile
     * @return boolean
     */
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

    /**
     * Because delete event on watcher is catch after file is deleted from disk, it screws over
     * the references on fileList. LastMod is created specific for this situations.
     * We have to redo the fileList but not using the usual equals and hash code from OmniFile.
     *
     * @param tempFile file to be excluded from
     */
    /*public synchronized void customRemoveFromFileList(OmniFile tempFile) {
        HashSet<OmniFile> newFileList = new HashSet<OmniFile>();
        for (OmniFile aux : fileList) {
            if (!aux.getFileName().equals(tempFile.getFileName()) || aux.getLastMod() != tempFile.getLastMod())
                newFileList.add(aux);
        }

        fileList = newFileList;
    }*/




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
    public synchronized boolean customRemoveFromFileList(OmniFile omniFile) {
        HashSet<OmniFile> newFileList = new HashSet<OmniFile>();
        boolean flag = false;
        for (OmniFile aux : fileList) {
            if ((aux.getFileName().equals(omniFile.getFileName()) && aux.getLastMod() == omniFile.getLastMod()) ||
                    (aux.getFileName().equals(OmniFile.getOriginalFileName(omniFile.getFileName())) &&
                    aux.getFileSize() == omniFile.getFileSize() &&
                    aux.getLastModified() <= omniFile.getLastMod())) {
                // File to exclude, so do nothing.
                flag = true;
            } else
                newFileList.add(aux);
        }

        fileList = newFileList;

        return flag;
    }
}
