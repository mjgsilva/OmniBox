package threads;

import server.OmniServer;
import shared.Constants;

/**
 * Created by mario on 29/11/14.
 */
public class HeartBeatHandler extends Thread {
    private final OmniServer omniServer;

    public HeartBeatHandler(OmniServer omniServer) {
        this.omniServer = omniServer;
    }

    @Override
    public void run() {
        while(true) {
            try {
            Thread.sleep(Constants.EXPIRE_TIME);
            } catch (InterruptedException e) { }
            omniServer.removeExpiredRepositories();
        }
    }
}
