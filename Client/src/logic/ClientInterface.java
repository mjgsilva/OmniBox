package logic;

import logic.state.StateInterface;

import java.io.File;

/**
 * Describes the interface for model classes.
 *
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public interface ClientInterface {
    void defineAuthentication(final String username, final String password);

    void defineGetRequest(final File fileToGet);

    void defineSendRequest(final File fileToSend);

    void defineRemoveRequest(final File fileToRemove);

    void defineReturnToRequest();
}
