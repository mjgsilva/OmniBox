package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by omnibox on 20/12/14.
 */
public interface ServerStatusBoard extends Remote {
    public void serverNotification(String notification) throws RemoteException;
}
