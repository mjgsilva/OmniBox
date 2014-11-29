package threads;

import shared.OmniFile;
import shared.OmniRepository;
import shared.Request;

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
                omniRepository.deleteFile((String) request.getArgsList().get(0));
            case cmdGetFile:
                try {
                    //Send a file to a client
                    omniRepository.sendFile(socketToClient, omniRepository.getOmniFileByName((String) request.getArgsList().get(0)));
                } catch (InterruptedException e) {
                } catch (IOException e) {
                }
            case cmdSendFile:
                try {
                    omniRepository.getFile(socketToClient, (String) request.getArgsList().get(0));
                } catch (IOException e) {
                } catch (InterruptedException e) {
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            default:
        }
    }
}
