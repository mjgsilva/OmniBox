package threads;

import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */
public class AnswerClient extends Thread{
    Socket socketToClient;

    public AnswerClient(Socket s){
        socketToClient = s;
    }


    @Override
    public void run() {
        super.run();
    }
}
