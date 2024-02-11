#include "client.h"

using namespace std;

/* Client connect menu
 * Allow client to specify server address & port
*/
int Client::connectMenu(){
    bool connected = false;
    string serverIP;
    int serverPort;

    while (!connected){
        cout << "\n===== Remote File Access System Client =====\n";
        cout << "To access remote files, please connect to the server.\n";
        cout << "Server address: ";
        cin >> serverIP;
        serverPort = readInt("Server port: ", 0, 65535);

        if (udpClient.connectServer(serverIP, serverPort) == 1){
            connected = true;
        }
    }
}

/* Client invocation menu
 * Allow client to specify invocation semantics
*/
int Client::invocationMenu(){
    int invocationInput;

    cout << "1. At-least-once invocation semantics" << endl;
    cout << "2. At-most-once invocation semantics" << endl;
    invocationInput = readInt("Please choose the invocation semantics: ", 1, 2);
}

/* Client main menu
 * Allow client to navigate the file system and send requests to server
 * Actions include: 
 * 1. Read file (string filePath, int offset, int numBytes)
 * 2. Write to file (string filePath, int offset, string insertString)
 * 3. Monitor file (string filePath, int monitorInterval)
 * 4. Quit
*/
int Client::mainMenu(){
    int choice = 0;
    string filePath, insertString;
    int offset, numBytes, monitorInterval;
    while (choice != 4){
        cout << "\n===== Remote File Access System =====\n";
        cout << "1. Read file\n";
        cout << "2. Write to file\n";
        cout << "3. Monitor file updates\n";
        cout << "4. Quit\n";
        choice = readInt("Action: ", 1, 4);

        offset, numBytes, monitorInterval = -1;

        switch (choice){
            case 1:                     // read file
                cout << "\n----- Read File -----\n";
                cout << "File path: ";
                getline(cin >> ws, filePath);

                offset = readInt("Read offset (in bytes): ", 0, INT_MAX);
                numBytes = readInt("Number of bytes to read: ", 0, INT_MAX);

                udpClient.send((char *)"Trying to read file...");

                char buffer[1024];
                udpClient.recv(buffer);
                cout << "Reply from server: " << buffer << endl;
                break;
            
            case 2:                     // write to file
                cout << "\n----- Write to File -----\n";
                cout << "File path: ";
                getline(cin >> ws, filePath);

                offset = readInt("Write offset (in bytes): ", 0, INT_MAX);

                cout << "Insert string: ";
                getline(cin >> ws, insertString);
                break;

            case 3:                     // monitor file
                cout << "\n----- Monitor File -----\n";
                cout << "File path: ";
                getline(cin >> ws, filePath);
                monitorInterval = readInt("Monitor interval (in s): ", 0, INT_MAX);
                break;

            case 4:                     // quit
                cout << "Closing program...\n";
                return 1;
            
            default:
                cout << "Please enter integer from 1 to 4!\n";
        }
    }
}

/* Read integer from user input, assumes nonnegative input and does error checking for non integer input
 * and within valid range.
 * prompt: message prompt for user
 * min: minimum of valid range
 * max: maximum of valid range
*/
int Client::readInt(string prompt, int min, int max){
    int input = -1;

    while (input == -1){
        cout << prompt;
        cin >> input;

        if (cin.fail()) {   // handle non digit inputs
                cin.clear();
                cin.sync();
                cout << "Please enter an integer!\n";
                input = -1;
                continue;
        }

        if (input < min || input > max){
            cout << "Please enter an integer between " << min << " and " << max << "!" << endl;
        }
    }
    return input;
}

/* Client constructor
 * Calls connect menu to initialise & check connection
*/ 
Client::Client(){
    connectMenu();
}

/* Start client process
 * Starts main menu
*/
int Client::startProcess(){
    return mainMenu();
}

/* Main program
 * Creates client & starts it
*/
int main(){
    Client client;
    client.startProcess();
}