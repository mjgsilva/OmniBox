package logic;

import logic.state.StateInterface;
import logic.state.WaitAuthentication;

import java.io.File;

/**
 * This class represents the client program itself.
 * It's initialized on the first state, WaitAuthentication.
 * It's part of the model.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class Client implements ClientInterface {
    private StateInterface currentState;

    /**
     * Client Constructor
     *
     * Client instances start on WaitAuthentication state.
     */
    public Client() {
        this.currentState = new WaitAuthentication(this);
    }

    public StateInterface getCurrentState() {
        return currentState;
    }

    @Override
    public void defineAuthentication(String username, String password) {
        currentState = currentState.defineAuthentication(username, password);
    }

    @Override
    public void defineGetRequest(File fileToGet) {
        currentState = currentState.defineGetRequest(fileToGet);
    }

    @Override
    public void defineSendRequest(File fileToSend) {
        currentState = currentState.defineSendRequest(fileToSend);
    }

    @Override
    public void defineRemoveRequest(File fileToRemove) {
        currentState = currentState.defineRemoveRequest(fileToRemove);
    }

    @Override
    public void defineReturnToRequest() {
        currentState = currentState.defineReturnToRequest();
    }
}
