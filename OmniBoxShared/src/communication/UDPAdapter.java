package communication;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by OmniBox on 02-11-2014.
 */
public abstract class UDPAdapter implements UDP {
    @Override
    public void connect(DatagramSocket destinySocket) throws InterruptedException, IOException {

    }

    @Override
    public void sendMessage(String messageToSend) throws InterruptedException, IOException {

    }
}
