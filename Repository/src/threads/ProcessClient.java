package threads;

import shared.OmniFile;
import shared.OmniRepository;
import shared.Request;
import shared.User;

import java.io.*;
import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */
public class ProcessClient extends Thread {
    private Request request = null;
    private Socket socketToClient;
    private OmniFile localDirectory;
    private OmniRepository omniRepository;

    public ProcessClient(Socket socketToClient, OmniRepository omniRepository, Request request) {
        this.omniRepository = omniRepository;
        this.socketToClient = socketToClient;
        this.request = request;
    }

    @Override
    public void run() {
        switch (request.getCmd()) {
            case cmdDeleteFile:
                omniRepository.deleteFile((String) request.getArgsList().get(0),(User)request.getArgsList().get(1));
            case cmdGetFile:
                try {
                    //Send a file to a client
                    System.out.println("SendFile from "+socketToClient.getInetAddress().getAddress());
                    omniRepository.sendFile(socketToClient, omniRepository.getOmniFileByName((String) request.getArgsList().get(0)),(User)request.getArgsList().get(1));
                } catch (InterruptedException e) {
                } catch (IOException e) {
                }
            case cmdSendFile:
                try {
                    System.out.println("GetFile from "+socketToClient.getInetAddress().getHostAddress());
                    omniRepository.getFile(socketToClient, (String) request.getArgsList().get(0),(User)request.getArgsList().get(1));
                } catch (IOException e) {
                } catch (InterruptedException e) {
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            default:
        }
    }
}
