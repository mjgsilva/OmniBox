package threads;

import shared.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Process Multicast.
 * Responsible for handling Multicast requests. Server returns its own IP address.
 *
 * Created by Omnibox on 20-12-2014.
 */
public class ProcessMulticast extends Thread {
    private MulticastSocket multicastSocket;
    private boolean isRunning = false;

    public ProcessMulticast() throws IOException {
        setDaemon(true);
    }

    @Override
    public void run() {
        InetAddress group;
        DatagramPacket pkt;
        ByteArrayOutputStream buff;
        ObjectOutputStream out;

        isRunning = true;

        try {
            group = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
            multicastSocket = new MulticastSocket(Constants.MULTICAST_PORT);
            multicastSocket.joinGroup(group);

            while (isRunning) {
                pkt = new DatagramPacket(new byte[Constants.MAX_SIZE], Constants.MAX_SIZE);
                try {
                    multicastSocket.receive(pkt);

                    buff = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(buff);
                    out.writeObject(InetAddress.getLocalHost().getHostAddress());
                    out.flush();
                    out.close();

                    pkt.setData(buff.toByteArray());
                    pkt.setLength(buff.size());
                    multicastSocket.send(pkt);
                } catch (IOException e) {
                    System.out.println("Multicast: A problem occurred sending messages");
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Multicast: UnknownEx");
        } catch (IOException e) {
            System.out.println("Multicast: IOEx");
        } finally {
                if(multicastSocket != null)
                    multicastSocket.close();
        }
    }
}
