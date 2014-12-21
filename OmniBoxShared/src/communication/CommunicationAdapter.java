package communication;

import shared.Constants;
import shared.Request;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;

/**
 * Created by OmniBox on 03-11-2014.
 */
public abstract class CommunicationAdapter implements TCP, UDP, Multicast {

    ObjectInputStream in;
    ObjectOutputStream out;

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
        multicastSocket.close();

        return response;
    }

    @Override
    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(cmd);
        out.flush();
    }

    @Override
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException {
        Object obj;
        Request req = null;

        in = new ObjectInputStream(socket.getInputStream());
        obj = in.readObject();

        if(obj instanceof Request)
            req = (Request)obj;

        return req;
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
        sendUDPMessageSize(socket,inetAddress,port,packet.getLength());
        socket.send(packet);
    }

    @Override
    public void sendUDPMessageSize(DatagramSocket socket, InetAddress inetAddress, int port, int size) throws InterruptedException, IOException {
        DatagramPacket packet = null;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream out = null;

        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(size);

        packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), inetAddress, port);
        socket.send(packet);
    }

    @Override
    public Request getUDPMessage(DatagramSocket socket) throws InterruptedException, IOException, ClassNotFoundException {
        Request cmdTemp=null;
        ObjectInputStream in = null;
        DatagramPacket packet = null;
        Object first, second;

        packet = new DatagramPacket(new byte[Constants.MAX_SIZE], Constants.MAX_SIZE);
        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        first = in.readObject();

        if(first instanceof Integer) {
            int size = (Integer)first;

            packet = new DatagramPacket(new byte[size], size);
            socket.receive(packet);
            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

            second = in.readObject();

            if(second instanceof Request) {
                cmdTemp = (Request) (in.readObject());
                cmdTemp.getArgsList().add(packet.getAddress().getHostAddress());
            }
        }
        return cmdTemp;
    }
}
