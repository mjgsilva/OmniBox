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
    // Saves last modified, real date, this reference does not change even if file is
    // write on disk. Because OmniFile.lastMod != File.lastModified
    private long lastMod;

    /**
     * OmniFile constructor.
     * All variables are initiated here.
     * Notice that lastMod is filled with File.lastModified()
     *
     * @param pathname File path. Super (File) is called with this path.
     */
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
        lastMod = lastModified();
    }

    /**
     * @return file name without path.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return directory where file is saved.
     */
    public String getDirectory(){
        String absolutePath = this.getAbsolutePath();
        String filePath = absolutePath;
        filePath = filePath.substring(0,absolutePath.lastIndexOf(File.separator));

        return filePath + File.separator;
    }

    /**
     * @return last modification date
     */
    public long getLastMod() {
        return lastMod;
    }

    /**
     * Returns file name of given path.
     *
     * @param path This path should include file name.
     * @return File name as a String.
     */
    public static String extractFileName(String path){
        String fileNameStr = path;
        return fileNameStr.substring(path.lastIndexOf(File.separator)+1,path.length()-path.lastIndexOf(File.separator)-1);
    }

    /**
     * Returns Date object with creation date of file. If file is not found on disk
     * it returns begin of epoch.
     * This creation date being used is las modification for compability reasons with
     * all operation systems.
     *
     * @return Creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Returns file extension.
     *
     * @return file extension
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Returns file size.
     *
     * @return file size
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Returns last modified as long.
     * This function calls File.lastModified().
     *
     * @return last modified date as long number
     */
    public long getLastModified() {
        return lastModified();
    }

    /**
     * Equals only takes into account the following parameters of Omnifile:
     *      - lastModified
     *      - fileName (Without $'s)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OmniFile omniFile = (OmniFile) o;

        String realFileName = getOriginalFileName(fileName);
        if (realFileName != null ? !realFileName.equals(getOriginalFileName(omniFile.fileName)) : getOriginalFileName(omniFile.fileName) != null)
            return false;

        return true;
    }

    /**
     * HasCode only takes in account lastModified and fileName without dollar signs.
     */
    @Override
    public int hashCode() {
        int result=0;
        String realFileName = getOriginalFileName(fileName);
        result = 31 * result + realFileName.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return getFileName();
    }

    /**
     * Returns original file name, without the $ signs that might have been added for duplicated file names.
     *
     * @param fileName OmniFile name
     * @return file name without any dollar signs
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