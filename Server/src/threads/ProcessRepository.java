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
                    e.printStackTrace();
                }
            }
        } finally{

        }
    }

    private void ProcessHeartBeat(Request request) {
        OmniRepository omniRepository = (OmniRepository)request.getArgsList().get(0);
        omniServer.addRepository(omniRepository);
<<<<<<< HEAD
        System.out.println("Ligou-se! " + omniRepository.getLocalAddr().getHostAddress().toString());
=======
        System.out.println("Ligou-se! " + omniRepository.getLocalAddr().getHostAddress() + " / " + omniRepository.getPort());
>>>>>>> 566f7dc... Server integration progress
    }

    private void ProcessNotification(Request request) {
        int operationType = (Integer)request.getArgsList().get(0);
        int status = (Integer)request.getArgsList().get(1);
        OmniFile omniFile = (OmniFile)request.getArgsList().get(2);
        User user = (User)request.getArgsList().get(3);

        if(operationType == Constants.OP_DOWNLOAD) {
<<<<<<< HEAD
            downloadNotification(status,omniFile,user);
        } else {
            if(operationType == Constants.OP_UPLOAD) {
                uploadNotification(status,omniFile,user);
=======
            downloadNotification(status,omniFile,user,isSucessful,omniRepository);
        } else {
            if(operationType == Constants.OP_UPLOAD) {
                uploadNotification(status,omniFile,user,omniRepository);
>>>>>>> 566f7dc... Server integration progress
            } else {
                if(operationType == Constants.OP_DELETE) {
                    deleteNotification(status,omniFile,user);
                    //TODO: is this really necessary? deleteNotification(status,omniFile);
                }
            }
        }
    }

<<<<<<< HEAD
    private void downloadNotification(int status,OmniFile omniFile,User user) {
=======
    private void uploadNotification(int status,OmniFile omniFile,User user,OmniRepository omniRepository) {
>>>>>>> 566f7dc... Server integration progress
        if(status == Constants.OP_S_STARTED) {
            omniServer.editUserActivity(user, Constants.OP_DOWNLOAD);
            omniServer.addAccessToFile(user, omniFile);
        } else {
            if(status == Constants.OP_S_FINISHED) {
                omniServer.editUserActivity(user, Constants.INACTIVE);
                omniServer.removeAccessToFile(user);
            }
        }
        //TODO: E se a transf falhar? <- Considerar uma opção de FILEOK/FILENOTOK
    }

<<<<<<< HEAD
    private void uploadNotification(int status,OmniFile omniFile,User user) {
=======
    private void downloadNotification(int status,OmniFile omniFile,User user,Boolean isSucessful,OmniRepository omniRepository) {
>>>>>>> 566f7dc... Server integration progress
        if(status == Constants.OP_S_STARTED) {
            omniServer.editUserActivity(user, Constants.OP_UPLOAD);
        } else {
            if(status == Constants.OP_S_FINISHED) {
                omniServer.editUserActivity(user, Constants.INACTIVE);
<<<<<<< HEAD
                //TODO: E se a transf falhar? <- Considerar uma opção de FILEOK/FILENOTOK
=======
                if(isSucessful && (omniFile!=null)) {
                    omniServer.addFile(omniFile);
                }
>>>>>>> 566f7dc... Server integration progress
            }
        }
    }

    private void deleteNotification(int status,OmniFile omniFile,User user) {
        if(status == Constants.OP_S_STARTED) {
            omniServer.editUserActivity(user, Constants.OP_DELETE);
        } else {
            if(status == Constants.OP_S_FINISHED) {
                omniServer.editUserActivity(user, Constants.INACTIVE);
                //TODO: Review this method
            }
        }
    }
}
