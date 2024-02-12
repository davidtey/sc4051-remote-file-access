#ifndef HANDLER_ENUM
#define HANDLER_ENUM

enum HandlerNum {
    READ_FILE_REQUEST = 1,
    WRITE_FILE_REQUEST = 2,
    MONITOR_FILE_REQUEST = 3,
    RESERVED_FILE_REQUEST1 = 4,
    RESERVED_FILE_REQUEST2 = 5,
    READ_FILE_REPLY = 6,
    INSERTION_ACK = 7,
    NETWORK_PING = 8,
    NETWORK_ACK = 9,
    ERROR_REPLY = 10
};

#endif