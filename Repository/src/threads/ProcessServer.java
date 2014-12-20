package threads;

import com.sun.tools.internal.jxc.apt.Const;
import shared.*;

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
        //Request request = null;
        while(true) {
            try {
                System.out.println("Ready to receive UDP messages from the Server!");
                Request request = omniRepository.getUDPMessage(dSocket);
                System.out.println("Get UDP Message: " + request.getCmd());

                switch (request.getCmd()) {
                    case cmdRepositoryAddress:
                        if ((Integer) request.getArgsList().get(0) == Constants.OP_REPLICATION) {
                            System.out.println("* Server's asking for a replication *");
                            //Create socket with addr and port that server send in notification request
                            String destinationAddress = (String) request.getArgsList().get(2);
                            Integer destinationPort = (Integer) request.getArgsList().get(3);

                            Socket socketTemp = new Socket(destinationAddress, destinationPort);
                            System.out.println("Ready to send to -> " + socketTemp.getInetAddress() + ":" + socketTemp.getPort());


                            OmniFile omniFile = (OmniFile) request.getArgsList().get(1);
                            ArrayList args = new ArrayList();

                            args.add(omniFile.getFileName());
                            args.add(null);
                            Request req = new Request(Constants.CMD.cmdSendFile, args);
                            omniRepository.sendTCPMessage(socketTemp, req);
                            omniRepository.sendFile(socketTemp, omniFile, null);
                            //omniRepository.sendFile(socketTemp, omniRepository.getOmniFileByName(((OmniFile)request.getArgsList().get(1)).getFileName()),null);
                        }
                        break;
                    case cmdDeleteFile:
                        omniRepository.deleteFile((String) request.getArgsList().get(0), null);
                        break;
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
}
