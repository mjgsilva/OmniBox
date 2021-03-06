package communication;

import shared.Constants;
import shared.Request;

import java.io.*;
import java.net.*;

/**
 * Created by OmniBox on 03-11-2014.
 */
public abstract class CommunicationAdapter implements TCP, UDP, Multicast {

    ObjectInputStream in;
    ObjectOutputStream out;
    DatagramSocket datagramSocket;

    @Override
    public String sendMulticastMessage(String messageToSend) throws IOException {
        InetAddress groupAddress = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
        int port = Constants.MULTICAST_PORT;
        datagramSocket = new DatagramSocket();

        // Send object
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);
        out.writeObject(messageToSend); // code as a String object
        out.flush();
        out.close();
        DatagramPacket packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), groupAddress, port);
        datagramSocket.send(packet);

        // Receive object as a response from server
        packet = new DatagramPacket(new byte[Constants.MAX_SIZE], Constants.MAX_SIZE);
        datagramSocket.receive(packet);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0,
                packet.getLength()));

        String response;
        try {
            response = (String) in.readObject();
        } catch (ClassNotFoundException e) {
            // Just so we don't have to handle potential inner code errors on user interface
            throw new IOException("Internal error.");
        }

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
                cmdTemp = (Request) second;
                cmdTemp.getArgsList().add(packet.getAddress().getHostAddress());
            }
        }
        return cmdTemp;
    }
}
