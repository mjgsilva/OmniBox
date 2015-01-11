package shared;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * File Operations class.
 * Common file operations that are shared by all modules
 *
 * Created by OmniBox on 09-11-2014.
 */
public class FileOperations {

    /**
     * This method savesOmniFile to disk, and returns that OmniFile.
     * File is retrieved through a TCP socket.
     *
     * @param socket
     * @param destinyDirectoryPath this param is the destiny folder to save file. Must end in / so the file may be named "temp"
     * @return OmniFile created after transfer is finished
     * @throws IOException If there's an error with file/socket operations
     * @throws FileNotFoundException If file already exists
     */
    public static OmniFile saveFileFromSocket(Socket socket, String destinyDirectoryPath) throws FileNotFoundException, IOException {
        FileOutputStream fos;
        byte[] chunk = new byte[Constants.MAX_SIZE];
        int nBytes;
        String temporaryFileName = destinyDirectoryPath;// + "temp" + new Date();

        fos = new FileOutputStream(temporaryFileName);

        while((nBytes = socket.getInputStream().read(chunk)) > 0) {
            fos.write(chunk,0,nBytes);
        }


        return new OmniFile(temporaryFileName);
    }

    /**
     * This method reads file from disk and write it on the socket.
     * Is expected for someone to be reading it from the other side of the TCP socket.
     *
     * @param socket
     * @param file
     * @throws FileNotFoundException
     * @throws IOException If there's an error with file/socket operations
     */
    public static void readFileToSocket(Socket socket, OmniFile file) throws FileNotFoundException, IOException {
        String absolutePath = file.getAbsolutePath();
        FileInputStream fis;
        OutputStream out;
        int nbytes;
        byte []fileChunck = new byte[Constants.MAX_SIZE];

        fis = new FileInputStream(absolutePath);
        out = socket.getOutputStream();

        while((nbytes = fis.read(fileChunck))>0){
            out.write(fileChunck, 0, nbytes);
            out.flush();
        }
    }
}
