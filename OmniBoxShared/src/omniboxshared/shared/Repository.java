package omniboxshared.shared;

import omniboxshared.communication.TCP;
import omniboxshared.communication.UDP;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * Created by OmniBox on 02/11/14.
 */
public class Repository implements UDP, TCP{
    private final int port;
    private final String address;
    private final HashSet<OmniFile> fileList = new HashSet();
    private final int oppNum = 0;

    public Repository(int port, String address) {
        this.port = port;
        this.address = address;
    }

    @Override
    public void connect(Socket destinySocket) throws InterruptedException, IOException {

    }

    @Override
    public File getFile(File fileToGet) throws NoSuchElementException, IllegalArgumentException, InterruptedException, IOException {
        return null;
    }

    @Override
    public void sendFile(File fileToSend) throws IllegalArgumentException, InterruptedException, IOException {

    }

    @Override
    public String toString(String s) {
        return null;
    }

    @Override
    public void connect(DatagramSocket destinySocket) throws InterruptedException, IOException {

    }

    @Override
    public void sendMessage(String messageToSend) throws InterruptedException, IOException {

    }
}
