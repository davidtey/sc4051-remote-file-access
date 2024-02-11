#include "utils.h"

#include <iostream>
using namespace std;

/* Marshals a string
 * Returns by reference to buffer
*/
void utils::marshalString(string s, char *b){
    char *cur = b;
    for (int i=0; i < s.size(); i++){
        *(cur + i) = s[i];
    }
}

/* Marshals an integer
 * Converts hostlong integer to netlong
 * Returns by reference to buffer
*/
void utils::marshalInt(int host_i, char *b){
    int net_i = htonl(host_i);
    char *cur = b;
    char *net_i_p = (char*) &net_i;

    for (int i=0; i<4; i++){
        *(cur + i) = *(net_i_p + i);
    }
}

void utils::unmarshal(string s, char *b){
    return;
}