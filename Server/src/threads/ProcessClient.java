package threads;

import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */
public class ProcessClient extends Thread {
    private Socket socket;

    public ProcessClient(Socket socket) {
        this.socket = socket;
    }

    public void run() {

    }
}
