package logic;

import logic.state.StateInterface;
import shared.OmniFile;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

/**
 * Created by OmniBox on 08-11-2014.
 */
public class ClientModel extends Observable implements ClientInterface {
    private Client client;

    public ClientModel(Client client) {
        this.client = client;
    }

    public void sendNotification() {
        setChanged();
        notifyObservers();
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void defineAuthentication(String username, String password) throws IOException, InterruptedException, ClassNotFoundException {
        client.defineAuthentication(username, password);
        sendNotification();
    }

    @Override
    public void defineGetRequest(OmniFile fileToGet) throws InterruptedException, IOException, ClassNotFoundException {
        client.defineGetRequest(fileToGet);
        sendNotification();
    }

    @Override
    public void defineSendRequest(OmniFile fileToSend) throws InterruptedException, IOException, ClassNotFoundException {
        client.defineSendRequest(fileToSend);
        sendNotification();
    }

    @Override
    public void defineRemoveRequest(OmniFile fileToRemove) throws IOException, InterruptedException {
        client.defineRemoveRequest(fileToRemove);
        sendNotification();
    }

    @Override
    public void defineReturnToRequest() {
        client.defineReturnToRequest();
        sendNotification();
    }

    @Override
    public void defineMulticastRequest() throws IOException {
        client.defineMulticastRequest();
        sendNotification();
    }

    public void setFileToUpload(OmniFile fileToUpload) {
        client.setFileToUpload(fileToUpload);
    }

    public StateInterface getCurrentState() {
        return client.getCurrentState();
    }
}
