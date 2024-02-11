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
    char *net_i_p = (char*) &net_i;

    for (int i=0; i<4; i++){
        *(cur + i) = *(net_i_p + i);
    }
}

void utils::unmarshal(string s, char *b){
    return;
}