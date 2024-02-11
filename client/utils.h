#ifndef UTILS_H
#define UTILS_H
#include <string>
#include <winsock2.h>

using namespace std;

namespace utils{
    void marshalString(string s, char *b);
    void marshalInt(int i, char *b);

    void unmarshal(string s, char *b);
}

#endif