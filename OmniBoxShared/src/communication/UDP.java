package communication;

import shared.Request;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
     * Send UDP Message specifying the size of the datagram to send.
     *
     * @param socket Socket to write on
     * @param inetAddress Destiny InetAddress
     * @param port Destiny port
     * @param size Datagram package size
     * @throws InterruptedException If operation is interrupted
     * @throws IOException If there's an error associated with a socket operation
     */
    public void sendUDPMessageSize(DatagramSocket socket, InetAddress inetAddress, int port, int size) throws InterruptedException, IOException;

    /**
     * Send UDP Request message.
     *
     * @param socket Socket to write on
     * @param inetAddress Destiny InetAddress
     * @param port Destiny port
     * @param cmd Request to send
     * @throws InterruptedException If operation is interrupted
     * @throws IOException If there's an error associated with a socket operation
     */
    public void sendUDPMessage(DatagramSocket socket,InetAddress inetAddress,int port, Request cmd) throws InterruptedException, IOException;

    /**
     * Get UDP Request message.
     *
     * @param socket Socket to read from
     * @return Received Request object
     * @throws InterruptedException If operation is interrupted
     * @throws IOException If there's an error associated with a socket operation
     * @throws ClassNotFoundException If cast to Request object fails
     */
    public Request getUDPMessage(DatagramSocket socket) throws InterruptedException, IOException, ClassNotFoundException;
}
