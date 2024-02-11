#include "utils.h"

#include <iostream>
using namespace std;

/* Marshals a string (copy over to byte array)
*/
void utils::marshalString(string s, char *b){
    char *cur = b;
    for (int i=0; i < s.size(); i++){
        *cur = s[i];
        cur++;
    }
}

/* Marshals an integer
 * Converts hostlong integer to netlong
 * Save integer into byte array
*/
void utils::marshalInt(int host_i, char *b){
    int net_i = htonl(host_i);
    char *cur = b;

    *cur = (net_i & 0xff000000) >> 24;
    *(cur + 1) = (net_i & 0x00ff0000) >> 16;
    *(cur + 1) = (net_i & 0x0000ff00) >> 8;
    *(cur + 1) = net_i & 0x000000ff;
}

void utils::unmarshal(string s, char *b){
    return;
}