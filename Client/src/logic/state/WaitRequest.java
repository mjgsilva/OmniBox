package logic.state;

import communication.TCP;
import logic.Client;
import shared.Constants;
import shared.FileOperations;
import shared.OmniFile;
import shared.Request;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static shared.FileOperations.readFileToSocket;
import static shared.FileOperations.saveFileFromSocket;

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
        Socket repositorySocket = null;

        // add fileToGet to request args
        filesToGet.add(fileToGet);

        // Send request to get file with fileToGet as an arg and wait for repository address to be given
        sendTCPMessage(client.getServerSocket(), new Request(Constants.CMD.cmdGetFile, filesToGet));

        try {
            // Wait for server to retrieve repository address
            Request repositoryAddr = getTCPMessage(client.getServerSocket());
            if (repositoryAddr.getCmd() == Constants.CMD.cmdRepositoryAddress)
                repositorySocket = new Socket((String) repositoryAddr.getArgsList().get(0), (Integer) repositoryAddr.getArgsList().get(1));
            else
                throw new ClassNotFoundException("Problem retrieving repository address.");

            // Get file attributes from repository
            // TODO: Integration -> GetFileCommand; OmniFile retrievedFile = getFile(repositorySocket);

            // Save file on disk
            //saveFileFromSocket(repositorySocket, client.getLocalDirectoryPath()).renameTo(new File(retrievedFile.getFileName() + retrievedFile.getFileExtension()));
        } finally {
            if (repositorySocket != null && !repositorySocket.isClosed())
                repositorySocket.close();
        }

        return this;
    }

    @Override
    public StateInterface defineSendRequest(final OmniFile fileToSend) throws IOException, InterruptedException, ClassNotFoundException {
        ArrayList <OmniFile> filesToSend = new ArrayList<OmniFile>();
        Socket repositorySocket = null;

        // add fileToSend to request args
        filesToSend.add(fileToSend);

        // Send request to send file with fileToSend as an arg and wait for repository address to be given
        sendTCPMessage(client.getServerSocket(), new Request(Constants.CMD.cmdSendFile, filesToSend));

        try {
            // Wait for server to retrieve repository address
            Request repositoryAddr = getTCPMessage(client.getServerSocket());
            //TODO: Integration -> Verify file ok / not ok
            if (repositoryAddr.getCmd() == Constants.CMD.cmdRepositoryAddress)
                repositorySocket = new Socket((String) repositoryAddr.getArgsList().get(0), (Integer) repositoryAddr.getArgsList().get(1));
            else
                throw new ClassNotFoundException("Problem retrieving repository address.");

            // Send file attributes to repository
            //TODO: Integration -> CMDSendFile; sendFile(repositorySocket, fileToSend);

            // Read file to socket
            readFileToSocket(repositorySocket, fileToSend);
        } finally {
            if (repositorySocket != null && !repositorySocket.isClosed())
                repositorySocket.close();
        }

        return this;
    }

    @Override
    public StateInterface defineRemoveRequest(final OmniFile fileToRemove) throws IOException, InterruptedException {
        ArrayList <OmniFile> filesToRemove = new ArrayList<OmniFile>();

        // add fileToRemove to request args
        filesToRemove.add(fileToRemove);

        // Send request to remove file with fileToRemove as an arg
        sendTCPMessage(client.getServerSocket(), new Request(Constants.CMD.cmdDeleteFile, filesToRemove));

        return this;
    }

    @Override
    public String toString(String s) {
        return null;
    }
}
