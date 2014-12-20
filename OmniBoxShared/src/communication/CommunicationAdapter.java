package communication;

import shared.Constants;
import shared.OmniFile;
import shared.Request;

import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;

/**
 * Created by OmniBox on 03-11-2014.
 */
public abstract class CommunicationAdapter implements TCP, UDP, Multicast {

    /**
     * This method sends the only available muticast message and waits for response.
     *
     * Returns the reponse as a <b>String</b> object.
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
    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(cmd);
        out.flush();
    }

    @Override
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        return (Request)in.readObject();
    }

    @Override
    public String toString(String s) {
        return null;
    }

    @Override
    public void sendUDPMessage(DatagramSocket socket,InetAddress inetAddress,int port,Request cmd) throws InterruptedException, IOException {
        DatagramPacket packet = null;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream out = null;

        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(cmd);

        packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), inetAddress, port);
        socket.send(packet);

    }

    @Override
    public Request getUDPMessage(DatagramSocket socket) throws InterruptedException, IOException, ClassNotFoundException {
        Request cmdTemp=null;
        ObjectInputStream in = null;
        DatagramPacket packet = null;

        packet = new DatagramPacket(new byte[Constants.MAX_SIZE], Constants.MAX_SIZE);
        socket.receive(packet);

        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        cmdTemp = (Request)(in.readObject());

        cmdTemp.getArgsList().add(packet.getAddress().getHostAddress());

        return cmdTemp;
    }
}
