package communication;

import java.io.IOException;

/**
 * Multicast method implementation
 *
 * Created by OmniBox on 02-11-2014.
 */
public interface Multicast {

    /**
     * This method sends the only available muticast message and waits for response.
     *
     * Returns the reponse as a <b>String</b> object.
     *
     * @param messageToSend
     * @return Server's Address
     * @throws IOException
     */
    public String sendMulticastMessage(String messageToSend) throws IOException;
}
