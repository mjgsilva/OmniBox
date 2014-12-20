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
 * Created by danilo on 20-12-2014.
 */
public class ProcessMulticast extends Thread {
    private MulticastSocket multicastSocket;
    private boolean isRunning = false;

    public ProcessMulticast(int port) throws IOException {
        this.multicastSocket = new MulticastSocket();
        setDaemon(true);
    }

    @Override
    public void run() {
        InetAddress group;
        ObjectInputStream in;
        Object obj;
        DatagramPacket pkt;
        ByteArrayOutputStream buff;
        ObjectOutputStream out;
        String msgToSend = "127.0.0.1"; // Precisa ser o ip do servidor
        int destinyPort;

        isRunning = true;

        try {
            group = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
            multicastSocket.joinGroup(group);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(isRunning) {
            pkt = new DatagramPacket(new byte[Constants.MAX_SIZE], Constants.MAX_SIZE);
            try {
                multicastSocket.receive(pkt);

                buff = new ByteArrayOutputStream();
                out = new ObjectOutputStream(buff);
                out.writeObject(msgToSend);
                out.flush();
                out.close();

                pkt.setData(buff.toByteArray());
                pkt.setLength(buff.size());
                multicastSocket.send(pkt);
            } catch (IOException e) {
                multicastSocket.close();
                break;
            } finally {
                multicastSocket.close();
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
