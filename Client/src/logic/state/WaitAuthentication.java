package logic.state;

import logic.Client;

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
    public StateInterface defineAuthentication(String username, String password) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
