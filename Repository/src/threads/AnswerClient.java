package threads;

import shared.OmniRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */
public class AnswerClient extends Thread{
    private static OmniRepository omniRepository= null;

    public AnswerClient(OmniRepository omniRepository){
        this.omniRepository = omniRepository;
    }

    @Override
    public void run() {
        while(true){
            Socket socketToClient = null;
            try {
                socketToClient = omniRepository.socket.accept();
            } catch (IOException e) {

            }
            new ProcessClient(socketToClient, omniRepository).start();
        }

    }
}
