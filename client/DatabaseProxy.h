#ifndef DATABASEPROXY_H
#define DATABASEPROXY_H
#include <iostream>
#include "UDPClient.h"

using namespace std;

/**Database Proxy class
 * Acts as the database class in server with the same interface
 * Gets arguments from client, marshals request, waits for reply, unmarshals reply and
 * prints the replies to client console
*/
class DatabaseProxy{
    private:
        UDPClient udpClient;
        int requestID;
        char reqBuffer[1024];
        char replyBuffer[1024];
        void getFileAttr(string filePath);

    public:
        DatabaseProxy();
        int connectToDatabase(string serverIP, int serverPort);
        void readFromFile(string filePath, int offset, int numBytes);
        void writeToFile(string filePath, int offset, string insertString);
        void monitorFile(string filePath, int monitorInterval);
        void deleteFromFile(string filePath, int offset, int numBytes);
        void listDirectory(string filePath);
};

#endif