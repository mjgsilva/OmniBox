package threads;

import com.sun.tools.internal.jxc.apt.Const;
import server.OmniServer;
import shared.*;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by OmniBox on 13/11/14.
 */
public class ProcessRepository extends Thread {
    private final DatagramSocket socket;
    private final OmniServer omniServer;

    public ProcessRepository(DatagramSocket socket, OmniServer omniServer) throws IOException{
        this.socket = socket;
        this.omniServer = omniServer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Request request = omniServer.getUDPMessage(socket);

                    if (request instanceof Request) {
                        switch (request.getCmd()) {
                            case cmdHeartBeat:
                                ProcessHeartBeat(request);
                                break;
                            case cmdNotification:
                                ProcessNotification(request);
                                break;
                        }
                    }

                } catch (ClassNotFoundException e) {
                } catch (InterruptedException e) {
                } catch (IOException e) {
                }
            }
        } finally{

        }
    }

    private void ProcessHeartBeat(Request request) {
        OmniRepository omniRepository = (OmniRepository)request.getArgsList().get(0);
        omniServer.addRepository(omniRepository);
    }

    private void ProcessNotification(Request request) {
        int operationType = (Integer)request.getArgsList().get(0);
        int status = (Integer)request.getArgsList().get(1);
        OmniFile omniFile = (OmniFile)request.getArgsList().get(2);
        User user = (User)request.getArgsList().get(3);

        if(operationType == Constants.OP_DOWNLOAD) {
            if(status == Constants.OP_S_STARTED) {
                omniServer.editUserActivity(user, Constants.OP_DOWNLOAD);
                omniServer.fileBeingAccessed(omniFile);
            } else {
                if(status == Constants.OP_S_FINISHED) {
                    omniServer.editUserActivity(user, Constants.INACTIVE);
                    omniServer.remoteAccessToFile(user);
                }
            }
        }
    }
}
