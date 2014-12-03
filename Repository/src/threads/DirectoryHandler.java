package threads;

import shared.Constants;
import shared.OmniFile;
import shared.OmniRepository;
import shared.Request;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmniBox on 08/11/14.
 */
public class DirectoryHandler extends Thread{
    OmniRepository omniRepository;
    WatchService watcher = null;
    Path dirPath;

    public DirectoryHandler(OmniRepository omniRepository) throws IOException {
        this.omniRepository = omniRepository;
        this.dirPath = Paths.get(omniRepository.getFilesDirectory());
        watcher = dirPath.getFileSystem().newWatchService();
        dirPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Override
    public void run() {
        Request cmdTemp = new Request(Constants.CMD.cmdNotification,new ArrayList<Object>());
        OmniFile tempFile = null;

        while(true) {
            try {
                WatchKey watckKey = watcher.take();

                List<WatchEvent<?>> events = watckKey.pollEvents();
                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        //create a tempFile based on event
                        tempFile = new OmniFile(event.context().toString());
                        //add file to fileList of repository
                        omniRepository.getFileList().add(tempFile);
                        cmdTemp.getArgsList().add(Constants.OP_UPLOAD);
                        cmdTemp.getArgsList().add(Constants.OP_S_FINISHED);
                        cmdTemp.getArgsList().add(tempFile);

                        //Send a notification request to Server
                        omniRepository.sendUDPMessage(omniRepository.getSocketUDP(),omniRepository.getServerAddr(),omniRepository.getServerPort(),cmdTemp);
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        cmdTemp.getArgsList().add(Constants.OP_DELETE);
                        cmdTemp.getArgsList().add(Constants.OP_S_FINISHED);
                        cmdTemp.getArgsList().add(tempFile);

                        //remove file of fileList
                        omniRepository.getFileList().remove((OmniFile) event.context());

                        //Send a notification request to Server
                        omniRepository.sendUDPMessage(omniRepository.getSocketUDP(),omniRepository.getServerAddr(),omniRepository.getServerPort(),cmdTemp);
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        //create a tempFile based on event
                        tempFile = new OmniFile(event.context().toString());
                        //add file to fileList of repository
                        omniRepository.getFileList().add(tempFile);
                        cmdTemp.getArgsList().add(Constants.OP_UPLOAD);
                        cmdTemp.getArgsList().add(Constants.OP_S_FINISHED);
                        cmdTemp.getArgsList().add(tempFile);

                        //Send a notification request to Server
                        omniRepository.sendUDPMessage(omniRepository.getSocketUDP(),omniRepository.getServerAddr(),omniRepository.getServerPort(),cmdTemp);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }

        }
    }
}
