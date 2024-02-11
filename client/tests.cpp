#include <assert.h>
#include <stdio.h>
#include <string>
#include "utils.h"
#include <iostream>
#include <iomanip>

using namespace std;


void test_marshalling(){
    cout << "Testing marshalString function..." << endl;
    string s = "teststring";
    char buffer[20];

    utils::marshalString(s, buffer);
    cout << buffer << endl;

    int num = 2864434397;
    char intBuffer[4];

    utils::marshalInt(num, intBuffer);
    for (int i=0; i<4; i++){
        std::cout << "0x" << std::setw(2) << std::setfill('0') << std::hex << (intBuffer[i] & 0xff) << ' ';
    }

    cout << "\n\n";
    int n_num = htonl(num);
    char *r = (char *) &n_num;
    
    for (int j=0; j<4; j++){
        cout << "0x" << (*(r+j) & 0xff) << ' ';
    }
}

int main(){
    test_marshalling();
}