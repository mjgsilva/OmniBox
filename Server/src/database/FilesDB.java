package database;

import shared.OmniFile;
import shared.OmniRepository;
import shared.User;

import java.util.*;

/**
 * Created by OmniBox on 08/11/14.
 */
public class FilesDB {
    private HashSet<OmniFile> files;
    private HashMap<User,OmniFile> filesBeingAccessed;

    public FilesDB() {
        files = new HashSet<OmniFile>();
        filesBeingAccessed = new HashMap<User,OmniFile>();
    }

    public synchronized void addFile(final OmniFile omniFile) {
        files.add(omniFile);

    }

    public synchronized boolean fileExists(OmniFile omniFile) {
        return files.contains(omniFile);
    }

    public synchronized void removeFile(final OmniFile omniFile) {
        files.remove(omniFile);
    }

    public synchronized ArrayList fileList() {
        ArrayList fileList = new ArrayList<OmniFile>();
        for(OmniFile omniFile : files) {
            fileList.add(omniFile);
        }
        return fileList;
    }

    public synchronized boolean isFileBeingAccessed(final OmniFile omniFile) {
        return filesBeingAccessed.containsValue(omniFile);
    }

    public synchronized void addAccessToFile(final User user, final OmniFile omniFile) {
        if(!filesBeingAccessed.containsKey(user))
            filesBeingAccessed.put(user,omniFile);
    }

    public synchronized void removeAccessToFile(final User user) {
        filesBeingAccessed.remove(user);
    }

    public void rebuildSet(final HashSet<OmniRepository> omniRepositories) {
        files.clear();
        for (OmniRepository omniRepository : omniRepositories) {
            for (OmniFile omniFile : omniRepository.getFileList()) {
                addFile(omniFile);
            }
        }
    }
}
