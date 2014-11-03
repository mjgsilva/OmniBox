package communication;

import shared.Constants;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Multicast method implementation
 *
 * Created by OmniBox on 02-11-2014.
 */
public interface Multicast {

    /**
     *
     * @param messageToSend
     * @return
     * @throws IOException
     */
    public String sendMulticastMessage(String messageToSend, int port) throws IOException;
}
