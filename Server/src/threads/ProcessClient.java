package threads;

import communication.CommunicationAdapter;
import server.OmniServer;
import shared.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by OmniBox on 02/11/14.
 */
public class ProcessClient extends Thread {
    final private Socket socket;
    final private OmniServer omniServer;

    public ProcessClient(Socket socket,OmniServer omniServer) {
        this.socket = socket;
        this.omniServer = omniServer;
    }

    public void run() {
        while(true) {
            try {
                Request request = omniServer.getTCPMessage(socket);

                if (request instanceof Request) {
                    switch (request.getCmd()) {
                        case cmdAuthenticate:
                            autheticate(request);
                            break;
                        case cmdSendFile:
                            upload(request);
                            break;
                    }
                }
            } catch (ClassNotFoundException e) {
            } catch (InterruptedException e) {
            } catch (IOException e) {
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {}
            }
        }
    }


    private void sendMessage(Request response) {
        try {
            omniServer.sendTCPMessage(socket, response);
        } catch (InterruptedException e) {
        } catch (IOException e) { }
    }

    private void autheticate(Request req) {
        User user = (User) req.getArgsList().get(0);
        ArrayList args = new ArrayList();
        args.add(omniServer.login(user));
        Request response = new Request(Constants.CMD.cmdAuthenticate,args);
        sendMessage(response);
    }

    private void upload(Request request) {
        OmniFile omniFile = (OmniFile) request.getArgsList().get(0);
        ArrayList args = new ArrayList();
        args.add(Constants.OP_DOWNLOAD);

        if(omniServer.fileExists(omniFile)) {
            OmniRepository omniRepository = omniServer.getLessWorkloadedRepository();
            args.add(omniRepository.getAddressServer());
            args.add(omniRepository.getPort());
            args.add(Constants.FILEOK);
        } else {
            args.add(null);
            args.add(null);
            args.add(Constants.FILENOTOK);
        }
        Request response = new Request(Constants.CMD.cmdRepositoryAddress,args);
        sendMessage(response);
    }
}
