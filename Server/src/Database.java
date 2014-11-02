import omniboxshared.shared.User;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by OmniBox on 02/11/14.
 */
public class Database {
    private ArrayList<User> usersDB;
    final private String fileDB;

    public Database(String fileDB) {
        usersDB = new ArrayList();
        this.fileDB = fileDB;
    }

    public void insertUser(User user) {

        usersDB.add(user);
    }

    public boolean login(final User user) {
        if(usersDB.contains(user))
            return true;
        return false;
    }

    public int getNumberOfRegisteredUsers() {
        return usersDB.size();
    }

    public void serializeDB() throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        fos = new FileOutputStream(fileDB);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(usersDB);
        if (oos != null)
            oos.close();
        if (fos != null)
            fos.close();
    }

    public void deserializeDB() throws ClassNotFoundException, IOException{
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        fis = new FileInputStream(fileDB);
        ois = new ObjectInputStream(fis);
        usersDB = (ArrayList<User>) ois.readObject();
        if (ois != null)
            ois.close();
        if (fis != null)
            fis.close();
    }
}