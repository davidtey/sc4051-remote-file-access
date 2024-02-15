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

int UDPClient::sendNReceive(const char *msg, int msgLen, char *replyBuffer, bool useTimeout){
    sendRequest(msg, msgLen);

    while (recvReply(replyBuffer, useTimeout) == -1){
        sendRequest(msg, msgLen);
    }
    return 1;
}


int UDPClient::sendRequest(const char *msg, int msgLen){
    // debug print
    cout << "Sending to server: ";

    for(int i=0; i<msgLen; i++){
        if (i % 4 == 0){
            cout << " ";
        }
        cout << hex << setfill('0') << setw(2) << (int)msg[i];
    }
    cout << endl;

    
    if (sendto(sockfd, msg, msgLen, 0, 
    (const struct sockaddr *) &servaddr, sizeof(servaddr)) < 0){
        cout << "Client failed to send message: " << msg << endl;
        return -1;
    }
    return 1;
}

int UDPClient::recvReply(char *buffer, bool useTimeout){
    struct timeval timeout;
    if (useTimeout){
        timeout.tv_sec = 2;
    }
    else{
        timeout.tv_sec = 0;
    }
    timeout.tv_usec = 0;
    setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (const char *) &timeout, sizeof(timeout));


    n = recvfrom(sockfd, (char *)buffer, 1024, 
        0, (struct sockaddr *) &servaddr, &servaddrLen);

    // debug print
    cout << "Reply from server: ";
    for(int i=0; i<n; i++){
        if (i % 4 == 0){
            cout << " ";
        }
        cout << hex << setfill('0') << setw(2) << (int)buffer[i];
    }
    cout << endl;
    
    if (n < 0){
        cout << "Client failed to receive message." << endl;
        return n;
    }
    
    buffer[n] = '\0';
    return n;
}