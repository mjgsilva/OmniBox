package omniboxshared.communication;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Created by OmniBox on 02-11-2014.
 */
public abstract class TCPAdapter implements TCP {
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
    public void sendMessage(String messageToSend) throws InterruptedException, IOException {

    }

    @Override
    public String toString(String s) {
        return null;
    }
}
