package logic;

import communication.CommunicationAdapter;
import shared.Constants;
import shared.OmniFile;
import shared.Request;
import ui.graphic.ListPanel;

import javax.swing.*;
import java.io.IOException;
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

    public ListController(Client client, ListPanel filesList) {
        this.client = client;
    }

    /**
     * List Controller Thread.
     * Is a daemon thread.
     * Thread to keep reading the socket from server.
     *
     * Updates files List.
     * If what is read is a request, instantiates a thread to handle the request.
     * The thread to handle requests is user type.
     */
    public class ListControllerThread extends Thread {
        private final ListPanel filesList;

        public ListControllerThread(ListPanel filesList) {
            setDaemon(true);
            this.filesList = filesList;
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
                            //client.define
                            break;
                        case cmdNotification:

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
