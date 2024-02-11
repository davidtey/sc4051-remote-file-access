#ifndef UDPCLIENT_H
#define UDPCLIENT_H
#include <stdio.h>
#include <string>
#include <iostream>
#include <winsock2.h>
#include <Ws2tcpip.h>

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
        int send(const char *msg, int msgLen);
        int recv(char *buffer);
};
#endif