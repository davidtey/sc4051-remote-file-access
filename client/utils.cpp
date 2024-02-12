#include "utils.h"

#include <iostream>
using namespace std;

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

/* Marshals a string
 * Returns by reference to buffer
*/
void utils::marshalString(string s, char *b){
    char *cur = b;
    for (int i=0; i < s.size(); i++){
        *(cur + i) = s[i];
    }
}

/* Unmarshals int
 * Returns by value
 * Assumes network transmit in big endian and converts from netlong to hostlong
*/
int utils::unmarshalInt(char *b){
    int host_i;
    char *host_i_p = (char *) &host_i;  // byte pointer starting at host_i address
    for (int i=0; i<4; i++){
        *(host_i_p + i) = *(b + i);     // set integer values byte by byte
    }

    host_i = ntohl(host_i);
    return host_i;
}

/* Unmarshals long int (64 bit)
 * Returns by value
 * Assumes network transmit in big endian and converts from netlong to hostlong
*/
long long utils::unmarshalLong(char *b){
    long long host_i;
    char *host_i_p = (char *) &host_i;  // byte pointer starting at host_i address
    for (int i=0; i<8; i++){
        *(host_i_p + i) = *(b + i);     // set integer values byte by byte
    }

    host_i = ntohll(host_i);
    return host_i;
}

/* Unmarshals string
 * Returns by value
*/
string utils::unmarshalString(char *b, int length){
    char out[length + 1];
    for (int i=0; i<length; i++){
        out[i] = *(b+i);
    }
    out[length] = '\0';

    return out;
}