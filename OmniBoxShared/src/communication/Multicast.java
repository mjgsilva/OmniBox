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
public class Multicast {

    /**
     *
     * @param messageToSend
     * @return
     * @throws IOException
     */
    public String sendMessage(String messageToSend, int port) throws IOException {
        InetAddress group = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.joinGroup(group);
        multicastSocket.setTimeToLive(1); //TTL

        // Send object
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);
        out.writeObject(new String(messageToSend)); // code as a String object
        DatagramPacket packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), group, port);
        multicastSocket.send(packet);

        // Receive object as a response from server
        packet = new DatagramPacket(new byte[Constants.MAX_SIZE], Constants.MAX_SIZE);
        multicastSocket.receive(packet);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0,
                packet.getLength()));

        String response;
        try {
            response = (String) in.readObject();
        } catch (ClassNotFoundException e) {
            // Just so we don't have to handle potential inner code errors on user interface
            throw new IOException("Internal error.");
        }

        multicastSocket.leaveGroup(group);

        return response;
    }
}
