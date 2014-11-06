package threads;

import shared.Constants;
import shared.OmniFile;
import shared.OmniRepository;

import java.io.*;
import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */
public class ProcessClient extends Thread{
    private Socket socketToClient;
    private OmniFile localDirectory;
    private OmniRepository omniRepository;

    public ProcessClient(Socket socketToClient, OmniRepository omniRepository) {
        this.omniRepository = omniRepository;
        this.socketToClient = socketToClient;
    }

    private void saveFile(String fileName) throws IOException {
        //
    }

    private void sendFile(String filename) throws IOException {

    }

    private void deleteFile(OmniFile file){
        file.delete();
    }


    private void processMessage(){

    }



    @Override
    public void run() {
        super.run();
    }
}
