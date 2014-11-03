package communication;


import java.io.IOException;
import java.net.DatagramSocket;

/**
 * - Interface UDP -
 * This interface has every UDP communication method prototype needed
 * in a class that implements TCP operations.
 *
 * Of course every method is supposed to be used as the user wants.
 * Commentaries simply describe what they are intended to do when overriding it.
 * This way we maintain a concise code.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public interface UDP {

    /**
     * Sends message via TCP socket.
     *
     * @param messageToSend
     * @throws InterruptedException If timeout is reached
     * @throws IOException If there's any IO operation that failed
     */
    public void sendUDPMessage(String messageToSend) throws InterruptedException, IOException;
}
