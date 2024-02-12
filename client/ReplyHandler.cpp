#include "ReplyHandler.h"

using namespace std;

namespace ReplyHandler{

}
int ReplyHandler::handleReply(char *b){
    char *cur = b;
    HandlerNum replyType = static_cast<HandlerNum>(utils::unmarshalInt(cur));

    switch(replyType){
        case READ_FILE_REPLY:
            ReplyHandler::handleReadFileReply(b);
            return 1;

        case INSERTION_ACK:
            ReplyHandler::handleInsertAck(b);
            return 1;

        case ERROR_REPLY:
            ReplyHandler::handleErrorReply(b);
            return 0;
    }
    return 1;
}

int ReplyHandler::handleReadFileReply(char *b){
    char *cur = b + 4;
    int fileLength = utils::unmarshalInt(cur);
    cur += 4;
    string fileContent = utils::unmarshalString(cur, fileLength);

    cout << "File Content: \n" << fileContent << endl;

    return 1;
}

int ReplyHandler::handleInsertAck(char *b){
    cout << "String has successfully been inserted!" << endl;
    return 1;
}

int ReplyHandler::handleErrorReply(char *b){
    char *cur = b + 4;
    int errorLength = utils::unmarshalInt(cur);
    cur += 4;
    string errorString = utils::unmarshalString(cur, errorLength);
    cout << "Error: " << errorString << endl;
    return 1;
}