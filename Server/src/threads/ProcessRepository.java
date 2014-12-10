package threads;

import com.sun.org.apache.xpath.internal.operations.Bool;
//import com.sun.tools.internal.jxc.apt.Const;
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
        System.out.println("Ligou-se! " + omniRepository.getLocalAddr().getHostAddress().toString() + " / " + omniRepository.getPort());
    }

    private void ProcessNotification(Request request) {
        int operationType = (Integer)request.getArgsList().get(0);
        int status = (Integer)request.getArgsList().get(1);
        OmniFile omniFile = (OmniFile)request.getArgsList().get(2);
        User user = (User)request.getArgsList().get(3);
        boolean isSucessful = (Boolean)request.getArgsList().get(4);
        OmniRepository omniRepository = (OmniRepository)request.getArgsList().get(5);


        if(operationType == Constants.OP_DOWNLOAD) {
            downloadNotification(status,omniFile,user,omniRepository);
        } else {
            if(operationType == Constants.OP_UPLOAD) {
                uploadNotification(status,omniFile,user,isSucessful,omniRepository);
            } else {
                if(operationType == Constants.OP_DELETE) {
                    deleteNotification(status,omniFile,user,omniRepository);
                } else {
                    if(operationType == Constants.OP_REPLICATION) {
                        replicationNotification(status,omniFile);
                    }
                }
            }
        }
    }

    private void downloadNotification(int status,OmniFile omniFile,User user,OmniRepository omniRepository) {
        if(status == Constants.OP_S_STARTED) {
            omniServer.editUserActivity(user, Constants.OP_DOWNLOAD);
            omniServer.addAccessToFile(user, omniFile);
        } else {
            if(status == Constants.OP_S_FINISHED) {
                omniServer.editUserActivity(user, Constants.INACTIVE);
                omniServer.removeAccessToFile(user);
            }
        }
        omniServer.addRepository(omniRepository);
    }

    private void uploadNotification(int status,OmniFile omniFile,User user,Boolean isSucessful,OmniRepository omniRepository) {
        if(status == Constants.OP_S_STARTED) {
            omniServer.editUserActivity(user, Constants.OP_UPLOAD);
        } else {
            if(status == Constants.OP_S_FINISHED) {
                omniServer.editUserActivity(user, Constants.INACTIVE);
                if(!isSucessful) {
                    omniServer.removeFile(omniFile);
                }
            }
        }
        omniServer.addRepository(omniRepository);
        omniServer.notifyClients();
        omniServer.replicationProcess(omniFile);
    }

    private void deleteNotification(int status,OmniFile omniFile,User user,OmniRepository omniRepository) {
        if(status == Constants.OP_S_STARTED) {
            omniServer.editUserActivity(user, Constants.OP_DELETE);
        } else {
            if(status == Constants.OP_S_FINISHED) {
                omniServer.editUserActivity(user, Constants.INACTIVE);
            }
        }
        omniServer.addRepository(omniRepository);
        omniServer.notifyClients();
    }

    private void replicationNotification(int status,OmniFile omniFile) {
        omniServer.replicationProcess(omniFile);
    }
}
