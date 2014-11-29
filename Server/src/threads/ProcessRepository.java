package threads;

import server.OmniServer;
import shared.Request;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by OmniBox on 13/11/14.
 */
public class ProcessRepository extends Thread {
    private final DatagramSocket socket;
    private final OmniServer omniServer;

    public ProcessRepository(DatagramSocket socket, OmniServer omniServer) throws IOException{
        this.socket = socket;
        this.omniServer = omniServer;
    }

    @Override
    public void run() {
        try {
            Request req = omniServer.getUDPMessage(socket);

        } catch (ClassNotFoundException e) {
        } catch (InterruptedException e) {
        } catch (IOException e) { }
    }

}
