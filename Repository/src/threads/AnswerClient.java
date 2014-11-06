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
                socketToClient = omniRepository.socket.accept();
                //Get Request to execute
                Request request = omniRepository.getTCPMessage(socketToClient);
                //Launch thread to process client
                new ProcessClient(socketToClient, omniRepository, request).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
