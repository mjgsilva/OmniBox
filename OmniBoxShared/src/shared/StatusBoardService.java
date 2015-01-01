package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Status Board Service.
 * Service interface, should be implemented on respective server.
 * Contains methods to add/remove observers to a RMI implementation.
 *
 * Created by Omnibox on 20/12/14.
 */
public interface StatusBoardService extends Remote {
    /**
     * Adds given Remote as an observer.
     *
     * @param observer Remote to be added as observer
     * @throws RemoteException
     */
    public void addObserver(ServerStatusBoard observer) throws RemoteException;

    /**
     * Removes given Remote as an observer.
     *
     * @param observer Remote to be removed as observer
     * @throws RemoteException
     */
    public void removeObserver(ServerStatusBoard observer) throws RemoteException;
}
