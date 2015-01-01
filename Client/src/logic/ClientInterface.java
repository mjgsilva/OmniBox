package logic;

import shared.OmniFile;

import java.io.IOException;

/**
 * Describes the interface for model classes.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public interface ClientInterface {
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
     * @param username
     * @param password
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    void defineAuthentication(final String username, final String password) throws IOException, InterruptedException, ClassNotFoundException;

    /**
     * Define Get Request.
     *
     * Receives desired OmniFile to get. It should return state WaitAnswer if there are no errors and
     * if your waiting for an answer from the other end. If not, then return WaitRequest.
     *
     * Exceptions should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @param fileToGet
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void defineGetRequest(final OmniFile fileToGet) throws InterruptedException, IOException, ClassNotFoundException;

    /**
     * Define Send Request.
     *
     * Receives desired OmniFile to send. It should return state WaitAnswer if there are no errors and
     * if your waiting for an answer from the other end. If not, then return WaitRequest.
     *
     * Exceptions should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @param fileToSend
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void defineSendRequest(final OmniFile fileToSend) throws InterruptedException, IOException, ClassNotFoundException;

    /**
     * Define Remove Request.
     *
     * Receives desired OmniFile to remove. It should return state WaitAnswer if there are no errors and
     * if your waiting for an answer from the other end. If not, then return WaitRequest.
     *
     * Exceptions should be handle on interface level, because this way its more
     * flexible to create custom interfaces.
     *
     * @param fileToRemove
     * @throws IOException
     * @throws InterruptedException
     */
    void defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException;

    void defineReturnToRequest();

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
    void defineMulticastRequest() throws IOException;
}
