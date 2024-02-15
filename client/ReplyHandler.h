#ifndef REPLYHANDLER_H
#define REPLYHANDLER_H
#include <iostream>
#include "utils.h"
#include "HandlerNum.h"
#include <any>

using namespace std;

/**Reply handler
 * Unmarshals replies from server and return objects
*/
namespace ReplyHandler{
    tuple<HandlerNum, any> handleReply(char *b);
    tuple<string, long long int> handleReadFileReply(char *b);
    int handleInsertAck(char *b);
    int handleMonitorFileAck(char *b);
    int handleMonitorUpdate(char *b);
    int handleMonitorExpire(char *b);
    int handleDeleteFromFileAck(char *b);
    int handleListDirReply(char *b);
    long long int handleGetFileAttrReply(char *b);
    int handleErrorReply(char *b);
}

#endif