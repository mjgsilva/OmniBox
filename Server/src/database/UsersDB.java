package database;

import server.OmniServer;
import shared.Constants;
import shared.Request;
import shared.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by OmniBox on 02/11/14.
 */
public class UsersDB {
    final private String fileDB;
    private HashSet<User> users = new HashSet<User>();
    private HashMap<User,Integer> usersActivity = new HashMap<User,Integer>();
    private HashMap<User,Socket> usersSocket = new HashMap<User,Socket>();

    public UsersDB(String fileDB) {
        this.fileDB = fileDB;
    }

    public void insertUser(User user) {
        if(!users.contains(user))
            users.add(user);
    }

    /**
     * Login
     *
     * @param user
     * @return True if the credentials are corrected; False if the provided username or password or both are invalid
     */
    public synchronized boolean login(final User user) {
        boolean isValid = false;
        if(users.contains(user)) {
            for(Iterator<User> it = users.iterator(); it.hasNext();) {
                User temp = it.next();
                if (temp.getUsername().equals(user.getUsername())) {
                    if (temp.getPassword().equals(user.getPassword()))
                        addUserActivity(user);
                        isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }

    private String activityAux(int action) {
        if(action == Constants.OP_DOWNLOAD)
            return "Downloading";
        else
            if (action == Constants.OP_UPLOAD)
                return "Uploading";
            else
                if(action == Constants.OP_DELETE)
                    return "Deleting";
                else
                    if(action == Constants.INACTIVE)
                        return "Inactive";
        return "Unknown Action";
    }

    /**
     * Get Users Activity
     * @return a string with all the users activity
     */
    public synchronized String getUsersActivity() {
        StringBuilder notification = new StringBuilder();

        for(User user: usersActivity.keySet()) {
            notification.append(user.getUsername() + " -> " + activityAux(usersActivity.get(user)) + "\n");
        }

        return notification.toString();
    }

    public synchronized int getNumberOfUsers() { return users.size(); }

    public synchronized int getNumberOfLoggedUsers() { return usersSocket.size(); }

    /**
     * Add User Activity
     *
     * Inserts the first activity after the user login is completed.
     * Static activity defined - Inactive
     *
     * @param user
     */
    public synchronized void addUserActivity(final User user) {
        usersActivity.put(user, Constants.INACTIVE);
    }

    /**
     * Edit User Activity
     *
     * Updates the user's activity to the current operation
     *
     * @param user
     * @param activity
     */
    public synchronized void editUserActivity(final User user,Integer activity) {
        usersActivity.put(user,activity);
    }

    /**
     * Remove User From Activity's Collection
     *
     * @param user
     */
    public synchronized void removeUserActivity(final User user) {
        usersActivity.remove(user);
    }

    /**
     * Add socket
     *
     * When the user logs in, the socket is stored
     *
     * @param user
     * @param socket
     */
    public synchronized void addSocket(final User user, final Socket socket) {
        usersSocket.put(user,socket);
    }

    /**
     * Remove socket
     *
     * When the user logs out, the socket is removed
     *
     * @param user
     */
    public synchronized void removeSocket(final User user) {
        usersSocket.remove(user);
    }

    /**
     * Notify Users
     *
     * The server notifies all the clients, sending the file's list, when
     * occurs a change (Triggered by an upload or a delete operation)
     *
     * @param filesList
     * @param omniServer
     */
    public synchronized void notifyUsers(ArrayList filesList, OmniServer omniServer) {
        Request response = new Request(Constants.CMD.cmdRefreshList,filesList);

        for(User user : usersSocket.keySet()) {
            try {
                omniServer.sendTCPMessage(usersSocket.get(user), response);
            } catch(InterruptedException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Serialize DB
     *
     * Serialize the User DB file
     *
     * @throws IOException
     */
    public void serializeDB() throws IOException {
        FileOutputStream fos = new FileOutputStream(fileDB);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(users);
        oos.close();
        fos.close();
    }

    /**
     * Deserialize DB
     *
     * Deserialize the given file storing the users credentials
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void deserializeDB() throws ClassNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(fileDB);
        ObjectInputStream ois = new ObjectInputStream(fis);
        users = (HashSet<User>) ois.readObject();
        ois.close();
        fis.close();
    }
}