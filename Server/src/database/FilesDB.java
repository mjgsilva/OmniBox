package database;

import shared.OmniFile;
import shared.OmniRepository;
import shared.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Files Database.
 * Abstract representation of files database. It's composed by one HashSet of
 * OmniFile that saves files and by one HashMap that includes files being accessed
 * at the moment and by who. The choice of a HashSet instead of a List is related
 * to the functionality of HashSet in keeping unique objects, ensuring that we have
 * not repeated files in the system.
 *
 * Created by OmniBox on 08/11/14.
 */
public class FilesDB {
    private HashSet<OmniFile> files;
    private HashMap<User,OmniFile> filesBeingAccessed;

    public FilesDB() {
        files = new HashSet<OmniFile>();
        filesBeingAccessed = new HashMap<User,OmniFile>();
    }


    /**
     * Add File
     *
     * Add an OmniFile to the collection that stores all the existent files
     *
     * @param omniFile
     */
    public synchronized void addFile(final OmniFile omniFile) {
        files.add(omniFile);
    }


    /**
     * File Exists
     *
     * Checks if the file's collection contains the given OmniFile
     *
     * @param omniFile
     * @return true if the OmniFile exists, or false if don't
     */
    public synchronized boolean fileExists(OmniFile omniFile) {
        return files.contains(omniFile);
    }


    /**
     * Remove File With No Source
     *
     * A file should be hosted by one repository, at least. Otherwise,
     * it will be removed from the file's collection
     *
     * @param repositoriesDB
     */
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

    /**
     * File being accessed
     *
     * Checks if a file is being accessed by an user
     *
     * @param omniFile
     * @return true if the file is being accessed, false if don't
     */
    public synchronized boolean isFileBeingAccessed(final OmniFile omniFile) {
        return filesBeingAccessed.containsValue(omniFile);
    }

    /**
     * File List
     *
     * @return a list of the existent files
     */
    public synchronized ArrayList fileList() {
        ArrayList fileList = new ArrayList<OmniFile>();
        for(OmniFile omniFile : files) {
            fileList.add(omniFile);
        }
        return fileList;
    }

    /**
     * Get Files
     *
     * @return the file's collection
     */
    public synchronized HashSet<OmniFile> getFiles() {
        return files;
    }

    /**
     * Rebuid file's collection
     *
     * Given an OmniRepository, the file's collection is updated if the repository
     * contains one (or more) file that does not exist inside file's collection
     *
     * @param omniRepository
     */
    public synchronized void rebuildFileSet(final OmniRepository omniRepository) {
        for (OmniFile omniFile : omniRepository.getFileList()) {
            addFile(omniFile);
        }
    }

    /**
     * Adds access to file
     *
     * Marks a file as being accessed, and by which user
     *
     * @param user
     * @param omniFile
     */
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

    /**
     * Because delete event on watcher is catch after file is deleted from disk, it screws over
     * the references on fileList. LastMod is created specific for this situations.
     * We have to redo the fileList but not using the usual equals and hash code from OmniFile.
     *
     * <U>Note that</U> OmniFile/File lastModified saves milliseconds from epoch January first 1970
     * til the day it was modified.
     *
     * <U><H2>Important</H2></U>
     * LastMod from omniFile received, from repository, has the lastModified milliseconds from when the
     * file was deleted from the repository. So if size is equal and value of lastModified of aux
     * is inferior to the one on omniFile then we'll assume its the same file we're trying to delete.
     *
     * @param omniFile file to be excluded from file list
     * @return flag true if file was removed, false otherwise
     */
    public synchronized boolean customRemoveFile(OmniFile omniFile) {
        HashSet<OmniFile> newFileList = new HashSet<OmniFile>();
        boolean flag = false;
        for (OmniFile aux : getFiles()) {
            if (aux.getFileName().equals(OmniFile.getOriginalFileName(omniFile.getFileName())) &&
                    aux.getFileSize() == omniFile.getFileSize() &&
                    aux.getLastModified() <= omniFile.getLastMod()) {
                // File to exclude, so do nothing.
                flag = true;
            } else
                newFileList.add(aux);
        }

        setFiles(newFileList);

        return flag;
    }
}
