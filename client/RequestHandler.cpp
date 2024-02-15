#include "RequestHandler.h"

using namespace std;

/* Create read request message to be sent to server
 * 
 * Params
 * requestID: request id
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: number of bytes to read
 * *b: buffer to store request message
 * Returns length of message
 * 
 * Marshal format:
 * int (4 bytes): READ_FILE_REQUEST handler number
 * int (4 bytes): request ID
 * int (4 bytes): filePath length
 * string (n bytes): filePath string
 * int (4 bytes): offset
 * int (4 bytes): numBytes
*/
int RequestHandler::createReadRequest(int requestID, string filePath, int offset, int numBytes, char *b){
    char *cur = b; // index increment
    utils::marshalInt(HandlerNum::READ_FILE_REQUEST, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalInt(requestID, cur);  // insert request ID
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(filePath.length(), cur);      // insert filePath string length into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalString(filePath, cur);            // insert filePath into request message
    cur += filePath.length();   // increment by length of filePath
    utils::marshalInt(offset, cur);                 // insert offset into request message
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(numBytes, cur);               // insert numBytes into request message
    cur += 4;    // increment by 4 bytes

    return (int) (cur - b);
}

/* Create write request message to be sent to server
 * 
 * Params
 * requestID: request id
 * filePath: file path of requested file
 * offset: byte offset in file
 * insertString: string to insert into file
 * *b: buffer to store request message
 * Returns length of message
 * 
 * Marshal format:
 * int (4 bytes): WRITE_FILE_REQUEST handler number
 * int (4 bytes): request ID
 * int (4 bytes): filePath length
 * string (n bytes): filePath string
 * int (4 bytes): offset
 * int (4 bytes): insertString length
 * string (m bytes): insertString string
*/
int RequestHandler::createWriteRequest(int requestID, string filePath, int offset, string insertString, char *b){
    char* cur = b; // char pointer
    utils::marshalInt(HandlerNum::WRITE_FILE_REQUEST, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalInt(requestID, cur);  // insert request ID
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(filePath.length(), cur);      // insert filePath string length into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalString(filePath, cur);            // insert filePath into request message
    cur += filePath.length();   // increment by length of filePath
    utils::marshalInt(offset, cur);                 // insert offset into request message
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(insertString.length(), cur);      // insert insertString length into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalString(insertString, cur);            // insert insertString into request message
    cur += insertString.length();   // increment by length of insertString

    return cur - b;
}

/* Create monitor request message to be sent to server
 * 
 * Params
 * requestID: request id
 * filePath: file path of requested file
 * monitorInterval: time to monitor the file for in seconds
 * *b: buffer to store request message
 * Returns length of message
 * 
 * Marshal format:
 * int (4 bytes): MONITOR_FILE_REQUEST handler number
 * int (4 bytes): request ID
 * int (4 bytes): filePath length
 * string (n bytes): filePath string
 * int (4 bytes): monitorInterval
*/
int RequestHandler::createMonitorRequest(int requestID, string filePath, int monitorInterval, char *b){
    char* cur = b; // char pointer
    utils::marshalInt(HandlerNum::MONITOR_FILE_REQUEST, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalInt(requestID, cur);  // insert request ID
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(filePath.length(), cur);      // insert filePath string length into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalString(filePath, cur);            // insert filePath into request message
    cur += filePath.length();   // increment by length of filePath
    utils::marshalInt(monitorInterval, cur);        // insert monitorInterval into request message
    cur += 4;    // increment by 4 bytes

    return cur - b;
}

/* Create delete from file request message to be sent to server
 * 
 * Params
 * requestID: request id
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: numer of bytes to delete
 * *b: buffer to store request message
 * Returns length of message
 * 
 * Marshal format:
 * int (4 bytes): DELETE_FROM_FILE_REQUEST handler number
 * int (4 bytes): request ID
 * int (4 bytes): filePath length
 * string (n bytes): filePath string
 * int (4 bytes): offset
 * int (4 bytes): numBytes
*/
int RequestHandler::createDeleteFromFileRequest(int requestID, string filePath, int offset, int numBytes, char *b){
    char *cur = b; // index increment
    utils::marshalInt(HandlerNum::DELETE_FROM_FILE_REQUEST, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalInt(requestID, cur);              // insert request ID
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(filePath.length(), cur);      // insert filePath length into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalString(filePath, cur);            // insert filePath into request message
    cur += filePath.length();   // increment by length of filePath
    utils::marshalInt(offset, cur);                 // insert offset into request message
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(numBytes, cur);               // insert numBytes into request message
    cur += 4;    // increment by 4 bytes

    return (int) (cur - b);
}

/* Create list directory request message to be sent to server
 * 
 * Params
 * requestID: request id
 * filePath: file path of requested file/folder
 * *b: buffer to store request message
 * Returns length of message
 * 
 * Marshal format:
 * int (4 bytes): LIST_DIR_REQUEST handler number
 * int (4 bytes): request ID
 * int (4 bytes): filePath length
 * string (n bytes): filePath string
*/
int RequestHandler::createListDirRequest(int requestID, string filePath, char *b){
    char *cur = b; // index increment
    utils::marshalInt(HandlerNum::LIST_DIR_REQUEST, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalInt(requestID, cur);              // insert request ID
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(filePath.length(), cur);      // insert filePath length into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalString(filePath, cur);            // insert filePath into request message
    cur += filePath.length();   // increment by length of filePath

    return (int) (cur - b);
}

/* Create get file attribute request message to be sent to server
 * 
 * Params
 * requestID: request id
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: numer of bytes to read
 * *b: buffer to store request message
 * Returns length of message
 * 
 * Marshal format:
 * int (4 bytes): GET_FILE_ATTR_REQUEST handler number
 * int (4 bytes): request ID
 * int (4 bytes): filePath length
 * string (n bytes): filePath string
*/
int RequestHandler::createGetFileAttrRequest(int requestID, string filePath, char *b){
    char *cur = b; // index increment
    utils::marshalInt(HandlerNum::GET_FILE_ATTR_REQUEST, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalInt(requestID, cur);              // insert request ID
    cur += 4;    // increment by 4 bytes
    utils::marshalInt(filePath.length(), cur);      // insert filePath length into request message
    cur += 4;   // increment by 4 bytes
    utils::marshalString(filePath, cur);            // insert filePath into request message
    cur += filePath.length();   // increment by length of filePath

    return (int) (cur - b);
}