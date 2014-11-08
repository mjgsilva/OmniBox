package threads;

import shared.OmniFile;
import shared.OmniRepository;

import java.io.IOException;
import java.nio.file.*;
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
        while(true) {
            try {
                WatchKey watckKey = watcher.take();

                List<WatchEvent<?>> events = watckKey.pollEvents();
                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        omniRepository.getFileList().add(new OmniFile(event.context().toString()));
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        omniRepository.getFileList().remove((OmniFile) event.context());
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        omniRepository.getFileList().add(new OmniFile(event.context().toString()));
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }

        }
    }
}
