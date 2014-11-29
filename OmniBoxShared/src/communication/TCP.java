package communication;

import shared.OmniFile;
import shared.Request;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * - Interface TCP -
 * This interface has every TCP communication method prototype needed
 * in a class that implements TCP operations.
 *
 * Of course every method is supposed to be used as the user wants.
 * Commentaries simply describe what they are intended to do when overriding it.
 * This way we maintain a concise code.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public interface TCP {


    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException;

    /**
     * Gets message via TCP socket. This blocks the socket.
     * It's always sended a serializable String object.
     *
     * @param socket
     * @return received message
     * @throws InterruptedException
     * @throws IOException
     */
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException;

    // [QUESTION] What is this method supposed to do?
    public String toString(String s);
}
