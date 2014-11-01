package logic.state;

import java.io.File;

/**
 * Contains all possible operations to execute on this state machine.
 *
 * All method returns next state.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public interface StateInterface {
    public StateInterface defineAuthentication(final String username, final String password);
    public StateInterface defineGetRequest(final File fileToGet);
    public StateInterface defineSendRequest(final File fileToSend);
    public StateInterface defineRemoveRequest(final File fileToRemove);
    public StateInterface defineReturnToRequest();
}
