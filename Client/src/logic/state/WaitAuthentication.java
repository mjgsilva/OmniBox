package logic.state;

import logic.Client;
import shared.Constants;
import shared.Request;
import shared.User;

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
    public StateInterface defineAuthentication(String username, String password) throws InterruptedException, IOException, ClassNotFoundException {
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(new User(username, password));

        // If there's any error, stays in this state and then its UI responsibility to prompt an error message
        sendTCPMessage(client.getServerSocket(), new Request(Constants.CMD.cmdAuthenticate, parameters));

        // Wait for answer
        Request request = getTCPMessage(client.getServerSocket());

        if (request.getCmd() == Constants.CMD.cmdAuthenticate) {
            if ((Boolean)request.getArgsList().get(0))
                return new WaitRequest(client);
            else
                throw new IOException("Invalid username or password.");
        }

        return this;
    }

    @Override
    public StateInterface defineMulticastRequest() throws IOException {
        // If there's any error, stays in this state and then its UI responsibility to prompt an error message
        client.setServerIP(sendMulticastMessage(Constants.REQUEST_SERVER_IP_ADDRESS, client.getPort()));

        return new WaitRequest(client);
    }
}
