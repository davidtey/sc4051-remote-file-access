#include "RequestHandler.h"

using namespace std;

/* Create read request message to be sent to server
 * Returns message by reference through buffer
 * Assumes buffer is big enough to contain message, else segmentation fault (handle buffer size in client)
 * Returns length of message
*/
int RequestHandler::createReadRequest(string filePath, int offset, int numBytes, char *b){
    char *cur = b; // index increment

    utils::marshalInt(HandlerNum::READ_FILE_HANDLER, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(3, cur);  // insert number of fields = 3 into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_STRING, cur);   // insert first argument (string) primitive number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_INT, cur);     // insert second argument (int) primitive number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_INT, cur);     // insert third argument (int) primitive number into request message
    cur += 4;   // increment by 4 bytes

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
 * Returns message by reference through buffer
 * Assumes buffer is big enough to contain message, else segmentation fault (handle buffer size in client)
 * Returns length of message
*/
int RequestHandler::createWriteRequest(string filePath, int offset, string insertString, char *b){
    char* cur = b; // char pointer

    utils::marshalInt(HandlerNum::WRITE_FILE_HANDLER, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(3, cur);  // insert number of fields = 3 into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_STRING, cur);   // insert first argument (string) primitive number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_INT, cur);     // insert second argument (int) primitive number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_STRING, cur);     // insert third argument (string) primitive number into request message
    cur += 4;   // increment by 4 bytes

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
 * Returns message by reference through buffer
 * Assumes buffer is big enough to contain message, else segmentation fault (handle buffer size in client)
 * Returns length of message
*/
int RequestHandler::createMonitorRequest(string filePath, int monitorInterval, char *b){
    char* cur = b; // char pointer

    utils::marshalInt(HandlerNum::MONITOR_FILE_HANDLER, cur); // insert handler number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(2, cur);  // insert number of fields = 2 into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_STRING, cur);   // insert first argument (string) primitive number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(PrimitiveNum::ENUM_INT, cur);     // insert second argument (int) primitive number into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalInt(filePath.length(), cur);      // insert filePath string length into request message
    cur += 4;   // increment by 4 bytes

    utils::marshalString(filePath, cur);            // insert filePath into request message
    cur += filePath.length();   // increment by length of filePath

    utils::marshalInt(monitorInterval, cur);        // insert monitorInterval into request message
    cur += 4;    // increment by 4 bytes

    return cur - b;
}