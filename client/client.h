#ifndef CLIENT_H
#define CLIENT_H
#include "UDPClient.h"
#include "invocation.h"
#include <limits.h>
#include "ReplyHandler.h"

using namespace std;

/* Client class (main class)
*/
class Client{
    private:
        UDPClient udpClient;
        Invocation invocation;
        string filePath, insertString;
        int offset, numBytes, monitorInterval;
        int reqLength;
        char reqBuffer[1024];
        char replyBuffer[1024];
        int requestID = 1;

        int connectMenu();
        int invocationMenu();
        int mainMenu();
        int readFileMenu();
        int writeFileMenu();
        int monitorFileMenu();
        int readInt(string prompt, int min, int max);
    
    public:
        Client();
        int startProcess();
};
#endif