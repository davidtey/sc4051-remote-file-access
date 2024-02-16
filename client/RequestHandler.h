#ifndef REQUESTHANDLER_H
#define REQUESTHANDLER_H
#include <iostream>

#include "utils.h"
#include "HandlerNum.h"

using namespace std;

/**Request handler
 * Used to marshal client requests
*/
namespace RequestHandler{
    int createReadRequest(int requestID, string filePath, int offset, int numBytes, char *b);
    int createWriteRequest(int requestID, string filePath, int offset, string insertString, char *b);
    int createMonitorRequest(int requestID, string filePath, int monitorInterval, char *b);
    int createDeleteFromFileRequest(int requestID, string filePath, int offset, int numBytes, char *b);
    int createListDirRequest(int requestID, string filePath, char *b);
    int createGetFileAttrRequest(int requestID, string filePath, char *b);
    int createNetworkPing(char *b);
}

#endif