#ifndef CLIENT_H
#define CLIENT_H
#include "UDPClient.h"
#include <limits.h>
#include "ReplyHandler.h"
#include <map>
#include "FileCache.h"
#include <chrono>
#include "RequestHandler.h"
#include "DatabaseProxy.h"

using namespace std;

/* Client class (main class)
*/
class Client{
    private:
        DatabaseProxy database;
        string filePath, insertString;
        map<string, FileCache*> cache;
        
        int offset, numBytes, monitorInterval;
        int reqLength;
        int requestID = 1;

        int connectMenu();
        int mainMenu();
        int readFileMenu();
        int writeFileMenu();
        int monitorFileMenu();
        int deleteFromFileMenu();
        int listDirMenu();
        int readInt(string prompt, int min, int max);
    
    public:
        Client();
        int startProcess();
};
#endif