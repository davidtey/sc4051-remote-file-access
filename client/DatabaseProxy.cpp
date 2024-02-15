#include "DatabaseProxy.h"
#include "RequestHandler.h"
#include "ReplyHandler.h"

/**DatabaseProxy constructor
*/
DatabaseProxy::DatabaseProxy(){
    requestID = 1;
}

/**Connect to database
 * 
 * Params
 * serverIP: server IP
 * serverPort: server port
 * 
 * Returns -1 if unsuccessful
*/
int DatabaseProxy::connectToDatabase(string serverIP, int serverPort){
    return udpClient.connectServer(serverIP, serverPort);
}

/**Read from file
 * 
 * Params:
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: number of bytes to read
*/
void DatabaseProxy::readFromFile(string filePath, int offset, int numBytes){
    // check cache
    if (checkCacheHit(filePath, offset, numBytes)){
        readFileFromCache(filePath, offset, numBytes);
    }
    else{
        if (checkCacheValid(filePath)){
            readFileFromCache(filePath, offset, numBytes);
        }
        else{
            readFileFromServer(filePath, offset, numBytes);
        }
    }
}

/**Write to file
 * 
 * Params
 * filePath: file path of requested file
 * offset: byte offset in file
 * insertString: string to insert into file
*/
void DatabaseProxy::writeToFile(string filePath, int offset, string insertString){
    // write cache

    // write to server
    int reqLength = RequestHandler::createWriteRequest(requestID, filePath, offset, insertString, reqBuffer);
    udpClient.sendNReceive(reqBuffer, reqLength, replyBuffer, true);
    ReplyHandler::handleReply(replyBuffer);

    // prints reply depending on success/error
}

/**Monitor file
 * 
 * Params
 * filePath: file path of requested file
 * monitorInterval: time to monitor the file for in seconds
*/
void DatabaseProxy::monitorFile(string filePath, int monitorInterval){
    // send monitor request
    int reqLength = RequestHandler::createMonitorRequest(requestID, filePath, monitorInterval, reqBuffer);
    udpClient.sendNReceive(reqBuffer, reqLength, replyBuffer, true);
    ReplyHandler::handleReply(replyBuffer);

    
}

/**Delete from file
 * 
 * Params
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: numer of bytes to delete
*/
void DatabaseProxy::deleteFromFile(string filePath, int offset, int numBytes){

}


/**List directory
 * 
 * Params
 * filePath: file path of requested file
*/
void DatabaseProxy::listDirectory(string filePath){

}

/**Get File Attribute
 * 
 * Params
 * filePath: file path of requested file
 * 
 * Returns server last modified time, -1 if error
*/
long long int DatabaseProxy::getFileAttr(string filePath){
    int reqLength = RequestHandler::createGetFileAttrRequest(requestID, filePath, reqBuffer);
    udpClient.sendNReceive(reqBuffer, reqLength, replyBuffer, true);
    tuple<HandlerNum, any> reply = ReplyHandler::handleReply(replyBuffer);
    HandlerNum replyType = get<0>(reply);

    if (replyType == GET_FILE_ATTR_REPLY){
        long long int serverLastModified = any_cast<long long int>(get<1>(reply));
        cout << "File attribute received!" << endl;
        return serverLastModified;
    }
    else if (replyType == ERROR_REPLY){
        string errorMessage = any_cast<string>(get<1>(reply));
        cout << "Error encountered by server: " << errorMessage << endl;
        return -1;
    }

    return -1;
}

/**Check if file cache exists
 * 
 * Params
 * filePath: file path of requested file
 * Returns true if file cache exists
*/
bool DatabaseProxy::checkCacheExists(string filePath){
    FileCache *fileCache = cache[filePath];
    if (fileCache == NULL){
        return true;
    }
    return false;
}

/**Check if file cache exists, overlaps and is fresh
 * 
 * Params
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: numer of bytes to delete
 * Returns true if file cache exists, overlaps and is fresh
*/
bool DatabaseProxy::checkCacheHit(string filePath, int offset, int numBytes){
    if (!checkCacheExists) return false;

    FileCache *fileCache = cache[filePath];
    if (!fileCache->overlap(offset, numBytes)) return false;

    if (!fileCache->isFresh(freshnessInterval)) return false;

    return true;
}

/**Check if file cache is valid
 * 
 * Params
 * filePath: file path of requested file
 * Returns true if file cache is valid
*/
bool DatabaseProxy::checkCacheValid(string filePath){
    if (checkCacheExists(filePath)) return false;

    long long int serverLastModified = getFileAttr(filePath);
    if (serverLastModified < 0) return false;

    if (cache[filePath]->isValid(serverLastModified))return true;

    return false;
}

/**Read file from server
 * 
 * Params
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: numer of bytes to delete
*/
void DatabaseProxy::readFileFromServer(string filePath, int offset, int numBytes){
    // read from server
    int reqLength = RequestHandler::createReadRequest(requestID, filePath, offset, numBytes, reqBuffer);
    udpClient.sendNReceive(reqBuffer, reqLength, replyBuffer, true);
    ReplyHandler::handleReply(replyBuffer);
    
    tuple<HandlerNum, any> reply = ReplyHandler::handleReply(replyBuffer);
    HandlerNum replyType = get<0>(reply);

    // prints reply depending on success/error
    if (replyType == READ_FILE_REPLY){
        tuple<string, long long int, int> replyTuple = any_cast<tuple<string, long long int, int>>(get<1>(reply));
        string fileContent = get<0>(replyTuple);
        long long int serverLastModified = get<1>(replyTuple);
        int fileLength = get<2>(replyTuple);
        cout << "File Content: \n" << fileContent << endl;

        writeToCache(filePath, offset, fileContent, serverLastModified, fileLength);    // update cache
    }
    else if (replyType == ERROR_REPLY){
        string errorMessage = any_cast<string>(get<1>(reply));
        cout << "Error encountered by server: " << errorMessage << endl;
    }
}


/**Read file from cache
 * 
 * Params
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: numer of bytes to delete
*/
void DatabaseProxy::readFileFromCache(string filePath, int offset, int numBytes){
    string fileContent = cache[filePath]->read(offset, numBytes);
    cout << "File Content: \n" << fileContent << endl;
}

/**Write file to cache, creates new FileCache if necessary
 * 
 * Params
 * filePath: file path of requested file
 * offset: byte offset in file
 * numBytes: numer of bytes to delete
 * serverLastModified: time server last modified
 * fileLength: length of whole file
*/
void DatabaseProxy::writeToCache(string filePath, int offset, string fileContent, long long int serverLastModified, int fileLength){
    // save file content to cache
    if (checkCacheExists(filePath)){
        cache[filePath]->write(offset, fileContent, serverLastModified, fileLength);
    }
    else{
        FileCache *fileCache = new FileCache(filePath, serverLastModified, fileLength);
        cache[filePath] = fileCache;

        fileCache->write(offset, fileContent, serverLastModified, fileLength);
    }
}