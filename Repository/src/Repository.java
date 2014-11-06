import shared.OmniRepository;
import threads.AnswerClient;
import threads.HeartBeat;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */


public class Repository {

    public static void main(String[] args) throws IOException {
        OmniRepository repo = null;
        int port;
        String address;
        String filesDirectory;

        if(args.length != 1){
            if (args.length != 3) {
                System.out.println("Syntax: java <port> <address> <fileDB> // java <port>");
                return;
            }
        }

        switch (args.length) {
            case 3:
                port = Integer.parseInt(args[1]);
                address = args[1].trim();
                filesDirectory = args[2].trim();
                repo = new OmniRepository(port,address,filesDirectory);
                new AnswerClient(repo).start();
                new HeartBeat(repo).start();
            case 1:
                port = Integer.parseInt(args[1]);

                repo = new OmniRepository(port);
                new AnswerClient(repo).start();
                new HeartBeat(repo).start();
            default:
                return;
        }

    }
}
