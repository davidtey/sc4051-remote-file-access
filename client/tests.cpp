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

    int num = 65432;
    char intBuffer[4];

    utils::marshalInt(num, intBuffer);
    for (int i=0; i<4; ++i){
        cout << hex << setw(2) << setfill('0') << (int) intBuffer[i];
    }
}

int main(){
    test_marshalling();
}