#ifndef REQUESTHANDLER_H
#define REQUESTHANDLER_H
#include <iostream>

#include "utils.h"
#include "HandlerNum.h"
#include "PrimitiveNum.h"

using namespace std;

namespace RequestHandler{
    int createReadRequest(string filePath, int offset, int numBytes, char *b);
    int createWriteRequest(string filePath, int offset, string insertString, char *b);
    int createMonitorRequest(string filePath, int monitorInterval, char *b);
}

#endif