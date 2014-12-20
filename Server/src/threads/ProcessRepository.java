package threads;

import server.OmniServer;
import shared.*;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by OmniBox on 13/11/14.
 */
public class ProcessRepository extends Thread {
    private final OmniServer omniServer;

    public ProcessRepository(DatagramSocket socket, OmniServer omniServer) throws IOException{
        this.omniServer = omniServer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Request request = omniServer.getUDPMessage(omniServer.getDatagramSocket());
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
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally{
            //close socket
        }
    }

    private void ProcessHeartBeat(Request request) {
        OmniRepository omniRepository = (OmniRepository)request.getArgsList().get(0);
        omniRepository.setLocalAdd((String) request.getArgsList().get(request.getArgsList().size() - 1));
        omniServer.addRepository(omniRepository);
    }

    private void ProcessNotification(Request request) {
        int operationType = (Integer)request.getArgsList().get(0);
        int status = (Integer)request.getArgsList().get(1);
        OmniFile omniFile = (OmniFile)request.getArgsList().get(2);
        User user = (User)request.getArgsList().get(3);
        boolean isSuccessful = (Boolean)request.getArgsList().get(4);
        OmniRepository omniRepository = (OmniRepository)request.getArgsList().get(5);
        omniRepository.setLocalAdd((String) request.getArgsList().get(request.getArgsList().size() - 1));

        if(operationType == Constants.OP_DOWNLOAD) {
            downloadFromClientNotification(status, omniFile, user, isSuccessful, omniRepository);
        } else {
            if(operationType == Constants.OP_UPLOAD) {
                uploadToClientNotification(status, omniFile, user, omniRepository);
            } else {
                if(operationType == Constants.OP_DELETE) {
                    deleteNotification(status,omniFile,user,omniRepository);
                }
            }
        }
    }

    private void uploadToClientNotification(int status,OmniFile omniFile,User user,OmniRepository omniRepository) {
        omniServer.addRepository(omniRepository);
        if(user != null) {
            if (status == Constants.OP_S_STARTED) {
                omniServer.editUserActivity(user, Constants.OP_DOWNLOAD);
                omniServer.addAccessToFile(user, omniFile);
            } else {
                if (status == Constants.OP_S_FINISHED) {
                    omniServer.editUserActivity(user, Constants.INACTIVE);
                    omniServer.removeAccessToFile(user);
                }
            }
        }
        omniServer.sendServiceNotification();
    }

    private void downloadFromClientNotification(int status,OmniFile omniFile,User user,Boolean isSuccessful,OmniRepository omniRepository) {
        System.out.println("* OmniRepository notification received *");
        omniServer.addRepository(omniRepository);
        if (status == Constants.OP_S_STARTED) {
            if(user != null)
                omniServer.editUserActivity(user, Constants.OP_UPLOAD);
        } else {
            if (status == Constants.OP_S_FINISHED) {
                if(user!=null)
                    omniServer.editUserActivity(user, Constants.INACTIVE);
                if (!isSuccessful) {
                    omniServer.removeFile(omniFile);
                    omniServer.notifyClients();
                } else {
                    if(user!=null)
                        omniServer.notifyClients();
                    System.out.println("* Replicating *");
                    omniServer.replicationProcess(omniFile);
                }
            }
        }
        omniServer.sendServiceNotification();
    }

    private void deleteNotification(int status,OmniFile omniFile,User user,OmniRepository omniRepository) {
        omniServer.addRepository(omniRepository);
        omniServer.notifyClients();
        if (user != null) {
            if (status == Constants.OP_S_STARTED) {
                omniServer.editUserActivity(user, Constants.OP_DELETE);
            } else {
                if (status == Constants.OP_S_FINISHED) {
                    omniServer.editUserActivity(user, Constants.INACTIVE);
                }
            }
        }
        omniServer.sendServiceNotification();
    }
}
