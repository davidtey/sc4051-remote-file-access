#include "client.h"
# define MSG_WAITALL 0x08

using namespace std;

class Client{
    private:
        string clientIP;
        string serverIP;
        int clientPort;
        int serverPort;
        int sockfd;

        int connectMenu(){
            bool connected = false;

            while (!connected){
                cout << "\n===== Remote File Access System =====\n";
                cout << "To access remote files, please connect to the server.\n";
                clientPort = readInt("Client port:");
                cout << "Server address: ";
                cin >> serverIP;
                serverPort = readInt("Server port: ");

                connected = true;
            }
            
        }

        int mainMenu(){
            int choice = 0;
            string filePath, insertString;
            int offset, numBytes, monitorInterval;
            while (choice != 4){
                cout << "\n===== Remote File Access System =====\n";
                cout << "1. Read file\n";
                cout << "2. Write to file\n";
                cout << "3. Monitor file updates\n";
                cout << "4. Quit\n";
                cout << "Action: ";
                offset, numBytes, monitorInterval = -1;
                

                cin >> choice;

                if (cin.fail()) {   // handle non digit inputs
                    cin.clear();
                    cin.sync();
                    cout << "Please enter an integer from 1 to 4!\n";
                    continue;
                }

                switch (choice){
                    case 1:                     // read file
                        cout << "\n----- Read File -----\n";
                        cout << "File path: ";
                        getline(cin >> ws, filePath);

                        offset = readInt("Read offset (in bytes): ");
                        numBytes = readInt("Number of bytes to read: ");

                        break;
                    
                    case 2:                     // write to file
                        cout << "\n----- Write to File -----\n";
                        cout << "File path: ";
                        getline(cin >> ws, filePath);

                        offset = readInt("Write offset (in bytes): ");

                        cout << "Insert string: ";
                        getline(cin >> ws, insertString);
                        break;

                    case 3:                     // monitor file
                        cout << "\n----- Monitor File -----\n";
                        cout << "File path: ";
                        getline(cin >> ws, filePath);
                        monitorInterval = readInt("Monitor interval (in s): ");
                        break;

                    case 4:                     // quit
                        cout << "Closing program...\n";
                        return 1;
                    
                    default:
                        cout << "Please enter integer from 1 to 4!\n";
                }
            }
        }

        int readInt(string prompt){
            int input = -1;

            while (input < 0){
                cout << prompt;
                cin >> input;

                if (cin.fail()) {   // handle non digit inputs
                        cin.clear();
                        cin.sync();
                        cout << "Please enter an integer!\n";
                        input = -1;
                        continue;
                }

                if (input < 0){
                    cout << "Please enter a positive interger!\n";
                }
            }
            return input;
        }

    public:
        Client(){
            WORD wVersionRequested;
            WSADATA wsaData;
            wVersionRequested = MAKEWORD(2, 2);

            WSAStartup(wVersionRequested, &wsaData);
            connectMenu();

            struct sockaddr_in servaddr;
            struct sockaddr_in clientaddr;
            
            char hostbuffer[256];
            char *IPbuffer;
            struct hostent *host_entry;
            int hostname;
        
            // To retrieve hostname
            hostname = gethostname(hostbuffer, sizeof(hostbuffer));
        
            // To retrieve host information
            host_entry = gethostbyname(hostbuffer);
        
            // To convert an Internet network
            // address into ASCII string
            IPbuffer = inet_ntoa(*((struct in_addr*)
                                host_entry->h_addr_list[0]));
            

            if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ){
                cout << "Socket creation failed with error " << sockfd << ", " << WSAGetLastError();;
                exit(EXIT_FAILURE);
            }

            memset(&servaddr, 0, sizeof(servaddr)); 

            servaddr.sin_family = AF_INET;
            servaddr.sin_port = htons(serverPort);
            servaddr.sin_addr.s_addr = inet_addr(serverIP.c_str());

            clientaddr.sin_family = AF_INET;
            clientaddr.sin_port = htons(clientPort);
            clientaddr.sin_addr.s_addr = inet_addr(serverIP.c_str());

            const char *hello = "Hello from client";

            //bind(sockfd, (const struct sockaddr *) &clientaddr, sizeof(clientaddr));

            sendto(sockfd, (const char *) hello, strlen(hello), 0,
                (const struct sockaddr *) &servaddr, sizeof(servaddr)); 

            char buffer[1024];
            int n;
            socklen_t len;

            n = recvfrom(sockfd, (char *)buffer, 1024,  
                MSG_WAITALL, (struct sockaddr *) &servaddr, 
                &len); 
            
            buffer[n] = '\0'; 

            cout << "n: " << n << endl;
            cout << "Server :";

            for (int i=0; i<10; i++){
                
                cout << buffer[i];
            }

            cout << "\nWSAGetLastError: " << WSAGetLastError();
            
        }

        int startProcess(){
            return mainMenu();
        }

};

int main(){
    Client client;
    client.startProcess();
}

