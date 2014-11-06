package threads;

import shared.OmniRepository;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by OmniBox on 06/11/14.
 */
public class AnswerServer extends Thread{
    private static OmniRepository omniRepository= null;

    public AnswerServer(OmniRepository omniRepository){
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
