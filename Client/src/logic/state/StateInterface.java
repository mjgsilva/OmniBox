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
    /**
     * Define Authentication.
     *
     * Receives username and password for authentication purpose.
     * If data inserted is not valid, state should remain WaitAuthentication,
     * if its valid, then state should move to WaitRequest.
     *
     * Exception should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @param username username
     * @param password password
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    public StateInterface defineAuthentication(String username, String password) throws InterruptedException, IOException, ClassNotFoundException;

    /**
     * Define Get Request.
     *
     * Receives desired OmniFile to get. It should return state WaitAnswer if there are no errors and
     * if your waiting for an answer from the other end. If not, then return WaitRequest.
     *
     * Exceptions should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @param fileToGet file to get
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public StateInterface defineGetRequest(final OmniFile fileToGet) throws IOException, InterruptedException, ClassNotFoundException;

    /**
     * Define Send Request.
     *
     * Receives desired OmniFile to send. It should return state WaitAnswer if there are no errors and
     * if your waiting for an answer from the other end. If not, then return WaitRequest.
     *
     * Exceptions should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @param fileToSend file to send
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException;

    /**
     * Define Remove Request.
     *
     * Receives desired OmniFile to remove. It should return state WaitAnswer if there are no errors and
     * if your waiting for an answer from the other end. If not, then return WaitRequest.
     *
     * Exceptions should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @param fileToRemove file to remove
     * @throws IOException
     * @throws InterruptedException
     */
    public StateInterface defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException;
    public StateInterface defineReturnToRequest();

    /**
     * Define Multicast Request.
     *
     * Responsible for handling multicast connection if implemented.
     *
     * Exception should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @throws IOException
     */
    public StateInterface defineMulticastRequest() throws IOException;
}
