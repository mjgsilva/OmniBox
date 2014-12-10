package logic;

import communication.CommunicationAdapter;
import shared.Constants;
import shared.OmniFile;
import shared.Request;
import ui.graphic.ErrorDialog;
import ui.graphic.ListPanel;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import static shared.Constants.CMD.*;

/** ListController class.
 * Responsible for handling the files list updates.
 * If there are changes, it changes the JList.
 *
 * Created by OmniBox on 29-11-2014.
 */
public class ListController extends CommunicationAdapter {
    private final Client client;
    private final ArrayList<OmniFile> files = new ArrayList<OmniFile>();
    private final ListPanel filesList;

    public ListController(Client client, ListPanel filesList) {
        this.client = client;
        this.filesList = filesList;
    }

    /**
     * Method should only be called when authentication is a success.
     */
    public void startListController() {
        new ListControllerThread().start();
    }

    /**
     * List Controller Thread.
     * Its a daemon thread.
     * Thread to keep reading the socket from server.
     *
     * Updates files List.
     * If what is read is a request, instantiates a thread to handle the request.
     * The thread to handle requests is user type.
     */
    public class ListControllerThread extends Thread {

        public ListControllerThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Request request = getTCPMessage(client.getServerSocket());

                    // Handle request
                    Constants.CMD cmd = request.getCmd();
                    switch (cmd) {
                        case cmdRepositoryAddress:
                            ArrayList<Object> args = request.getArgsList();
                            int operation = (Integer)(args.get(0));
                            String repositoryAddress = (String) args.get(1);
                            int repositoryPort = ((Integer) args.get(2)) == null ? 0 : (Integer) args.get(2);
  
                            if ((Boolean) args.get(3) && repositoryAddress != null && repositoryPort != 0) {
                                System.out.println("Repo addr: " + repositoryAddress + "\nRepo port: " + repositoryPort + "\n");
                                client.setRepositorySocket(new Socket(repositoryAddress, repositoryPort));
                                if (operation == Constants.OP_UPLOAD) {
                                    // Client has to be on state WaitAnswer for this to work correctly
                                    client.defineSendRequest(client.getFileToUpload());
                                } else if (operation == Constants.OP_DOWNLOAD) {
                                    // Client has to be on state WaitAnswer for this to work correctly
                                    client.defineGetRequest(new OmniFile(ListPanel.getFilesList().getSelectedValue()));
                                }
                            } else
                                new ErrorDialog(null, "Server didn't authorize the operation.");
                            break;
                        case cmdRefreshList:
                            ListPanel.getFilesList().removeAll();
                            ArrayList<Object> temp = request.getArgsList();
                            for (Object aux : temp) {
                                filesList.addItemToList(((OmniFile) aux).getFileName());
                            }
                            break;
                        default:
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(-1);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
