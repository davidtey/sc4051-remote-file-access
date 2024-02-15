#ifndef CLIENT_H
#define CLIENT_H
#include "DatabaseProxy.h"
#include <iostream>

using namespace std;

/* Client class (main class)
*/
class Client{
    private:
        DatabaseProxy database;
        string filePath, insertString;
        
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