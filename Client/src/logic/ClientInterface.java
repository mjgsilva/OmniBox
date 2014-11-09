package logic;

import logic.state.StateInterface;
import shared.OmniFile;

import java.io.File;
import java.io.IOException;

/**
 * Describes the interface for model classes.
 *
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public interface ClientInterface {
    void defineAuthentication(final String username, final String password) throws IOException, InterruptedException;

    void defineGetRequest(final OmniFile fileToGet) throws InterruptedException, IOException, ClassNotFoundException;

    void defineSendRequest(final OmniFile fileToSend) throws InterruptedException, IOException, ClassNotFoundException;

    void defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException;

    void defineReturnToRequest();

    void defineMulticastRequest() throws IOException;
}
