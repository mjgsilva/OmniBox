package logic.state;

import logic.Client;

import java.io.File;

/**
 * Waits for user to choose which operation to be performed.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class WaitRequest extends StateAdapter {
    public WaitRequest(Client client) {
        super(client);
    }

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
}
