#include "UDPClient.h"

using namespace std;

UDPClient::UDPClient(){
    WORD wVersionRequested;
    WSADATA wsaData;
    wVersionRequested = MAKEWORD(2, 2);

    WSAStartup(wVersionRequested, &wsaData);

    if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ){
        cout << "Socket creation failed with error " << sockfd << ", WSAError: " << WSAGetLastError();;
        exit(EXIT_FAILURE);
    }
}

int UDPClient::connectServer(string ip, int port){
    serverIP = ip;
    serverPort = port;

    memset(&servaddr, 0, sizeof(servaddr)); 

    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(serverPort);
    servaddr.sin_addr.s_addr = inet_addr(serverIP.c_str());

    if (connect(sockfd, (struct sockaddr *) &servaddr, sizeof(servaddr))< 0){
        cout << "Client failed to connect to server " << serverIP << ":" << serverPort << endl;
        return -1;
    }
    return 1;
}

int UDPClient::send(const char *msg, int msgLen){
    cout << "Sending to server: " << endl;
    cout << "MsgLen = " << msgLen << endl;

    for(int i=0; i<msgLen; i++){
        if (i % 4 == 0){
            cout << " ";
        }
        cout << std::hex << (int)msg[i];
    }
    
    if (sendto(sockfd, msg, msgLen, 0, 
    (const struct sockaddr *) &servaddr, sizeof(servaddr)) < 0){
        cout << "Client failed to send message: " << msg << endl;
        return -1;
    }
    return 1;
}

int UDPClient::recv(char *buffer){
    n = recvfrom(sockfd, (char *)buffer, 1024, 
        0, (struct sockaddr *) &servaddr, &servaddrLen);
    
    if (n < 0){
        cout << "Client failed to receive message." << endl;
    }
    
    buffer[n] = '\0';
    return n;
}