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

    public ProcessClient(Socket socketToClient, OmniRepository omniRepository) {
        this.omniRepository = omniRepository;
        this.socketToClient = socketToClient;
    }

    @Override
    public void run() {
        //Get Request to execute
        Request request = null;
        try {
            request = omniRepository.getTCPMessage(socketToClient);


            System.out.println("Get Request from " + socketToClient.getInetAddress().getHostAddress() + " Request:" + request.getCmd().toString());

            switch (request.getCmd()) {
                case cmdDeleteFile:
                    omniRepository.deleteFile((String) request.getArgsList().get(0), (User) request.getArgsList().get(1));
                    break;
                case cmdGetFile:
                    //Send a file to a client
                    System.out.println("SendFile from " + socketToClient.getInetAddress().getHostAddress());
                    omniRepository.sendFile(socketToClient, omniRepository.getOmniFileByName((String) request.getArgsList().get(0)), (User) request.getArgsList().get(1));

                    break;
                case cmdSendFile:
                    System.out.println("PC | GetFile " + request.getArgsList().get(0) + " from " + socketToClient.getInetAddress().getHostAddress());
                    omniRepository.getFile(socketToClient, (String) request.getArgsList().get(0), (User) request.getArgsList().get(1));

                    break;
                default:
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
