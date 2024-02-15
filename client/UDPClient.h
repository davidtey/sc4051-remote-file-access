#ifndef UDPCLIENT_H
#define UDPCLIENT_H
#include <stdio.h>
#include <string>
#include <iostream>
#include <winsock2.h>
#include <Ws2tcpip.h>
#include <iomanip>
#include <time.h>

using namespace std;

class UDPClient{
    private:
        string serverIP;
        int serverPort;
        int sockfd;
        int n;
        int timeout;
        struct sockaddr_in servaddr;
        socklen_t servaddrLen = sizeof(servaddr);
        char buffer[1024];

    public:
        UDPClient();
        int connectServer(string ip, int port);
        int sendNReceive(const char *msg, int msgLen, char *replyBuffer, bool timeout);
        int sendRequest(const char *msg, int msgLen);
        int recvReply(char *buffer, bool timeout);
};
#endif