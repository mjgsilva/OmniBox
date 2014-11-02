import shared.User;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by OmniBox on 02/11/14.
 */
public class Database {
    private ArrayList<User> usersDB = new ArrayList<User>();
    final private String fileDB;

    public Database(String fileDB) {
        this.fileDB = fileDB;
    }

    public void insertUser(User user) {

        usersDB.add(user);
    }

    public boolean login(final User user) {
        return usersDB.contains(user);
    }

    public int getNumberOfRegisteredUsers() {
        return usersDB.size();
    }

    public void serializeDB() throws IOException {
        FileOutputStream fos = new FileOutputStream(fileDB);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(usersDB);
        oos.close();
        fos.close();
    }

    @SuppressWarnings("unchecked")
    public void deserializeDB() throws ClassNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(fileDB);
        ObjectInputStream ois = new ObjectInputStream(fis);
        usersDB = (ArrayList<User>) ois.readObject();
        ois.close();
        fis.close();
    }
}