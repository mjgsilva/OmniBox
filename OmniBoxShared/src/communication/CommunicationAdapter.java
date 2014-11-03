package communication;

import shared.Constants;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.NoSuchElementException;

/**
 * Created by OmniBox on 03-11-2014.
 */
public abstract class CommunicationAdapter implements TCP, UDP, Multicast {

    /**
     * This method sends the only available muticast message and waits for response.
     *
     * Returns the reponse as a String object.
     *
     * @param messageToSend
     * @param port
     * @return
     * @throws IOException
     */
    @Override
    public String sendMulticastMessage(String messageToSend, int port) throws IOException {
        InetAddress group = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.joinGroup(group);
        multicastSocket.setTimeToLive(1); //TTL

        // Send object
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);
        out.writeObject(messageToSend); // code as a String object
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

    @Override
    public File getFile(File fileToGet) throws NoSuchElementException, IllegalArgumentException, InterruptedException, IOException {
        return null;
    }

    @Override
    public void sendFile(File fileToSend) throws IllegalArgumentException, InterruptedException, IOException {

    }

    @Override
    public void sendTCPMessage(String messageToSend) throws InterruptedException, IOException {

    }

    @Override
    public String toString(String s) {
        return null;
    }

    @Override
    public void sendUDPMessage(String messageToSend) throws InterruptedException, IOException {

    }
}
