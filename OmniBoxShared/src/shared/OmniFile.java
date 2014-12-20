package shared;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by ©OmniBox on 02-11-2014.
 */
public class OmniFile extends File implements Serializable {
    private final String fileName;
    private final String fileDirectory;
    private final Date creationDate;
    private final String fileExtension;
    private final long fileSize;

    public OmniFile(String pathname) {
        super(pathname);

        fileName = getName();
        fileDirectory = getDirectory();
        creationDate = new Date(lastModified());
        if (fileName.lastIndexOf(".") > 0)
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
        else
            fileExtension = "";
        fileSize = super.length();
    }

    public String getFileName() {
        return fileName;
    }

    public String getDirectory(){
        String absolutePath = this.getAbsolutePath();
        String filePath = absolutePath;
        return filePath.substring(0,absolutePath.lastIndexOf(File.separator));
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OmniFile omniFile = (OmniFile) o;

        if (fileSize != omniFile.fileSize) return false;
        //if (creationDate != null ? !creationDate.equals(omniFile.creationDate) : omniFile.creationDate != null)
        //    return false;
        if (fileExtension != null ? !fileExtension.equals(omniFile.fileExtension) : omniFile.fileExtension != null)
            return false;
        if (fileName != null ? !fileName.equals(omniFile.fileName) : omniFile.fileName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result=0;
        result = 31 * result + fileName.hashCode();
        //result = 31 * result + creationDate.hashCode();
        result = 31 * result + fileExtension.hashCode();
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
