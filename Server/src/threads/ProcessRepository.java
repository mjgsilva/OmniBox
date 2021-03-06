package threads;

import server.OmniServer;
import shared.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;

/**
 * Process Repository.
 * Responsible for handling repositories requests. Request is made as a
 * Request object, this request may be of the following types: heartbeat
 * or notification.
 *
 * Created by OmniBox on 13/11/14.
 */
public class ProcessRepository extends Thread {
    private final OmniServer omniServer;

    public ProcessRepository(OmniServer omniServer) throws IOException{
        this.omniServer = omniServer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Request request = omniServer.getUDPMessage(omniServer.getDatagramSocket());
                    if (request != null) {
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
                    System.out.println("Received an invalid request from repository");
                } catch (InterruptedException e) {
                } catch (IOException e) {
                    System.out.println("A problem occurred while processing the repository call");
                }
            }
        } finally{
            //close socket
            omniServer.getDatagramSocket().close();
        }
    }

    private void ProcessHeartBeat(Request request) {
        OmniRepository omniRepository = (OmniRepository)request.getArgsList().get(0);
        omniRepository.setLocalAdd((String) request.getArgsList().get(request.getArgsList().size() - 1));
        omniServer.addRepository(omniRepository);
        omniServer.sendServiceNotification();
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
                    ArrayList args = new ArrayList();
                    args.add(omniFile);
                    Request repositoryResponse = new Request(Constants.CMD.cmdDeleteFile,args);
                    if (this.omniServer.customRemoveFile(omniFile)) {
                        this.omniServer.deleteBroadcast(repositoryResponse);
                        omniServer.notifyClients();
                    }
                    deleteNotification(omniRepository);
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
                    if (!omniServer.fileExists(omniFile))
                        omniServer.addFile(omniFile);
                    if(user!=null)
                        omniServer.notifyClients();
                    System.out.println("* Replicating *");
                    omniServer.replicationProcess(omniFile);
                }
            }
        }
        omniServer.sendServiceNotification();
    }

    private void deleteNotification(OmniRepository omniRepository) {
        omniServer.addRepository(omniRepository);
        omniServer.sendServiceNotification();
    }
}
