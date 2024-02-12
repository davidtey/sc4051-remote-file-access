#include "ReplyHandler.h"

using namespace std;

namespace ReplyHandler{

}
HandlerNum ReplyHandler::handleReply(char *b){
    char *cur = b;
    HandlerNum replyType = static_cast<HandlerNum>(utils::unmarshalInt(cur));

    switch(replyType){
        case READ_FILE_REPLY:
            ReplyHandler::handleReadFileReply(b);
            return replyType;

        case INSERTION_ACK:
            ReplyHandler::handleInsertAck(b);
            return replyType;

        case MONITOR_FILE_ACK:
            ReplyHandler::handleMonitorFileAck(b);
            return replyType;

        case MONITOR_FILE_UPDATE:
            ReplyHandler::handleMonitorUpdate(b);
            return replyType;

        case MONITOR_FILE_EXPIRE:
            ReplyHandler::handleMonitorExpire(b);
            return replyType;
        
        case DELETE_FROM_FILE_ACK:
            ReplyHandler::handleDeleteFromFileAck(b);
            return replyType;
        
        case LIST_DIR_REPLY:
            ReplyHandler::handleListDirReply(b);
            return replyType;

        case ERROR_REPLY:
            ReplyHandler::handleErrorReply(b);
            return replyType;
    }
    return UNKNOWN_REPLY;
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

int ReplyHandler::handleMonitorFileAck(char *b){
    cout << "Successfully registered!" << endl;
    return 1;
}

int ReplyHandler::handleMonitorUpdate(char *b){
    char *cur = b + 4;
    int filePathLength = utils::unmarshalInt(cur);
    cur += 4;
    string filePath = utils::unmarshalString(cur, filePathLength);
    cur += filePathLength;

    int fileLength = utils::unmarshalInt(cur);
    cur += 4;
    string fileContent = utils::unmarshalString(cur, fileLength);

    cout << filePath << " has been updated. " << endl;
    cout << "New file content: " << endl;
    cout << fileContent << endl;

    return 1;
}

int ReplyHandler::handleMonitorExpire(char *b){
    char *cur = b + 4;
    int filePathLength = utils::unmarshalInt(cur);
    cur += 4;
    string filePath = utils::unmarshalString(cur, filePathLength);

    cout << "Monitoring request for " << filePath << " has expired." << endl;

    return 1;
}

int ReplyHandler::handleDeleteFromFileAck(char *b){
    cout << "Successfully deleted from file!" << endl;
    return 1;
}

int ReplyHandler::handleListDirReply(char *b){
    char *cur = b + 4;
    int fileLength = utils::unmarshalInt(cur);
    cur += 4;
    string fileContent = utils::unmarshalString(cur, fileLength);

    cout << "Folder/File Content: \n" << fileContent << endl;

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