package threads;

import shared.Constants;
import shared.OmniFile;
import shared.OmniRepository;
import shared.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by OmniBox on 29/11/14.
 */
public class ProcessServer extends Thread  {

    private DatagramSocket dSocket = null;
    private DatagramPacket packet=null;
    private OmniRepository omniRepository;

    public ProcessServer(OmniRepository omniRepository) throws UnknownHostException, SocketException {
        this.omniRepository = omniRepository;
        InetAddress inetServerAddr = InetAddress.getByName(omniRepository.getAddressServer());

        dSocket = omniRepository.getSocketUDP();//new DatagramSocket(omniRepository.getServerPort(), inetServerAddr);
    }

    @Override
    public void run() {
        Request request = null;
        try {
            request = omniRepository.getUDPMessage(dSocket);


            switch (request.getCmd()) {
                case cmdNotification:
                    if((Integer)request.getArgsList().get(0) == Constants.OP_REPLICATION)
                    {
                        //Create socket with addr and port that server send in notification request
                        Socket socketTemp= new Socket((String)request.getArgsList().get(1),(Integer)request.getArgsList().get(2));

                        omniRepository.sendFile(socketTemp, omniRepository.getOmniFileByName((String) request.getArgsList().get(3)));
                    }
                case cmdDeleteFile:
                    omniRepository.deleteFile((String) request.getArgsList().get(0));
                default:
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
