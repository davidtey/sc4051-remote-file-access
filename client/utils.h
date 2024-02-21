#ifndef UTILS_H
#define UTILS_H
#include <string>
#include <winsock2.h>
#include <iostream>

#define htonll(x) ((1==htonl(1)) ? (x) : (((long long int)htonl((x) & 0xFFFFFFFFUL)) << 32) | htonl((int)((x) >> 32)))
#define ntohll(x) ((1==ntohl(1)) ? (x) : (((long long int)ntohl((x) & 0xFFFFFFFFUL)) << 32) | ntohl((int)((x) >> 32)))

using namespace std;

/**Utilities
 * Used for marshalling and unmarshalling primitives
*/
namespace utils{
    void marshalInt(int x, char *b);
    void marshalLong(long long int host_i, char *b);
    void marshalString(string s, char *b);

    int unmarshalInt(char *b);
    long long unmarshalLong(char *b);
    string unmarshalString(char *b, int length);
}

#endif