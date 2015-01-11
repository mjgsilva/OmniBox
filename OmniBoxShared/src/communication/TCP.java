package communication;

import shared.Request;

import java.io.IOException;
import java.net.Socket;

/**
 * - Interface TCP -
 * This interface has every TCP communication method prototype needed
 * in a class that implements TCP operations.
 *
 * Of course every method is supposed to be used as the user wants.
 * Commentaries simply describe what they are intended to do when overriding it.
 * This way we maintain a concise code.
 *
 * Created by OmniBox on 01-11-2014.
 */
public interface TCP {
    /**
     * Sends message via TCP socket.
     * It's always sent a Request object.
     *
     * @param socket Destiny socket
     * @param cmd Request to send
     * @throws InterruptedException If operation is interrupted
     * @throws IOException If there's an error associated with a socket operation
     */
    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException;

    /**
     * Gets message via TCP socket. This blocks the socket.
     * It's always expected to receive a Request object.
     *
     * @param socket Destiny socket
     * @return received message
     * @throws InterruptedException If operation is interrupted
     * @throws IOException If there's an error associated with a socket operation
     */
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException;
}
