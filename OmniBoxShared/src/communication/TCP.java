package communication;

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
     * Connects socket.
     *
     * @param destinySocket Socket wished to connect to
     * @throws InterruptedException If timeout is reached
     * @throws IOException
     */
    // [QUESTION] Isn't this method unnecessary?
    // Socket socket = new Socket(host, port); // This is a connection..
    public void connect(Socket destinySocket) throws InterruptedException, IOException;

    /**
     * Retrieves file from other end of TCP socket.
     *
     * @param fileToGet Desired file to get from the other TCP socket side
     * @throws NoSuchElementException If the file is not found
     * @throws IllegalArgumentException If the fileToGet is invalid
     * @throws InterruptedException If timeout is reached
     * @throws IOException If there's any File error
     * @return File
     */
    public File getFile(File fileToGet) throws NoSuchElementException, IllegalArgumentException, InterruptedException, IOException;

    /**
     * Send file to other end of TCP socket.
     *
     * @param fileToSend
     * @throws IllegalArgumentException If argument is invalid for any reason
     * @throws InterruptedException If timeout is reached
     * @throws IOException If there's any File error
     */
    public void sendFile(File fileToSend) throws IllegalArgumentException, InterruptedException, IOException;

    /**
     * Sends message via TCP socket.
     *
     * @param messageToSend
     * @throws InterruptedException If timeout is reached
     * @throws IOException If there's any IO operation that failed
     */
    public void sendMessage(String messageToSend) throws InterruptedException, IOException;

    // [QUESTION] What is this method supposed to do?
    public String toString(String s);
}
