package logic.state;

import communication.CommunicationAdapter;
import logic.Client;
import shared.OmniFile;
import shared.Request;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * State adapter so classes (states), who extends from the state
 * machine, don't have to implement every method.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public abstract class StateAdapter extends CommunicationAdapter implements StateInterface {
    protected Client client;

    protected StateAdapter(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public StateInterface defineAuthentication(String username, String password) throws InterruptedException, IOException {
        return this;
    }

    @Override
    public StateInterface defineGetRequest(final OmniFile fileToGet)  throws IOException, InterruptedException, ClassNotFoundException {
        return this;
    }

    @Override
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException {
        return this;
    }

    @Override
    public StateInterface defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException {
        return this;
    }

    @Override
    public StateInterface defineReturnToRequest() {
        return this;
    }

    @Override
    public StateInterface defineMulticastRequest() throws IOException { return this; }


    // Communication methods --------------------------------------------------------------------

    @Override
    public String sendMulticastMessage(String messageToSend, int port) throws IOException {
        return super.sendMulticastMessage(messageToSend, port);
    }

    @Override
    public OmniFile getFile(Socket socket) throws NoSuchElementException, IllegalArgumentException, InterruptedException, IOException, ClassNotFoundException {
        return super.getFile(socket);
    }

    @Override
    public void sendFile(Socket socket, OmniFile fileToSend) throws IllegalArgumentException, InterruptedException, IOException {
        super.sendFile(socket, fileToSend);
    }

    @Override
    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException {
        super.sendTCPMessage(socket, cmd);
    }

    @Override
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException {
        return super.getTCPMessage(socket);
    }

    @Override
    public String toString(String s) {
        return super.toString(s);
    }

    @Override
    public void sendUDPMessage(String messageToSend) throws InterruptedException, IOException {
        super.sendUDPMessage(messageToSend);
    }
}
