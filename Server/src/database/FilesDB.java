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

    public synchronized void addFile(final OmniFile omniFile) {
        files.add(omniFile);
    }

    public synchronized boolean fileExists(OmniFile omniFile) {
        return files.contains(omniFile);
    }

    public synchronized void removeFilesWithNoSource(final RepositoriesDB repositoriesDB) {
        for(Iterator<OmniFile> it = files.iterator();it.hasNext();) {
            OmniFile omniFile = it.next();
            if(repositoriesDB.getNumberOfReplicas(omniFile) == 0)
                it.remove();
        }
    }

    public synchronized int getNumberOfFiles() { return files.size(); }

    public synchronized int getNumberOfFilesBeingAccessed() { return filesBeingAccessed.size(); }

    public synchronized boolean removeFile(final OmniFile omniFile) {
        return files.remove(omniFile);
    }

    public synchronized boolean isFileBeingAccessed(final OmniFile omniFile) {
        return filesBeingAccessed.containsValue(omniFile);
    }

    public synchronized ArrayList fileList() {
        ArrayList fileList = new ArrayList<OmniFile>();
        for(OmniFile omniFile : files) {
            fileList.add(omniFile);
        }
        return fileList;
    }

    public synchronized HashSet<OmniFile> getFiles() {
        return files;
    }

    public synchronized void rebuildFileList(final OmniRepository omniRepository) {
        for (OmniFile omniFile : omniRepository.getFileList()) {
            addFile(omniFile);
        }
    }

    public synchronized void addAccessToFile(final User user, final OmniFile omniFile) {
        if(!filesBeingAccessed.containsKey(user))
            filesBeingAccessed.put(user,omniFile);
    }

    public synchronized void removeAccessToFile(final User user) {
        filesBeingAccessed.remove(user);
    }

    public synchronized void setFiles(HashSet<OmniFile> files) {
        this.files = files;
    }
}
