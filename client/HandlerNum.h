#ifndef HANDLER_ENUM
#define HANDLER_ENUM

/**HandlerNum Enum
 * Enum for types of requests & replies for identification.
 */
enum HandlerNum {
    READ_FILE_REQUEST = 1,
    WRITE_FILE_REQUEST = 2,
    MONITOR_FILE_REQUEST = 3,
    DELETE_FROM_FILE_REQUEST = 4,
    LIST_DIR_REQUEST = 5,
    READ_FILE_REPLY = 6,
    INSERTION_ACK = 7,
    MONITOR_FILE_ACK = 8,
    MONITOR_FILE_UPDATE = 9,
    MONITOR_FILE_EXPIRE = 10,
    DELETE_FROM_FILE_ACK = 11,
    LIST_DIR_REPLY = 12,
    GET_FILE_ATTR_REQUEST = 13,
    GET_FILE_ATTR_REPLY = 14,
    NETWORK_PING = 15,
    NETWORK_ACK = 16,
    ERROR_REPLY = 17,
    UNKNOWN_REPLY = 18
};

#endif