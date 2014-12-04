package threads;

import shared.Constants;
import shared.OmniRepository;
import shared.Request;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;

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
        Request cmdTemp = null;
        ArrayList<Object> tempList = new ArrayList<Object>();
        while(true) {
            try {
                tempList.clear();
                tempList.add(omniRepository);
                cmdTemp = new Request(Constants.CMD.cmdHeartBeat, tempList);
                omniRepository.sendUDPMessage(omniRepository.getSocketUDP(),omniRepository.getServerAddr(),omniRepository.getServerPort(), cmdTemp);
                System.out.println("Send UDP Message");
                Thread.sleep(Constants.EXPIRE_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
