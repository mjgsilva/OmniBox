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

    public synchronized void addUserActivity(final User user) {
        usersActivity.put(user, Constants.INACTIVE);
    }

    public synchronized void editUserActivity(final User user,Integer activity) {
        usersActivity.put(user,activity);
    }

    public synchronized void remoteUserActivity(final User user) {
        usersActivity.remove(user);
    }

    public synchronized void addSocket(final User user, final Socket socket) {
        usersSocket.put(user,socket);
    }

    public synchronized void removeSocket(final User user) {
        usersSocket.remove(user);
    }

    public synchronized void notifyUsers(ArrayList filesList, OmniServer omniServer) {
        Request response = new Request(Constants.CMD.cmdRefreshList,filesList);
        for(User user : users) {
            try {
                omniServer.sendTCPMessage(usersSocket.get(user), response);
                System.out.println("User: " + user + " Socket: " + usersSocket.get(user).getInetAddress());
            } catch(InterruptedException e) {
            } catch(IOException e) { }
        }
    }

    public void serializeDB() throws IOException {
        FileOutputStream fos = new FileOutputStream(fileDB);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(users);
        oos.close();
        fos.close();
    }

    @SuppressWarnings("unchecked")
    public void deserializeDB() throws ClassNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(fileDB);
        ObjectInputStream ois = new ObjectInputStream(fis);
        users = (HashSet<User>) ois.readObject();
        ois.close();
        fis.close();
    }
}