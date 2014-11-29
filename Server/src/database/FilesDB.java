package database;

import shared.OmniFile;
import shared.OmniRepository;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by OmniBox on 08/11/14.
 */
public class FilesDB {
    private HashSet<OmniFile> files;

    public FilesDB() {
        files = new HashSet<OmniFile>();
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

    public void rebuildSet(final HashSet<OmniRepository> omniRepositories) {
        files.clear();
        for (OmniRepository omniRepository : omniRepositories) {
            for (OmniFile omniFile : omniRepository.getFileList()) {
                addFile(omniFile);
            }
        }
    }
}
