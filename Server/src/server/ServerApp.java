package server;

import database.UsersDB;

import java.io.IOException;

/**
 * Created by OmniBox on 02/11/14.
 */
public class ServerApp {
    public static void main(String[] args) {

        if(args.length != 2) {
            System.out.println("Syntax: java <port> <fileDB>");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            UsersDB usersDB = new UsersDB(args[1]);

            if(port < 1)
                throw new NumberFormatException();

            usersDB.deserializeDB();

            OmniServer omniServer = new OmniServer(port, usersDB);
            omniServer.omniServerStart();
        } catch (NumberFormatException e) {
            System.out.println("Invalid port");
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid class on deserialize");
        } catch (IOException e) {
            System.out.println("Error reading fileDB");
        }
    }
}
