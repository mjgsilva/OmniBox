package threads;

import shared.Command;
import shared.Constants;
import shared.OmniRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by OmniBox on 02/11/14.
 */
public class AnswerClient extends Thread{
    private static OmniRepository omniRepository= null;

    public AnswerClient(OmniRepository omniRepository){
        this.omniRepository = omniRepository;
    }

    @Override
    public void run() {
        while(true){
            Socket socketToClient = null;
            try {
                socketToClient = omniRepository.socket.accept();
            } catch (IOException e) {

            }

            try {
                String response = omniRepository.getTCPMessage(socketToClient);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //TODO - How convert response to Command
            ArrayList<Object> argsList = new ArrayList<Object>();

            argsList.add("fileName");

            Command command = new Command(Constants.CMD.cmdDeleteFile,argsList);
            new ProcessClient(socketToClient, omniRepository,command).start();
        }

    }
}
