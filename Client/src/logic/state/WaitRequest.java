package logic.state;

import communication.TCP;
import logic.Client;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Waits for user to choose which operation to be performed.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class WaitRequest extends StateAdapter implements TCP {
    public WaitRequest(Client client) {
        super(client);
    }

    /**
     * Sends request to get file, this method is supposed to add that file to the Client.fileList
     *
     * @param fileToGet
     * @return next State
     */
    @Override
    public StateInterface defineGetRequest(File fileToGet) {

        return super.defineGetRequest(fileToGet);
    }

    @Override
    public StateInterface defineSendRequest(File fileToSend) {
        return super.defineSendRequest(fileToSend);
    }

    @Override
    public StateInterface defineRemoveRequest(File fileToRemove) {
        return super.defineRemoveRequest(fileToRemove);
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
}
