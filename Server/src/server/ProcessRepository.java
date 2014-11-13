package server;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by OmniBox on 13/11/14.
 */
public class ProcessRepository extends Thread {
    private final DatagramSocket socket;
    private final OmniServer omniServer;

    public ProcessRepository(int port, OmniServer omniServer) throws IOException{
        socket = new DatagramSocket(port);
        this.omniServer = omniServer;
    }

    @Override
    public void run() {
        //
    }

}
