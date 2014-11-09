package logic.state;

import logic.Client;
import shared.Constants;
import shared.Request;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents the authentication operation made when this client starts.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class WaitAuthentication extends StateAdapter {
    public WaitAuthentication(Client client) {
        super(client);
    }

    @Override
    public StateInterface defineAuthentication(String username, String password) throws InterruptedException, IOException {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(username); parameters.add(password);

        // If there's any error, stays in this state and then its UI responsibility to prompt an error message
        sendTCPMessage(client.getServerSocket(), new Request(Constants.CMD.cmdAuthenticate, parameters));

        return new WaitRequest(client);
    }

    @Override
    public StateInterface defineMulticastRequest() throws IOException {
        // If there's any error, stays in this state and then its UI responsibility to prompt an error message
        client.setServerIP(sendMulticastMessage(Constants.REQUEST_SERVER_IP_ADDRESS, client.getPort()));

        return new WaitRequest(client);
    }
}
