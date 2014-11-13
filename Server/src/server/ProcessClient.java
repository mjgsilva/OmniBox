package server;

import shared.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Object obj;

        try {
            try {

                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                obj = in.readObject();

                if (obj instanceof User) {
                    out.writeObject(omniServer.login((User) obj));
                }

            } catch (ClassNotFoundException e) {
            }
        } catch (IOException e) {
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
            }
        }
    }
}
