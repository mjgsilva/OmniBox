package threads;

import server.OmniServer;
import shared.Constants;

/**
 * Heartbeat Handler.
 * Responsible for calling existing method in RepositoriesDB in 30 seconds intervals.
 * This method erases repositories in which last heartbeat notification has overpassed
 * the denoted limit.
 *
 * Created by Omnibox on 29/11/14.
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
                System.out.println("Number of Repos: " + omniServer.getNumberOfRepositories());
                Thread.sleep(Constants.EXPIRE_TIME);
            } catch (InterruptedException e) { }
            omniServer.removeExpiredRepositories();
            omniServer.removeFilesWithNoSource();
        }
    }
}
