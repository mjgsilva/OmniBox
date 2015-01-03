package logic.state;

import communication.CommunicationAdapter;
import logic.Client;
import shared.OmniFile;
import shared.Request;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * State adapter, so classes (states), who extends from the state
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
    public StateInterface defineAuthentication(String username, String password) throws InterruptedException, IOException, ClassNotFoundException {
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
    public void sendTCPMessage(Socket socket, Request cmd) throws InterruptedException, IOException {
        super.sendTCPMessage(socket, cmd);
    }

    @Override
    public Request getTCPMessage(Socket socket) throws InterruptedException, IOException, ClassNotFoundException {
        return super.getTCPMessage(socket);
    }

    @Override
    public void sendUDPMessage(DatagramSocket socket,InetAddress inetAddress,int port,Request cmd) throws InterruptedException, IOException {
        super.sendUDPMessage(socket,inetAddress,port,cmd);
    }

    @Override
    public Request getUDPMessage(DatagramSocket socket) throws InterruptedException, IOException, ClassNotFoundException {
        return super.getUDPMessage(socket);
    }
}
