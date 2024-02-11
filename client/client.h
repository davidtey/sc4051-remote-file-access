#ifndef CLIENT_H
#define CLIENT_H
#include "UDPClient.h"
#include "invocation.h"
#include <limits.h>

using namespace std;

/* Client class (main class)
*/
class Client{
    private:
        UDPClient udpClient;
        Invocation invocation;
        int connectMenu();
        int invocationMenu();
        int mainMenu();
        int readInt(string prompt, int min, int max);
    
    public:
        Client();
        int startProcess();
};
#endif