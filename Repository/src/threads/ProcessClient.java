package threads;

import shared.Constants;
import shared.OmniFile;
import java.io.*;
import java.net.Socket;

/**
 * Created by OmniBox on 02/11/14.
 */
public class ProcessClient extends Thread{
    private Socket socketToClient;
    private OmniFile localDirectory;

    public ProcessClient(Socket socketToClient) {
        this.socketToClient = socketToClient;
    }

    private void saveFile(String fileName) throws IOException {
        FileOutputStream localFileOutputStream = null;
        InputStream in;
        byte []fileChunck = new byte[Constants.MAX_SIZE];
        int nbytes;


        localFileOutputStream = new FileOutputStream(fileName);

        in = socketToClient.getInputStream();

        while((nbytes = in.read(fileChunck)) > 0){
            localFileOutputStream.write(fileChunck, 0, nbytes);
        }

    }

    private void sendFile(String filename) throws IOException {
        BufferedReader in;
        OutputStream out;
        byte []fileChunck = new byte[Constants.MAX_SIZE];
        int nbytes;
        FileInputStream requestedFileInputStream = null;

        in = new BufferedReader(new InputStreamReader(socketToClient.getInputStream()));
        out = socketToClient.getOutputStream();

        requestedFileInputStream = new FileInputStream(filename);

        while((nbytes = requestedFileInputStream.read(fileChunck))>0){

            out.write(fileChunck, 0, nbytes);
            out.flush();

        }
    }

    private void deleteFile(OmniFile file){
        file.delete();
    }


    private void processMessage(){

    }



    @Override
    public void run() {
        super.run();
    }
}
