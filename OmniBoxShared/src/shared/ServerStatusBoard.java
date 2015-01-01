package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Server Status Board.
 * Represents the interface for a RMI implementation.
 * Should be implemented by "clients".
 *
 * Created by Omnibox on 20/12/14.
 */
public interface ServerStatusBoard extends Remote {
    /**
     * Notification shown to clients.
     *
     * @param notification Message that is displayed
     * @throws RemoteException
     */
    public void serverNotification(String notification) throws RemoteException;
}
