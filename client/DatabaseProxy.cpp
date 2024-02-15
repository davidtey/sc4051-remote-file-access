#include "DatabaseProxy.h"
#include "RequestHandler.h"
#include "ReplyHandler.h"


DatabaseProxy::DatabaseProxy(){
    requestID = 1;
}

int DatabaseProxy::connectToDatabase(string serverIP, int serverPort){
    return udpClient.connectServer(serverIP, serverPort);
}

void DatabaseProxy::readFromFile(string filePath, int offset, int numBytes){
    // check cache


    // read from server
    int reqLength = RequestHandler::createReadRequest(requestID, filePath, offset, numBytes, reqBuffer);
    udpClient.sendNReceive(reqBuffer, reqLength, replyBuffer);
    ReplyHandler::handleReply(replyBuffer);

    // prints reply depending on success/error

}

void DatabaseProxy::writeToFile(string filePath, int offset, string insertString){
    // write cache

    // write to server
    int reqLength = RequestHandler::createWriteRequest(requestID, filePath, offset, insertString, reqBuffer);
    udpClient.sendNReceive(reqBuffer, reqLength, replyBuffer);
    ReplyHandler::handleReply(replyBuffer);

    // prints reply depending on success/error
}
void DatabaseProxy::monitorFile(string filePath, int monitorInterval){
    // send monitor request
    int reqLength = RequestHandler::createMonitorRequest(requestID, filePath, monitorInterval, reqBuffer);
    udpClient.sendNReceive(reqBuffer, reqLength, replyBuffer);
    ReplyHandler::handleReply(replyBuffer);

    
}

void DatabaseProxy::deleteFromFile(string filePath, int offset, int numBytes){

}

void DatabaseProxy::listDirectory(string filePath){

}