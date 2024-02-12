#ifndef UTILS_H
#define UTILS_H
#include <string>
#include <winsock2.h>

using namespace std;

namespace utils{
    void marshalInt(int x, char *b);
    void marshalString(string s, char *b);

    int unmarshalInt(char *b);
    long long unmarshalLong(char *b);
    string unmarshalString(char *b, int length);
}

#endif