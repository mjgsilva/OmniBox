package communication;

import shared.Command;
import shared.OmniFile;

import java.io.File;
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

    /**
     * Retrieves file from other end of TCP socket.
     *
     * This method is supposed to call after sending a a String requesting file name with
     * the sendTCPMessage(String) method.
     *
     * @throws NoSuchElementException If the file is not found
     * @throws IllegalArgumentException If the fileToGet is invalid
     * @throws InterruptedException If timeout is reached
     * @throws IOException If there's any File error
     * @throws ClassNotFoundException if cast fails
     * @return OmniFile
     */
    public OmniFile getFile(Socket socket) throws NoSuchElementException, IllegalArgumentException, InterruptedException, IOException, ClassNotFoundException;

    /**
     * Send file to other end of TCP socket.
     *
     * @param fileToSend
     * @throws IllegalArgumentException If argument is invalid for any reason
     * @throws InterruptedException If timeout is reached
     * @throws IOException If there's any File error
     */
    public void sendFile(Socket socket, OmniFile fileToSend) throws IllegalArgumentException, InterruptedException, IOException;

    /**
     * Sends message via TCP socket.
     * It's always sended a serializable String object.
     *
     * @param cmd
     * @throws InterruptedException If timeout is reached
     * @throws IOException If there's any IO operation that failed
     */
    public void sendTCPMessage(Socket socket, Command cmd) throws InterruptedException, IOException;

    /**
     * Gets message via TCP socket. This blocks the socket.
     * It's always sended a serializable String object.
     *
     * @param socket
     * @return received message
     * @throws InterruptedException
     * @throws IOException
     */
    public Command getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException;

    // [QUESTION] What is this method supposed to do?
    public String toString(String s);
}
