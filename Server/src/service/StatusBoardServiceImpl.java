package service;

import shared.ServerStatusBoard;
import shared.StatusBoardService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omnibox on 20/12/14.
 */
public class StatusBoardServiceImpl extends UnicastRemoteObject implements StatusBoardService{

    private List<ServerStatusBoard> observers;

    public StatusBoardServiceImpl() throws RemoteException {
        observers = new ArrayList<ServerStatusBoard>();
    }

    @Override
    public synchronized void addObserver(ServerStatusBoard observer) throws RemoteException {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void removeObserver(ServerStatusBoard observer) throws RemoteException {
        observers.remove(observer);
    }

    public synchronized void notifyObservers(String notification)
    {
        int i;

        for(i=0; i < observers.size(); i++){
            try{
                observers.get(i).serverNotification(notification);
            }catch(RemoteException e){
                observers.remove(i--);
            }
        }
    }
}
