package server;

public enum HandlerNum {
    READ_FILE_HANDLER,
    WRITE_FILE_HANDLER,
    MONITOR_FILE_HANDLER,
    RESERVED_FILE_HANDLER1,
    RESERVED_FILE_HANDLER2,
    ERROR_HANDLER,
    INSERTION_ACK,
    NETWORK_PING,
    NETWORK_ACK;

    public static HandlerNum fromInt(int x){
        switch(x){
            case 1:
                return READ_FILE_HANDLER;
            case 2:
                return WRITE_FILE_HANDLER;
            case 3:
                return MONITOR_FILE_HANDLER;
            case 4:
                return RESERVED_FILE_HANDLER1;
            case 5:
                return RESERVED_FILE_HANDLER2;
            case 6:
                return ERROR_HANDLER;
            case 7:
                return INSERTION_ACK;
            case 8:
                return NETWORK_PING;
            case 9:
                return NETWORK_ACK;
        }
        return null;
    }
}