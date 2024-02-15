#ifndef DATABASEPROXY_H
#define DATABASEPROXY_H
#include <iostream>
#include <limits.h>
#include <map>
#include "UDPClient.h"
#include "FileCache.h"


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
        map<string, FileCache*> cache;
        char reqBuffer[1024];
        char replyBuffer[1024];
        long long int getFileAttr(string filePath);
        bool checkCacheExists(string filePath);
        bool checkCacheOverlap(string filePath, int offset, int numBytes);
        bool checkCacheFresh(string filePath, int offset, int numBytes);
        bool checkCacheValid(string filePath);
        void readFileFromServer(string filePath, int offset, int numBytes);
        void readFileFromCache(string filePath, int offset, int numBytes);
        void writeToCache(string filePath, int offset, string fileContent, long long int serverLastModified, int fileLength);
        void deleteFileCache(string filePath);

    public:
        int freshnessInterval;
        DatabaseProxy();
        int connectToDatabase(string serverIP, int serverPort);
        void readFromFile(string filePath, int offset, int numBytes);
        void writeToFile(string filePath, int offset, string insertString);
        void monitorFile(string filePath, int monitorInterval);
        void deleteFromFile(string filePath, int offset, int numBytes);
        void listDirectory(string filePath);
};

#endif