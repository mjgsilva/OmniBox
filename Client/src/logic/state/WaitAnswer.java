package logic.state;

import logic.Client;
import shared.Constants;
import shared.FileOperations;
import shared.OmniFile;
import shared.Request;
import ui.graphic.ErrorDialog;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Waits for user to confirm the result of previous chosen operation.
 *
 * This might be used to prompt errors to user.
 *
 * Created by ©OmniBox on 01-11-2014.
 */
public class WaitAnswer extends StateAdapter {
    public WaitAnswer(Client client) {
        super(client);
    }

    @Override
    public StateInterface defineGetRequest(final OmniFile fileToGet) throws IOException, InterruptedException, ClassNotFoundException {
        final Socket s = client.getRepositorySocket();
        OmniFile omniFile = null;
        try {
            // Tell repository what kind of operation I'm requesting
            ArrayList<Object> temp = new ArrayList<Object>();
            temp.add(fileToGet);

            temp.add(client.getUser());
            Request request = new Request(Constants.CMD.cmdGetFile, temp);
            sendTCPMessage(s, request);
            omniFile = FileOperations.saveFileFromSocket(s, client.getLocalDirectoryPath() +fileToGet.getFileName());
        } catch (IOException e) {
            // Error saving file, delete it
            new ErrorDialog(null, "Error saving file to disk.");
            omniFile.delete();
            e.printStackTrace(); // TODO - DELETE THIS
        } catch (InterruptedException e) {
            new ErrorDialog(null, "Error saving file to disk.");
            omniFile.delete();
            e.printStackTrace(); // TODO - DELETE THIS
        } finally {
            // Close repository socket when over
            try {
                s.close();
            } catch (IOException e) {
                new ErrorDialog(null, "Error closing repository socket.");
            }
            client.setRepositorySocket(null);
        }

        return new WaitRequest(client);
    }

    @Override
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException {
        final Socket s = client.getRepositorySocket();
        (new Thread() {
            @Override
            public void run() {
                try {
                    // Tell repository what kind of operation I'm requesting
                    ArrayList<Object> temp = new ArrayList<Object>();
                    temp.add(fileToSend);
                    temp.add(client.getUser());
                    Request request = new Request(Constants.CMD.cmdSendFile, temp);
                    sendTCPMessage(s, request);
                    // Send file to repository
                    FileOperations.readFileToSocket(s, fileToSend);
                } catch (IOException e) {
                    e.printStackTrace(); // TODO - DELETE THIS
                    new ErrorDialog(null, "Error transfering file to repository");
                } catch (InterruptedException e) {
                    e.printStackTrace(); // TODO - DELETE THIS
                    new ErrorDialog(null, "Error transferring file to repository");
                } finally {
                    // Close repository socket when over
                    try {
                        if (s != null)
                            s.close();
                    } catch (IOException e) {}
                    client.setRepositorySocket(null);
                    client.setFileToUpload(null);
                    client.setCurrentState(new WaitRequest(client));
                }
            }
        }).start();

        return new WaitRequest(client);
    }
}
