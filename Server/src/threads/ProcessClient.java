package threads;

import server.OmniServer;
import shared.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Process Client.
 * Responsible for handling client requests. Request is made as a Request
 * object (OmniBoxShared.Request), this request may be of the following types:
 * authentication, download, upload or remove.
 *
 * Created by OmniBox on 02/11/14.
 */
public class ProcessClient extends Thread {
    final private Socket socket;
    final private OmniServer omniServer;
    private User user;

    public ProcessClient(Socket socket,OmniServer omniServer) {
        this.socket = socket;
        this.omniServer = omniServer;
    }

    @Override
    public void run() {
        try {
            while(true) {
                try {
                    Request request = omniServer.getTCPMessage(socket);

                    switch (request.getCmd()) {
                        case cmdAuthentication:
                            authetication(request);
                            break;
                        case cmdSendFile:
                            upload(request);
                            break;
                        case cmdGetFile:
                            download(request);
                            break;
                        case cmdDeleteFile:
                            delete(request);
                            break;
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("Received an invalid object");
                    break;
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    if(user != null) {
                        omniServer.removeUserActivity(user);
                        omniServer.removeSocket(user);
                        omniServer.sendServiceNotification();
                    }
                    break;
                }
            }
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private synchronized void sendMessage(Request response) {
        try {
            System.out.println("Sending: " + response);
            omniServer.sendTCPMessage(socket, response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void authetication(Request req) {
        User user = (User) req.getArgsList().get(0);
        ArrayList args = new ArrayList();
        boolean validLogin = omniServer.login(user);

        if(validLogin) {
            this.user = user;
            omniServer.addUserActivity(user);
            omniServer.addSocket(user,socket);
        }

        args.add(validLogin);
        Request response = new Request(Constants.CMD.cmdAuthentication,args);
        sendMessage(response);
        fileList();
    }

    private synchronized void fileList() {
        ArrayList args = omniServer.getFileList();
        Request response = new Request(Constants.CMD.cmdRefreshList,args);
        sendMessage(response);
    }

    private synchronized void upload(Request request) {
        OmniFile omniFile = (OmniFile) request.getArgsList().get(0);
        ArrayList<Object> args = new ArrayList<Object>();
        args.add(Constants.OP_UPLOAD);
        System.out.println("Uploading: " +  omniFile.toString());
        if(!omniServer.fileExists(omniFile) && (omniServer.getNumberOfRepositories() != 0)) {
            OmniRepository omniRepository = omniServer.getLessWorkloadedRepository();
            System.out.println("[UP]Repository -> " + omniRepository.getLocalAddr() +":" + omniRepository.getPort());
            args.add(omniRepository.getLocalAddr());
            args.add(omniRepository.getPort());
            args.add(Constants.FILEOK);

            OmniFile aux2= omniFile;
            if(!omniServer.getFileList().contains(omniFile)){
                String fileNameDelimitator="";

                for (OmniFile aux : (ArrayList<OmniFile>)omniServer.getFileList())

                    if (aux.getFileName().equals(omniFile.getFileName())) {
                        while((aux2 = new OmniFile(omniFile.getDirectory() + omniFile.getFileName() + fileNameDelimitator)).exists()) {
                            fileNameDelimitator += "$"; // Adds dollar signs until file is unique
                            omniServer.addFile(aux2);
                            Request response = new Request(Constants.CMD.cmdRepositoryAddress,args);
                            sendMessage(response);
                            return;
                        }

                    }
            }
            omniServer.addFile(aux2);
        } else {
            args.add(null);
            args.add(null);
            args.add(Constants.FILENOTOK);
        }
        Request response = new Request(Constants.CMD.cmdRepositoryAddress,args);
        sendMessage(response);
    }

    private synchronized void download(Request request) {
        OmniFile omniFile = (OmniFile) request.getArgsList().get(0);
        ArrayList args = new ArrayList();
        args.add(Constants.OP_DOWNLOAD);

        System.out.println("Downloading: " +  omniFile.toString());
        if(omniServer.fileExists(omniFile)) {
            OmniRepository omniRepository = omniServer.getDownloadSource(omniFile);
            System.out.println("[DOWN]Repository -> " + omniRepository.getLocalAddr() +":" + omniRepository.getPort());
            args.add(omniRepository.getLocalAddr());
            args.add(omniRepository.getPort());
            args.add(Constants.FILEOK);
        } else {
            args.add(null);
            args.add(null);
            args.add(Constants.FILENOTOK);
        }

        Request response = new Request(Constants.CMD.cmdRepositoryAddress,args);
        sendMessage(response);
    }

    private synchronized void delete(Request request){
            OmniFile omniFile = (OmniFile) request.getArgsList().get(0);
            ArrayList<Object> args = new ArrayList<Object>();
            args.add(omniFile);
            Request repositoryResponse;

            if (omniServer.fileExists(omniFile) && !omniServer.fileBeingAccessed(omniFile)) {
                repositoryResponse = new Request(Constants.CMD.cmdDeleteFile, args);
                omniServer.deleteBroadcast(repositoryResponse);
                omniServer.removeFile(omniFile);
                omniServer.notifyClients();
                args.add(Constants.FILEOK);
            } else {
                args.add(Constants.FILENOTOK);
            }
            Request clientResponse = new Request(Constants.CMD.cmdDeleteFile,args);
            sendMessage(clientResponse);
    }
}
