#include "client.h"
#include "RequestHandler.h"

using namespace std;

/* Client connect menu
 * Allow client to specify server address & port
*/
int Client::connectMenu(){
    bool connected = false;
    string serverIP;
    int serverPort;
    int freshnessInterval;

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
    return 1;
}

/* Client invocation menu
 * Allow client to specify invocation semantics
*/
int Client::invocationMenu(){
    int invocationInput;

    cout << "1. At-least-once invocation semantics" << endl;
    cout << "2. At-most-once invocation semantics" << endl;
    invocationInput = readInt("Please choose the invocation semantics: ", 1, 2);
    return 1;
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

    while (choice != 6){
        cout << "\n===== Remote File Access System =====" << endl;
        cout << "1. Read file" << endl;
        cout << "2. Write to file" << endl;
        cout << "3. Monitor file updates" << endl;
        cout << "4. Delete from file" << endl;
        cout << "5. List Directory" << endl;
        cout << "6. Quit" << endl;
        choice = readInt("Action: ", 1, 6);

        offset, numBytes, monitorInterval = -1;

        switch (choice){
            case 1: {                    // read file
                readFileMenu();
                break;
            }
            case 2:                     // write to file
                writeFileMenu();
                break;

            case 3:                     // monitor file
                monitorFileMenu();
                break;

            case 4:
                deleteFromFileMenu();   // delete from file
                break;
            
            case 5:
                listDirMenu();          // list directory 
                break;

            case 6:                     // quit
                cout << "Closing program...\n";
                return 1;
            
            default:
                cout << "Please enter integer from 1 to 6!\n";
        }
    }
    return 1;
}

int Client::readFileMenu(){
    cout << "\n----- Read File -----" << endl;
    cout << "File path: ";
    getline(cin >> ws, filePath);                               // user input filePath
    offset = readInt("Read offset (in bytes): ", 0, INT_MAX);   // user input offset
    numBytes = readInt("Number of bytes to read: ", 0, INT_MAX);// user input numBytes

    // create request and send
    reqLength = RequestHandler::createReadRequest(requestID, filePath, offset, numBytes, reqBuffer);    //create request
    
    udpClient.send(reqBuffer, reqLength);   // send request

    // receive reply and handle
    udpClient.recv(replyBuffer);            // receive reply
    ReplyHandler::handleReply(replyBuffer); // handle reply
    requestID++;
    return 1;
}

int Client::writeFileMenu(){
    cout << "\n----- Write to File -----" << endl;;
    cout << "File path: ";
    getline(cin >> ws, filePath);                               // user input filePath
    offset = readInt("Write offset (in bytes): ", 0, INT_MAX);  // user input offset
    cout << "Insert string: ";
    getline(cin >> ws, insertString);                           // user input inputString

    // create request and send
    reqLength = RequestHandler::createWriteRequest(requestID, filePath, offset, insertString, reqBuffer);   // create request

    udpClient.send(reqBuffer, reqLength);   // send request

    // receive reply and handle
    udpClient.recv(replyBuffer);            // receive reply
    ReplyHandler::handleReply(replyBuffer); // handle reply

    requestID++;
    return 1;
}

int Client::monitorFileMenu(){
    cout << "\n----- Monitor File -----" << endl;
    cout << "File path: ";
    getline(cin >> ws, filePath);                                       // user input filePath
    monitorInterval = readInt("Monitor interval (in s): ", 0, INT_MAX); // user input monitorInterval

    // create request and send
    reqLength = RequestHandler::createMonitorRequest(requestID, filePath, monitorInterval, reqBuffer);   // create request
    
    udpClient.send(reqBuffer, reqLength);   // send request

    // receive reply and handle (monitor until expired)

    udpClient.recv(replyBuffer);            // receive reply
    HandlerNum replyType = ReplyHandler::handleReply(replyBuffer); // handle reply

    if (replyType == HandlerNum::MONITOR_FILE_ACK){     // successfully registered
        while (replyType != HandlerNum::MONITOR_FILE_EXPIRE){
            udpClient.recv(replyBuffer);            // receive reply
            replyType = ReplyHandler::handleReply(replyBuffer); // handle reply
        }
    }
    
    requestID++;
    return 1;
}

int Client::deleteFromFileMenu(){
    cout << "\n----- Delete from File -----" << endl;
    cout << "File path: ";
    getline(cin >> ws, filePath);                               // user input filePath
    offset = readInt("Bytes offset (in bytes): ", 0, INT_MAX);   // user input offset
    numBytes = readInt("Number of bytes to delete: ", 0, INT_MAX);// user input numBytes

    // create request and send
    reqLength = RequestHandler::createDeleteFromFileRequest(requestID, filePath, offset, numBytes, reqBuffer);    //create request
    
    udpClient.send(reqBuffer, reqLength);   // send request

    // receive reply and handle
    udpClient.recv(replyBuffer);            // receive reply
    ReplyHandler::handleReply(replyBuffer); // handle reply
    
    requestID++;
    return 1;
}

int Client::listDirMenu(){
    cout << "\n----- Read File -----" << endl;
    cout << "Directory path ('.' for root): ";
    getline(cin >> ws, filePath);                               // user input filePath

    // create request and send
    reqLength = RequestHandler::createListDirRequest(requestID, filePath, reqBuffer);    //create request

    udpClient.send(reqBuffer, reqLength);   // send request

    // receive reply and handle
    udpClient.recv(replyBuffer);            // receive reply
    ReplyHandler::handleReply(replyBuffer); // handle reply
    
    requestID++;
    return 1;
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
    requestID = 1;
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