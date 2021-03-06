package threads;

import shared.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by OmniBox on 08/11/14.
 *
 * this thread is responsible to monitoring a directory of a respective repository
 * if a file is deleted, modified or created, the directoryHandler has the same behavior of client.
 */
public class DirectoryHandler extends Thread{
    OmniRepository omniRepository;
    WatchService watcher = null;
    Path dirPath;
    User userWatcher;

    public DirectoryHandler(OmniRepository omniRepository) throws IOException {
        this.omniRepository = omniRepository;

        // Create a new Watch Service
        watcher = FileSystems.getDefault().newWatchService();

        //
        this.dirPath = Paths.get(omniRepository.getFilesDirectory());

        // Register events
        this.dirPath.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

        //Create UserWatcher
        userWatcher = new User("Watcher","Watcher123");
    }

    @Override
    public void run() {
        try{
            Request cmdTemp = new Request(Constants.CMD.cmdNotification,new ArrayList<Object>());
            OmniFile tempFile = null;

            System.out.println("Watching path: " + this.dirPath);

            // We obtain the file system of the Path
            FileSystem fs = this.dirPath.getFileSystem ();

            WatchKey key = null;
            while(true) {

                key = this.watcher.take();
                if(omniRepository.getNotifyWatcher()) {

                    WatchEvent.Kind<?> kind = null;
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        // Get the type of the event
                        kind = watchEvent.kind();
                        if (OVERFLOW == kind) {
                            continue; //loop
                        } else if (ENTRY_CREATE == kind) {
                            // A new Path was created
                            Path newPath = ((WatchEvent<Path>) watchEvent).context();
                            if (!newPath.getFileName().toString().equalsIgnoreCase(".DS_Store")) {
                                //create a tempFile based on event
                                Path child = this.dirPath.resolve(newPath);
                                String fileNameWithPath = OmniFile.extractFileName(child.getFileName().toString());
                                tempFile = omniRepository.getOmniFileByName(fileNameWithPath);
                                if (tempFile == null) {

                                    tempFile = new OmniFile(omniRepository.getFilesDirectory() + fileNameWithPath);
                                    //add file to fileList of repository
                                    omniRepository.getFileList().add(tempFile);
                                    cmdTemp.getArgsList().clear();
                                    cmdTemp.getArgsList().add(Constants.OP_DOWNLOAD);
                                    cmdTemp.getArgsList().add(Constants.OP_S_FINISHED);
                                    cmdTemp.getArgsList().add(tempFile);
                                    cmdTemp.getArgsList().add(userWatcher);
                                    cmdTemp.getArgsList().add(true);
                                    cmdTemp.getArgsList().add(omniRepository);

                                    //Send a notification request to Server
                                    omniRepository.sendUDPMessage(omniRepository.getSocketUDP(), omniRepository.getServerAddr(), omniRepository.getServerPort(), cmdTemp);
                                    // Output
                                    System.out.println("New path created: " + newPath);
                                }
                            }

                        } else if (ENTRY_MODIFY == kind) {
                            Path newPath = ((WatchEvent<Path>) watchEvent).context();
                            if (!newPath.getFileName().toString().equalsIgnoreCase(".DS_Store")) {
                                //First Delete File
                                Path child = this.dirPath.resolve(newPath);
                                String fileNameWithPath = OmniFile.extractFileName(child.getFileName().toString());
                                tempFile = omniRepository.getOmniFileByName(fileNameWithPath);
                                if (tempFile != null) {
                                    OmniFile f = new OmniFile(tempFile.getDirectory() + fileNameWithPath);
                                    if (f.getFileName().equals(tempFile.getFileName())) // LastModified date is the same ? Then break
                                        break;
                                    omniRepository.getFileList().remove(tempFile);
                                    cmdTemp.getArgsList().clear();
                                    cmdTemp.getArgsList().add(Constants.OP_DELETE);
                                    cmdTemp.getArgsList().add(Constants.OP_S_FINISHED);
                                    cmdTemp.getArgsList().add(tempFile);
                                    cmdTemp.getArgsList().add(userWatcher);
                                    cmdTemp.getArgsList().add(true);
                                    cmdTemp.getArgsList().add(omniRepository);

                                    //Send a notification request to Server
                                    omniRepository.sendUDPMessage(omniRepository.getSocketUDP(), omniRepository.getServerAddr(), omniRepository.getServerPort(), cmdTemp);

                                    //New OmniFile
                                    //create a tempFile based on event
                                    fileNameWithPath = OmniFile.extractFileName(child.getFileName().toString());
                                    tempFile = new OmniFile(omniRepository.getFilesDirectory() + fileNameWithPath);
                                    //add file to fileList of repository
                                    omniRepository.getFileList().add(tempFile);
                                    cmdTemp.getArgsList().clear();
                                    cmdTemp.getArgsList().add(Constants.OP_DOWNLOAD);
                                    cmdTemp.getArgsList().add(Constants.OP_S_FINISHED);
                                    cmdTemp.getArgsList().add(tempFile);
                                    cmdTemp.getArgsList().add(userWatcher);
                                    cmdTemp.getArgsList().add(true);
                                    cmdTemp.getArgsList().add(omniRepository);

                                    //Send a notification request to Server
                                    omniRepository.sendUDPMessage(omniRepository.getSocketUDP(), omniRepository.getServerAddr(), omniRepository.getServerPort(), cmdTemp);
                                    // Output
                                    System.out.println("File Modified: " + newPath);
                                }
                            }

                            // <U>ALERT</U>: When it reaches here, file has already been deleted from file,
                            // so it adulterates the references that are saved on omniRepository.fileList.
                            // (Java works only with references..)
                        } else if (ENTRY_DELETE == kind) {
                            Path newPath = ((WatchEvent<Path>) watchEvent).context();
                            if (!newPath.getFileName().toString().equalsIgnoreCase(".DS_Store")) {
                                Path child = this.dirPath.resolve(newPath);
                                String fileNameWithPath = OmniFile.extractFileName(child.getFileName().toString());
                                tempFile = omniRepository.getOmniFileByName(fileNameWithPath);
                                if (tempFile != null) {
                                    //omniRepository.getFileList().remove(tempFile);
                                    if (!omniRepository.customRemoveFromFileList(tempFile))
                                        break;
                                    cmdTemp.getArgsList().clear();
                                    cmdTemp.getArgsList().add(Constants.OP_DELETE);
                                    cmdTemp.getArgsList().add(Constants.OP_S_FINISHED);
                                    cmdTemp.getArgsList().add(tempFile);
                                    //cmdTemp.getArgsList().add(userWatcher);
                                    cmdTemp.getArgsList().add(null);
                                    cmdTemp.getArgsList().add(true);
                                    cmdTemp.getArgsList().add(omniRepository);

                                    //Send a notification request to Server
                                    omniRepository.sendUDPMessage(omniRepository.getSocketUDP(), omniRepository.getServerAddr(), omniRepository.getServerPort(), cmdTemp);
                                    // Output
                                    System.out.println("File Deleted: " + newPath);
                                }
                            }

                        }
                    }
                }

                if(!key.reset()) {
                    break; //loop
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
