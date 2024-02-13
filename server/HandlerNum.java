package server;

/**HandlerNum Enum
 * <pre>
 * Enum for types of requests & replies for identification.
 * </pre>
 */
public enum HandlerNum {
    READ_FILE_REQUEST,          // 1
    WRITE_FILE_REQUEST,         // 2
    MONITOR_FILE_REQUEST,       // 3
    DELETE_FROM_FILE_REQUEST,   // 4
    LIST_DIR_REQUEST,           // 5
    READ_FILE_REPLY,            // 6
    INSERTION_ACK,              // 7
    MONITOR_FILE_ACK,           // 8
    MONITOR_FILE_UPDATE,        // 9
    MONITOR_FILE_EXPIRE,        // 10
    DELETE_FROM_FILE_ACK,       // 11
    LIST_DIR_REPLY,             // 12
    GET_FILE_ATTR_REQUEST,      // 13
    GET_FILE_ATTR_REPLY,        // 14
    NETWORK_PING,               // 15
    NETWORK_ACK,                // 16
    ERROR_REPLY;                // 17

    /**Converts int to HandlerNum
     * @param x integer
     * @return HandlerNum Enum
     */
    public static HandlerNum fromInt(int x){
        switch(x){
            case 1:
                return READ_FILE_REQUEST;
            case 2:
                return WRITE_FILE_REQUEST;
            case 3:
                return MONITOR_FILE_REQUEST;
            case 4:
                return DELETE_FROM_FILE_REQUEST;
            case 5:
                return LIST_DIR_REQUEST;
            case 6:
                return READ_FILE_REPLY;
            case 7:
                return INSERTION_ACK;
            case 8:
                return MONITOR_FILE_ACK;
            case 9:
                return MONITOR_FILE_UPDATE;
            case 10:
                return MONITOR_FILE_EXPIRE;
            case 11:
                return DELETE_FROM_FILE_ACK;
            case 12:
                return LIST_DIR_REPLY;
            case 13:
                return GET_FILE_ATTR_REQUEST;
            case 14:
                return GET_FILE_ATTR_REPLY;
            case 15:
                return NETWORK_PING;
            case 16:
                return NETWORK_ACK;
            case 17:
                return ERROR_REPLY;
        }
        return null;
    }

    /**Converts HandlerNum to int
     * @param handlerNum HandlerNum Enum
     * @return integer associated with the HandlerNum
     */
    public static int toInt(HandlerNum handlerNum){
        switch(handlerNum){
            case READ_FILE_REQUEST:
                return 1;
            case WRITE_FILE_REQUEST:
                return 2;
            case MONITOR_FILE_REQUEST:
                return 3;
            case DELETE_FROM_FILE_REQUEST:
                return 4;
            case LIST_DIR_REQUEST:
                return 5;
            case READ_FILE_REPLY:
                return 6;
            case INSERTION_ACK:
                return 7;
            case MONITOR_FILE_ACK:
                return 8;
            case MONITOR_FILE_UPDATE:
                return 9;
            case MONITOR_FILE_EXPIRE:
                return 10;
            case DELETE_FROM_FILE_ACK:
                return 11;
            case LIST_DIR_REPLY:
                return 12;
            case GET_FILE_ATTR_REQUEST:
                return 13;
            case GET_FILE_ATTR_REPLY:
                return 14;
            case NETWORK_PING:
                return 15;
            case NETWORK_ACK:
                return 16;
            case ERROR_REPLY:
                return 17;
        }
        return 0;
    }
}