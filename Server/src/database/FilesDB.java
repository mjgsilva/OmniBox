package database;

import shared.OmniFile;
import shared.OmniRepository;
import shared.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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

    public void addFile(final OmniFile omniFile) {
        files.add(omniFile);

    }

    public boolean fileExists(OmniFile omniFile) {
        return files.contains(omniFile);
    }

    public void removeFile(final OmniFile omniFile) {
        files.remove(omniFile);
    }

    public boolean isFileBeingAccessed(final OmniFile omniFile) {
        return filesBeingAccessed.containsValue(omniFile);
    }

    public synchronized ArrayList fileList() {
        ArrayList fileList = new ArrayList<OmniFile>();
        for(OmniFile omniFile : files) {
            fileList.add(omniFile);
        }
        return fileList;
    }

    public void addAccessToFile(final User user, final OmniFile omniFile) {
        if(!filesBeingAccessed.containsKey(user))
            filesBeingAccessed.put(user,omniFile);
    }

    public void removeAccessToFile(final User user) {
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
