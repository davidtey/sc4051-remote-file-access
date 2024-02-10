#include "client.h"

using namespace std;

int main(){
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