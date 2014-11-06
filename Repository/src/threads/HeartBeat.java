package threads;

import shared.OmniRepository;

/**
 * Created by OmniBox on 02/11/14.
 */
public class HeartBeat extends Thread{
    private static OmniRepository omniRepository= null;

    public HeartBeat(OmniRepository omniRepository) {
        this.omniRepository = omniRepository;
    }

    @Override
    public void run() {
        super.run();
    }
}
