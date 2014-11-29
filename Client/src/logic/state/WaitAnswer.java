package logic.state;

import logic.Client;
import shared.FileOperations;
import shared.OmniFile;

import java.io.IOException;
import java.net.Socket;

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
        (new Thread() {
            @Override
            public void run() {
                try {
                    FileOperations.saveFileFromSocket(s, client.getLocalDirectoryPath() + OmniFile.separator);
                    // Rename file
                    new OmniFile(client.getLocalDirectoryPath() + OmniFile.separator + "temp").renameTo(new OmniFile(fileToGet.getFileName()));
                } catch (IOException e) {
                    // Error saving file - Delete it
                    new OmniFile(client.getLocalDirectoryPath() + OmniFile.separator + "temp").delete();
                } finally {
                    // Close repository socket when over
                    try {
                        client.getRepositorySocket().close();
                    } catch (IOException e) {}
                    client.setRepositorySocket(null);
                }
            }
        }).start();

        return new WaitRequest(client);
    }

    @Override
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException {
        final Socket s = client.getRepositorySocket();
        (new Thread() {
            @Override
            public void run() {
                try {
                    FileOperations.readFileToSocket(s, fileToSend);
                    // Rename file
                    new OmniFile(client.getLocalDirectoryPath() + OmniFile.separator + "temp").renameTo(new OmniFile(fileToSend.getFileName()));
                } catch (IOException e) {
                } finally {
                    // Close repository socket when over
                    try {
                        client.getRepositorySocket().close();
                    } catch (IOException e) {}
                    client.setRepositorySocket(null);
                }
            }
        }).start();

        return new WaitRequest(client);
    }

    @Override
    public StateInterface defineReturnToRequest() {
        return super.defineReturnToRequest();
    }
}
