package shared;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * OmniFile class.
 *
 * An OmniFile is unique as long as its name and modification date are different.
 * Dollar signs ($) should be added to the file name to differentiate files with the same name,
 * but different modification dates.
 *
 * In the case of the OmniBox application, only the repository saves the files with the dollar
 * signs, server and client don't know about this differentiation on the names.
 *
 * Note that: HashCode and Equals use only name without $'s and modification date. Creation date
 * is not used for OS compatibility reasons.
 *
 * Created by ©OmniBox on 02-11-2014.
 */
public class OmniFile extends File implements Serializable {
    private final String fileName;
    private final String fileDirectory;
    private final Date creationDate;
    private final String fileExtension;
    private final long fileSize;
    //private long lastModified;

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
        //lastModified = lastModified();
    }

    public String getFileName() {
        return fileName;
    }

    public String getDirectory(){
        String absolutePath = this.getAbsolutePath();
        String filePath = absolutePath;
        filePath = filePath.substring(0,absolutePath.lastIndexOf(File.separator));

        return filePath + File.separator;
    }

    public static String extractFileName(String path){
        String fileNameStr = path;
        return fileNameStr.substring(path.lastIndexOf(File.separator)+1,path.length()-path.lastIndexOf(File.separator)-1);
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

    public long getLastModified() {
        return lastModified();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OmniFile omniFile = (OmniFile) o;

        //if (fileSize != omniFile.fileSize) return false;
        //if (creationDate != null ? !creationDate.equals(omniFile.creationDate) : omniFile.creationDate != null)
        //    return false;
        if (lastModified() != omniFile.getLastModified()) return false;
        //if (fileExtension != null ? !fileExtension.equals(omniFile.fileExtension) : omniFile.fileExtension != null)
        //    return false;

        //if (fileName != null ? !fileName.equals(omniFile.fileName) : omniFile.fileName != null) return false;

        String realFileName = getOriginalFileName(fileName);
        if (realFileName != null ? !realFileName.equals(getOriginalFileName(omniFile.fileName)) : getOriginalFileName(omniFile.fileName) != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result=0;
        String realFileName = getOriginalFileName(fileName);

        result = 31 * result + realFileName.hashCode();
        //result = 31 * result + fileName.hashCode();

        //result = 31 * result + creationDate.hashCode();
        //result = 31 * result + fileExtension.hashCode();
        //result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        result = 31 * result + (int) (lastModified() ^ (lastModified() >>> 32));

        return result;
    }

    @Override
    public String toString() {
        return getFileName();
    }

    /**
     * Returns original file name, without the $ signs that might have been added for duplicated file names.
     *
     * @param fileName
     * @return
     */
    public static String getOriginalFileName(String fileName) {
        char [] fileNameChars = fileName.toCharArray();
        int cutIndex = 0;

        for (int i = fileNameChars.length - 1; i >= 0; i--) {
            if (fileNameChars[i] != '$') {
                cutIndex = i;
                break;
            }
        }

        return fileName.substring(0, cutIndex+1);
    }
}
