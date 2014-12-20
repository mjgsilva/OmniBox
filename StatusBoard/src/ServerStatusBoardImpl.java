import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by omnibox on 20/12/14.
 */
public class ServerStatusBoardImpl extends UnicastRemoteObject implements ServerStatausBoard {

    public ServerStatusBoardImpl() throws RemoteException {}

    @Override
    public void serverNotification(String notification) throws RemoteException {
        System.out.println(notification);
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Syntax: <Server IP>");
            return;
        }

        try {
            ServerStatusBoardImpl serverStatusBoard = new ServerStatusBoardImpl();
            String url = "rmi://"+args[0]+"/StatusBoardService";

            StatusBoardService statusBoardService = (StatusBoardService)Naming.lookup(url);
            statusBoardService.addObserver(serverStatusBoard);

            System.out.println("<Enter> para parar o Server Status Board");
            System.out.println();
            System.in.read();

            statusBoardService.removeObserver(serverStatusBoard);
            UnicastRemoteObject.unexportObject(serverStatusBoard, true);
        }catch(RemoteException e) {
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
