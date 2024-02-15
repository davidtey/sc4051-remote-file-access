#include "ReplyHandler.h"

using namespace std;

/**Handle reply
 * Reads first 4 bytes of reply to find reply type, then calls the appropriate reply handler to unmarshal reply
 * 
 * Params:
 * *b: char array of reply
 * Returns tuple of HandlerNum and any, with any being specific to the type of reply specified by HandlerNum
*/
tuple<HandlerNum, any> ReplyHandler::handleReply(char *b){
    char *cur = b;
    HandlerNum replyType = static_cast<HandlerNum>(utils::unmarshalInt(cur));

    switch(replyType){
        case READ_FILE_REPLY:
            return make_tuple(replyType, ReplyHandler::handleReadFileReply(b));

        case INSERTION_ACK:
            return make_tuple(replyType, ReplyHandler::handleInsertAck(b));

        case MONITOR_FILE_ACK:
            return make_tuple(replyType, ReplyHandler::handleMonitorFileAck(b));

        case MONITOR_FILE_UPDATE:
            return make_tuple(replyType, ReplyHandler::handleMonitorUpdate(b));

        case MONITOR_FILE_EXPIRE:
            return make_tuple(replyType, ReplyHandler::handleMonitorExpire(b));
        
        case DELETE_FROM_FILE_ACK:
            return make_tuple(replyType, ReplyHandler::handleDeleteFromFileAck(b));
        
        case LIST_DIR_REPLY:
            return make_tuple(replyType, ReplyHandler::handleListDirReply(b));

        case GET_FILE_ATTR_REPLY:
            return make_tuple(replyType, ReplyHandler::handleGetFileAttrReply(b));

        case ERROR_REPLY:
            return make_tuple(replyType, ReplyHandler::handleErrorReply(b));
    }
    return make_tuple(UNKNOWN_REPLY, 0);
}

/**Handle read file reply
 * 
 * Params
 * *b: char array of reply
 * Returns tuple:
 * string: file content
 * long long int: server last modified time
*/
tuple<string, long long int, int> ReplyHandler::handleReadFileReply(char *b){
    char *cur = b + 4;
    long long int serverLastModified = utils::unmarshalLong(cur);
    cur += 8;
    int fileLength = utils::unmarshalInt(cur);
    cur += 4;
    int fileContentLength = utils::unmarshalInt(cur);
    cur += 4;

    string fileContent = utils::unmarshalString(cur, fileContentLength);
    
    return make_tuple(fileContent, serverLastModified, fileLength);
}

/**Handles insert ACK
 * 
 * Params
 * *b: char array of reply
 * Returns 1
*/
int ReplyHandler::handleInsertAck(char *b){
    cout << "String has successfully been inserted!" << endl;
    return 1;
}

/**Handles monitor file ACK
 * 
 * Params
 * *b: char array of reply
 * Returns 1
*/
int ReplyHandler::handleMonitorFileAck(char *b){
    cout << "Successfully registered!" << endl;
    return 1;
}

/**Handles monitor updates
 * 
 * Params
 * *b: char array of reply
 * Returns 1
*/
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

/**Handles monitor expiry
 * 
 * Params
 * *b: char array of reply
 * Returns 1
*/
int ReplyHandler::handleMonitorExpire(char *b){
    char *cur = b + 4;
    int filePathLength = utils::unmarshalInt(cur);
    cur += 4;
    string filePath = utils::unmarshalString(cur, filePathLength);

    cout << "Monitoring request for " << filePath << " has expired." << endl;

    return 1;
}

/**Handles delete from file ACK
 * 
 * Params
 * *b: char array of reply
 * Returns 1
*/
int ReplyHandler::handleDeleteFromFileAck(char *b){
    cout << "Successfully deleted from file!" << endl;
    return 1;
}

/**Handles list directory reply
 * 
 * Params
 * *b: char array of reply
 * Returns 1
*/
int ReplyHandler::handleListDirReply(char *b){
    char *cur = b + 4;
    int fileLength = utils::unmarshalInt(cur);
    cur += 4;
    string fileContent = utils::unmarshalString(cur, fileLength);

    cout << "Folder/File Content: \n" << fileContent << endl;

    return 1;
}

/**Handles get file attribute reply
 * 
 * Params
 * *b: char array of reply
 * Returns:
 * long long int: server last modified time
*/
long long int ReplyHandler::handleGetFileAttrReply(char *b){
    char *cur = b + 4;
    long long int serverLastModified = utils::unmarshalLong(cur);

    return serverLastModified;
}

/**Handles error reply
 * 
 * Params
 * *b: char array of reply
 * Returns error string
*/
string ReplyHandler::handleErrorReply(char *b){
    char *cur = b + 4;
    int errorLength = utils::unmarshalInt(cur);
    cur += 4;
    string errorString = utils::unmarshalString(cur, errorLength);
    
    return errorString;
}