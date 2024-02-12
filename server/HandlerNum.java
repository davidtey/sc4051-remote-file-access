package server;

public enum HandlerNum {
    READ_FILE_REQUEST,          // 1
    WRITE_FILE_REQUEST,         // 2
    MONITOR_FILE_REQUEST,       // 3
    RESERVED_FILE_REQUEST1,     // 4
    RESERVED_FILE_REQUEST2,     // 5
    READ_FILE_REPLY,            // 6
    INSERTION_ACK,              // 7
    NETWORK_PING,               // 8
    NETWORK_ACK,                // 9
    ERROR_REPLY;              // 10

    public static HandlerNum fromInt(int x){
        switch(x){
            case 1:
                return READ_FILE_REQUEST;
            case 2:
                return WRITE_FILE_REQUEST;
            case 3:
                return MONITOR_FILE_REQUEST;
            case 4:
                return RESERVED_FILE_REQUEST1;
            case 5:
                return RESERVED_FILE_REQUEST2;
            case 6:
                return READ_FILE_REPLY;
            case 7:
                return INSERTION_ACK;
            case 8:
                return NETWORK_PING;
            case 9:
                return NETWORK_ACK;
            case 10:
                return ERROR_REPLY;
        }
        return null;
    }

    public static int toInt(HandlerNum handlerNum){
        switch(handlerNum){
            case READ_FILE_REQUEST:
                return 1;
            case WRITE_FILE_REQUEST:
                return 2;
            case MONITOR_FILE_REQUEST:
                return 3;
            case RESERVED_FILE_REQUEST1:
                return 4;
            case RESERVED_FILE_REQUEST2:
                return 5;
            case READ_FILE_REPLY:
                return 6;
            case INSERTION_ACK:
                return 7;
            case NETWORK_PING:
                return 8;
            case NETWORK_ACK:
                return 9;
            case ERROR_REPLY:
                return 10;
        }
        return 0;
    }


}