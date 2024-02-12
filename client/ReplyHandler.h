#ifndef REPLYHANDLER_H
#define REPLYHANDLER_H
#include <iostream>
#include "utils.h"
#include "HandlerNum.h"
#include "PrimitiveNum.h"

using namespace std;

namespace ReplyHandler{
    int handleReply(char *b);
    int handleReadFileReply(char *b);
    int handleInsertAck(char *b);
    int handleErrorReply(char *b);
}

#endif