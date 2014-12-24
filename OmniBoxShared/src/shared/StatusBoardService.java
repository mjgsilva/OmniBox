package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Omnibox on 20/12/14.
 */
public interface StatusBoardService extends Remote {
    public void addObserver(ServerStatusBoard observer) throws RemoteException;
    public void removeObserver(ServerStatusBoard observer) throws RemoteException;
}
