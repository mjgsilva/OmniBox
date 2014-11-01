package logic.state;

import logic.Client;

import java.io.File;

/**
 * State adapter so classes (states), who extends from the state
 * machine, don't have to implement every method.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public abstract class StateAdapter implements StateInterface {
    private Client client;

    protected StateAdapter(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public StateInterface defineAuthentication(String username, String password) {
        return this;
    }

    @Override
    public StateInterface defineGetRequest(File fileToGet) {
        return this;
    }

    @Override
    public StateInterface defineSendRequest(File fileToSend) {
        return this;
    }

    @Override
    public StateInterface defineRemoveRequest(File fileToRemove) {
        return this;
    }

    @Override
    public StateInterface defineReturnToRequest() {
        return this;
    }
}
