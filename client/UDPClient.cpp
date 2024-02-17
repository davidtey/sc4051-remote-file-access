#include "UDPClient.h"

using namespace std;

/**UDPClient constructor
 * Creates socket object
*/
UDPClient::UDPClient(){
    srand(time(0));
    WORD wVersionRequested;
    WSADATA wsaData;
    wVersionRequested = MAKEWORD(2, 2);

    WSAStartup(wVersionRequested, &wsaData);

    if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ){
        cout << "Socket creation failed with error " << sockfd << ", WSAError: " << WSAGetLastError();;
        exit(EXIT_FAILURE);
    }
}

/**Connect to server
 * Params
 * ip: Server IP address
 * port: Server port
*/
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

/**Send request & receive reply
 * Will block execution until reply is received.
 * 
 * Params
 * *msg: request to be sent (char array)
 * msgLen: length of request
 * *replyBuffer: char array buffer to store reply
 * useTimeout: set to true to set timeout
 * Returns 1 when reply received
*/
int UDPClient::sendNReceive(const char *msg, int msgLen, char *replyBuffer, bool useTimeout){
    
    sendRequest(msg, msgLen);

    while (recvReply(replyBuffer, useTimeout) == -1){
        this_thread::sleep_for(chrono::milliseconds(500));
        sendRequest(msg, msgLen);
    }
    return 1;
}

/**Send request to server
 * 
 * Params
 * *msg: request to be sent (char array)
 * msgLen: length of request
*/
int UDPClient::sendRequest(const char *msg, int msgLen){
    cout << "Sending request to server..." << endl;
    if (((double) rand() / (RAND_MAX)) < LOSS_PROBABILITY){
        cout << "Simulating request packet loss..." << endl;
        return -1;
    }
    // debug print
    /*
    cout << "Sending to server: ";

    for(int i=0; i<msgLen; i++){
        if (i % 4 == 0){
            cout << " ";
        }
        cout << hex << setfill('0') << setw(2) << (int)msg[i];
    }
    cout << endl;
    */
    
    if (sendto(sockfd, msg, msgLen, 0, 
    (const struct sockaddr *) &servaddr, sizeof(servaddr)) < 0){
        cout << "Client failed to send message: " << msg << endl;
        return -1;
    }
    return 1;
}

/**Receive reply from server
 * 
 * Params
 * *replyBuffer: char array buffer to store reply
 * useTimeout: set to true to set timeout
*/
int UDPClient::recvReply(char *buffer, bool useTimeout){
    //struct timeval timeout;
    if (useTimeout){
        DWORD timeout = 100;
    }
    else{
        DWORD timeout = 0;
    }
    setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (char *) &timeout, sizeof(timeout));

    n = recvfrom(sockfd, (char *)buffer, 1024, 
        0, (struct sockaddr *) &servaddr, &servaddrLen);

    // debug print
    /*
    cout << "Reply from server: ";
    for(int i=0; i<n; i++){
        if (i % 4 == 0){
            cout << " ";
        }
        cout << hex << setfill('0') << setw(2) << (int)buffer[i];
    }
    cout << endl;
    */
    
    if (n < 0){
        cout << "Client failed to receive message." << endl;
        return n;
    }
    else{
        cout << "Reply received from server!" << endl;
    }
    
    buffer[n] = '\0';
    return n;
}