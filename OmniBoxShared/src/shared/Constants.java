package shared;

import java.io.Serializable;

/**
 * Created by mario on 02/11/14.
 */
public class Constants implements Serializable {
    final public static String MULTICAST_ADDRESS = "230.30.30.30";
    final public static int MAX_SIZE = 1024;
    final public static int TIMEOUT = 5 * 1000;
    final public static int EXPIRE_TIME = 30 * 1000;
    // Shared commands ---------------------
    // ::Multicast::
    final public static String REQUEST_SERVER_IP_ADDRESS = "request_server_ip_address";

    final public static boolean FILEOK = true;
    final public static boolean FILENOTOK = false;
    final public static int OP_DOWNLOAD = 0;
    final public static int OP_UPLOAD = 1;
    final public static int OP_DELETE = 2;
    final public static int OP_REPLICATION = 3;
    final public static int OP_SEND_FILE = 4;
    final public static int OP_GET_FILE = 5;
    final public static int OP_S_STARTED = 1;
    final public static int OP_S_FINISHED = 0;
    final public static int INACTIVE = 9;

    public enum CMD {cmdNone,cmdSendFile,cmdGetFile,cmdDeleteFile,cmdRepositoryAddress,cmdAuthenticate,cmdHeartBeat,cmdNotification,cmdRefreshList};
}
