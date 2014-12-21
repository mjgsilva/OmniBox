package logic.state;

import communication.TCP;
import logic.Client;
import shared.Constants;
import shared.OmniFile;
import shared.Request;
import ui.graphic.ErrorDialog;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Waits for user to choose which operation to be performed.
 *
 * Created by ©OmniBox on 01-11-2014.
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
        ArrayList <OmniFile> filesToGet = new ArrayList<OmniFile>();
        ArrayList <Object> cmdList = new ArrayList<Object>();
        cmdList.add(fileToGet);
        cmdList.add(client.getUser());
        Request request = new Request(Constants.CMD.cmdGetFile,cmdList);
        Socket repositorySocket = null;

        new ErrorDialog(null, fileToGet.getFileName());

        // add fileToGet to request args
        filesToGet.add(fileToGet);

        // Send request to get file with fileToGet as an arg and wait for repository address to be given
        sendTCPMessage(client.getServerSocket(), request);

        return new WaitAnswer(client);
    }

    @Override
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException {
        ArrayList <OmniFile> filesToSend = new ArrayList<OmniFile>();
        Socket repositorySocket = null;
        client.setFileToUpload(fileToSend);

        new ErrorDialog(null, fileToSend.getFileName());

        ArrayList <Object> cmdList = new ArrayList<Object>();
        cmdList.add(fileToSend);
        cmdList.add(client.getUser());
        Request request = new Request(Constants.CMD.cmdSendFile,cmdList);

        // add fileToSend to request args
        filesToSend.add(fileToSend);

        // Send request to send file with fileToSend as an arg and wait for repository address to be given
        sendTCPMessage(client.getServerSocket(), request);

        return new WaitAnswer(client);
    }

    @Override
    public StateInterface defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException {
        ArrayList <OmniFile> filesToRemove = new ArrayList<OmniFile>();

        // add fileToRemove to request args
        filesToRemove.add(fileToRemove);

        ArrayList <Object> cmdList = new ArrayList<Object>();
        cmdList.add(fileToRemove);
        cmdList.add(client.getUser());
        Request request = new Request(Constants.CMD.cmdDeleteFile,cmdList);

        // Send request to remove file with fileToRemove as an arg
        sendTCPMessage(client.getServerSocket(), request);

        return this;
    }

    @Override
    public String toString(String s) {
        return null;
    }
}
