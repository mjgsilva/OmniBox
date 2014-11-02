import omniboxshared.communication.TCP;
import omniboxshared.communication.UDP;
import omniboxshared.shared.Repository;

import java.util.ArrayList;

public class OmniServer {
    private int port;
    private Database usersDB;
    private ArrayList<Repository> repoDB;

    public OmniServer(int port, Database usersDB) {
        this.port = port;
        this.usersDB = usersDB;
        repoDB = new ArrayList<Repository>();
    }

}