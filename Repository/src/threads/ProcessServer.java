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

    public ProcessServer(OmniRepository omniRepository,String serverAddr, int serverPort) throws UnknownHostException, SocketException {
        this.omniRepository = omniRepository;
        InetAddress inetServerAddr = InetAddress.getByName(serverAddr);

        dSocket = new DatagramSocket(serverPort, inetServerAddr);
    }

    @Override
    public void run() {
        ObjectInputStream in=null;
        Request request = null;

        packet = new DatagramPacket(new byte[Constants.MAX_SIZE], Constants.MAX_SIZE);
        try {
            dSocket.receive(packet);
            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
            request = (Request)in.readObject();


            switch (request.getCmd()) {
                case cmdNotification:
                    if((Integer)request.getArgsList().get(0) == Constants.OP_REPLY)
                    {
                        ArrayList<Object> tempList = new ArrayList<Object>();
                        tempList.add(Constants.OP_UPLOAD);
                        tempList.add(Constants.OP_STARTED);
                        Request reqTemp = new Request(Constants.CMD.cmdNotification,tempList);

                        omniRepository.sendUDPMessage(dSocket,reqTemp);


                        //Create socket with addr and port that server send in notification request
                        Socket socketTemp= new Socket((String)request.getArgsList().get(1),(Integer)request.getArgsList().get(2));

                        //TODO-> enviar ficheiro pelo nome
                        omniRepository.sendFile(socketTemp,null);
                    }
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
