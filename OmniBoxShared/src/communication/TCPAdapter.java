package communication;

import shared.Constants;
import shared.OmniFile;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Created by OmniBox on 02-11-2014.
 */
public abstract class TCPAdapter implements TCP {

    @Override
    public OmniFile getFile(Socket socket) throws NoSuchElementException, IllegalArgumentException, InterruptedException, IOException, ClassNotFoundException {
        // IOException my be thrown here, user of this method is supposed to handle this exceptions
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        // Read Object - May throw ClassNotFoundException
        return (OmniFile) in.readObject();
    }

    @Override
    public void sendFile(Socket socket, OmniFile fileToSend) throws IllegalArgumentException, InterruptedException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        // Sends OmniFile object
        out.writeObject(fileToSend);
        out.flush();
    }

    @Override
    public void sendTCPMessage(Socket socket, String messageToSend) throws InterruptedException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(messageToSend);
        out.flush();
    }

    @Override
    public String getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        return (String) in.readObject();
    }

    @Override
    public String toString(String s) {
        return null;
    }
}
