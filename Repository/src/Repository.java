import shared.OmniFile;
import shared.OmniRepository;
import threads.AnswerClient;
import threads.DirectoryHandler;
import threads.HeartBeat;
import threads.ProcessServer;

import java.io.File;
import java.io.IOException;

/**
 * Created by OmniBox on 02/11/14.
 */


public class Repository {

    public static void main(String[] args) throws IOException {
        OmniRepository repo = null;
        int port;
        String address;
        String filesDirectory;
        AnswerClient answerClient;

        if(args.length != 1){
            if (args.length != 3) {
                System.out.println("Syntax: java <port> <address> <filesDirectory> // java <port>");
                return;
            }
        }


        switch (args.length) {
            case 3:
                port = Integer.parseInt(args[0]);
                address = args[1].trim();
                filesDirectory = args[2].trim();
                File folder = new File(filesDirectory);

                repo = new OmniRepository(port,address,filesDirectory, "");

                //Add Files in directory on start
                for (final File fileEntry : folder.listFiles()) {
                    if (!fileEntry.isDirectory()) {
                        repo.getFileList().add(new OmniFile(filesDirectory+fileEntry.getName()));
                    }
                }

                answerClient = new AnswerClient(repo);
                answerClient.start();
                new HeartBeat(repo).start();
                new DirectoryHandler(repo).start();
                new ProcessServer(repo).start();
                break;
            case 1:
                port = Integer.parseInt(args[0]);

                repo = new OmniRepository(port);
                new AnswerClient(repo).start();
                new HeartBeat(repo).start();
                new DirectoryHandler(repo).start();
                new ProcessServer(repo).start();
                break;
            default:
                return;
        }

    }
}
