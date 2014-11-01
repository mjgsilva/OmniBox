package logic.state;

import logic.Client;

/**
 * Waits for user to confirm the result of previous chosen operation.
 *
 * This might be used to prompt errors to user.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class WaitAnswer extends StateAdapter {
    public WaitAnswer(Client client) {
        super(client);
    }

    @Override
    public StateInterface defineReturnToRequest() {
        return super.defineReturnToRequest();
    }
}
