package threads;

import shared.OmniRepository;
import shared.Request;

import java.io.IOException;
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
                //Accept socket to communicate
                socketToClient = omniRepository.getSocket().accept();
                //Launch thread to process client
                new ProcessClient(socketToClient, omniRepository).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
