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
                omniRepository.deleteFile((OmniFile) request.getArgsList().get(0));
            case cmdGetFile:
                try {
                    omniRepository.getFile(socketToClient);
                } catch (InterruptedException e) {
                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                }
            case cmdSendFile:
                try {
                    omniRepository.sendFile(socketToClient, (OmniFile) request.getArgsList().get(0));
                } catch (IOException e) {
                } catch (InterruptedException e) {
                }

            default:
        }
    }
}
