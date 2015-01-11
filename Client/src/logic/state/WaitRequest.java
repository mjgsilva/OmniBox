package logic.state;

import communication.TCP;
import logic.Client;
import shared.Constants;
import shared.OmniFile;
import shared.Request;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Waits for user to choose which operation to be performed.
 *
 * Created by OmniBox on 01-11-2014.
 */
public class WaitRequest extends StateAdapter implements TCP {
    public WaitRequest(Client client) {
        super(client);
    }

    /**
     * Sends request to get file, this method is supposed to add that file to the Client.fileList
     *
     * @param fileToGet
     * @return next State

     */
    @Override
    public StateInterface defineGetRequest(final OmniFile fileToGet) throws IOException, InterruptedException, ClassNotFoundException {
        ArrayList <Object> cmdList = new ArrayList<Object>();
        cmdList.add(fileToGet);
        cmdList.add(client.getUser());
        Request request = new Request(Constants.CMD.cmdGetFile,cmdList);

        // Send request to get file with fileToGet as an arg and wait for repository address to be given
        sendTCPMessage(client.getServerSocket(), request);

        return new WaitAnswer(client);
    }

    @Override
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException {
        client.setFileToUpload(fileToSend);

        ArrayList <Object> cmdList = new ArrayList<Object>();
        cmdList.add(fileToSend);
        cmdList.add(client.getUser());
        Request request = new Request(Constants.CMD.cmdSendFile,cmdList);

        // Send request to send file with fileToSend as an arg and wait for repository address to be given
        sendTCPMessage(client.getServerSocket(), request);

        return new WaitAnswer(client);
    }

    @Override
    public StateInterface defineRemoveRequest(OmniFile fileToRemove) throws IOException, InterruptedException {
        ArrayList <Object> cmdList = new ArrayList<Object>();
        cmdList.add(fileToRemove);
        Request request = new Request(Constants.CMD.cmdDeleteFile,cmdList);

        // Send request to remove file with fileToRemove as an arg
        sendTCPMessage(client.getServerSocket(), request);

        return new WaitRequest(client);
    }
}
