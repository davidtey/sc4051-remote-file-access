#include "FileCache.h"

using namespace std;

FileCache::FileCache(string path, long long int lastModified, int fileLength){
    filePath = path;
    timeLastValidated = chrono::duration_cast< chrono::milliseconds >(chrono::system_clock::now().time_since_epoch()).count();
    timeLastModified = lastModified;
    fileLength = fileLength;
}

bool FileCache::overlap(int offset, int numBytes){
    int start = offset;
    int end = offset + numBytes - 1;

    if (end > fileLength){
        return false;
    }

    for (auto& curTuple : validRange){
        if (get<0>(curTuple) >= start && get<1>(curTuple) <= end){  // if (start, end) inside curTuple
            return true;
        }   
    }
    return false;
}

bool FileCache::isFresh(int freshnessInterval){
    chrono::milliseconds T = chrono::duration_cast< chrono::milliseconds >(chrono::system_clock::now().time_since_epoch());
    if (T.count() - timeLastValidated < freshnessInterval*1000){
        return true;
    }
    return false;
}

bool FileCache::isValid(long long int serverLastModified){
    if (timeLastModified < serverLastModified){
        return false;
    }
    return true;
}

string FileCache::read(int offset, int numBytes){
    char out[numBytes];

    for (int i=0; i<numBytes; i++){
        out[i] = fileData[offset+i];
    }

    return string(out);
}

int FileCache::write(int offset, string insertString){
    for (int i=0; i<insertString.length(); i++){
        fileData[offset + i] = insertString[i];
    }
    return 1;
}

int FileCache::writeFile(string data){
    fileData = data;
    return 1;
}

void FileCache::addToValidRange(int start, int end){
    if (validRange.empty()){
        validRange.push_back(make_tuple(start, end));
    }
    else{
        int low = start;
        int lowIndex = INT_MAX;
        int highIndex = 0;
        int high = end;
        vector<tuple<int, int>> newValidRange;
        int curIndex = 0;
        bool addCur = true;
        
        for (auto& curTuple : validRange){
            addCur = true;
            if (get<0>(curTuple) <= start && get<1>(curTuple) >= start){    // if start is in range of curTuple
                low = min(low, get<0>(curTuple));
                lowIndex = min(lowIndex, curIndex);
                addCur = false;
            }

            if (get<0>(curTuple) <= end && get<1>(curTuple) >= end){        // if end is in range of curTuple
                high = max(high, get<1>(curTuple));
                highIndex = min(highIndex, curIndex);
                addCur = false;
            }

            if (get<0>(curTuple) > start && get<1>(curTuple) < end){        // if curTuple is inside range of (start, end)
                addCur = false;
            }

            if (addCur){
                newValidRange.push_back(curTuple);
            }
            curIndex++;
        }

        newValidRange.push_back(make_tuple(low, high));     // add union of overlapping tuples as 1 single tuple
        validRange.clear();
        validRange = newValidRange;                         // set validRange to newValidRange
    }
}
