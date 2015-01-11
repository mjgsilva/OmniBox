package shared;

import java.io.Serializable;

/**
 * Constants class.
 * This class has every constant values that are shared by OmniBox project modules.
 *
 * Created by Omnibox on 02/11/14.
 */
public class Constants implements Serializable {
    public static final String MULTICAST_ADDRESS = "230.30.30.30";
    public static final int MULTICAST_PORT = 7000;
    public static final int MAX_SIZE = 1024*2;
    public static final int TIMEOUT = 5 * 1000;
    public static final int EXPIRE_TIME = 30 * 1000;
    public static final String REQUEST_SERVER_IP_ADDRESS = "request_server_ip_address";

    public static final boolean FILEOK = true;
    public static final boolean FILENOTOK = false;
    public static final int OP_DOWNLOAD = 0;
    public static final int OP_UPLOAD = 1;
    public static final int OP_DELETE = 2;
    public static final int OP_REPLICATION = 3;
    public static final int OP_S_STARTED = 1;
    public static final int OP_S_FINISHED = 0;
    public static final int INACTIVE = 9;

    /**
     * Commands enumeration list.
     */
    public enum CMD {cmdSendFile,cmdGetFile,cmdDeleteFile,cmdRepositoryAddress,cmdAuthentication,cmdHeartBeat,cmdNotification,cmdRefreshList};
}
