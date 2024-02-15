#ifndef FILECACHE_H
#define FILECACHE_H
#include <string>
#include <vector>
#include <bits/stdc++.h>
#include <chrono>

using namespace std;

/**FileCache class
 * Used to cache files recently read
*/
class FileCache{
    private:
        string filePath;
        int fileLength;
        vector<tuple<int, int>> validRange;
        string fileData;
    
    public:
        long long int timeLastValidated;
        long long int timeLastModified;         // 64 bit int used here so that it is compatible with java
        FileCache(string path, long long int lastModified, int fileLength);
        bool overlap(int offset, int numBytes);
        bool isFresh(int freshnessInterval);
        bool isValid(long long int serverLastModified);
        string read(int offset, int numBytes);
        int write(int offset, string insertString);
        int writeFile(string data);
        void addToValidRange(int start, int end);
};

#endif