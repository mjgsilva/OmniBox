package logic.state;

import shared.OmniFile;

import java.io.File;
import java.io.IOException;

/**
 * Contains all possible operations to execute on this state machine.
 *
 * All method returns next state.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public interface StateInterface {
    public StateInterface defineAuthentication(String username, String password) throws InterruptedException, IOException;
    public StateInterface defineGetRequest(final OmniFile fileToGet) throws IOException, InterruptedException, ClassNotFoundException;
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException;
    public StateInterface defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException;
    public StateInterface defineReturnToRequest();
    public StateInterface defineMulticastRequest() throws IOException;
}
