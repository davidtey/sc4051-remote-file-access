#ifndef REPLYHANDLER_H
#define REPLYHANDLER_H
#include <iostream>
#include "utils.h"
#include "HandlerNum.h"
#include "PrimitiveNum.h"

using namespace std;

namespace ReplyHandler{
    HandlerNum handleReply(char *b);
    int handleReadFileReply(char *b);
    int handleInsertAck(char *b);
    int handleMonitorFileAck(char *b);
    int handleMonitorUpdate(char *b);
    int handleMonitorExpire(char *b);
    int handleDeleteFromFileAck(char *b);
    int handleListDirReply(char *b);
    int handleErrorReply(char *b);
}

#endif