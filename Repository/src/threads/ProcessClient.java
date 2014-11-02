package threads;

import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */
public class ProcessClient extends Thread{
    Socket socketToClient;

    public ProcessClient(Socket socketToClient) {
        this.socketToClient = socketToClient;
    }






    @Override
    public void run() {
        super.run();
    }
}
