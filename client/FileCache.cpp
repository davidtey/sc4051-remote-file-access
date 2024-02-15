#include "FileCache.h"

using namespace std;

/**FileCache constructor
 * 
 * Params
 * path: file path
 * lastModified: file last modified time (local)
 * fileLength: length of file
*/
FileCache::FileCache(string path, long long int lastModified, int fileLength){
    filePath = path;
    timeLastValidated = chrono::duration_cast< chrono::milliseconds >(chrono::system_clock::now().time_since_epoch()).count();
    timeLastModified = lastModified;
    fileLength = fileLength;
    fileData = new char[fileLength];
}

/**Checks if requested range of file overlaps with cached memory
 * 
 * Params
 * offset: offset from start of file in bytes
 * numBytes: number of bytes to read from file
 * 
 * Returns true if requested range overlaps with cached memory
*/
bool FileCache::overlap(int offset, int numBytes){
    int start = offset;
    int end = offset + numBytes - 1;

    if (end > fileLength){
        return false;
    }

    for (auto& curTuple : validRange){
        if (get<0>(curTuple) <= start && get<1>(curTuple) >= end){  // if (start, end) inside curTuple
            cout << "Valid Range: min: " << get<0>(curTuple) << ", max: " << get<1>(curTuple) << endl; 
            return true;
        }   
    }
    return false;
}

/**Checks if file cache is fresh
 * 
 * Params
 * freshnessInterval: threshold time needed for cache to be considered fresh
 * 
 * Returns true if time cache was last validated is within freshness interval
*/
bool FileCache::isFresh(int freshnessInterval){
    chrono::milliseconds T = chrono::duration_cast< chrono::milliseconds >(chrono::system_clock::now().time_since_epoch());
    if ((long long int) T.count() - timeLastValidated < freshnessInterval*1000){
        cout << "Time difference: " << (long long int) T.count() - timeLastValidated << endl;
        return true;
    }
    return false;
}

/**Checks if cache is valid
 * 
 * Params
 * serverLastModified: time same file on server was last modified
 * 
 * Returns true if time last modified on local cache is equal to time last modified on server
*/
bool FileCache::isValid(long long int serverLastModified){
    if (timeLastModified < serverLastModified){
        return false;
    }
    return true;
}

/**Read from file cacahe
 * 
 * Params
 * offset: offset from start of file in bytes
 * numBytes: number of bytes to read from file
 * 
 * Returns string of file content
*/
string FileCache::read(int offset, int numBytes){
    char out[numBytes + 1];

    for (int i=0; i<numBytes; i++){
        out[i] = fileData[offset+i];
    }

    out[numBytes] = '\0';

    return string(out);
}

/**Write to file cache
 * Checks if file is still valid, if modified, resets file length and valid ranges
 * If valid, insert new string into file cache and adds to valid range
 * 
 * offset: offset from start of file in bytes
 * insertString: string to insert into file
 * 
 * Returns 1
*/
int FileCache::write(int offset, string insertString, long long int serverLastModified, int fileLength){
    if (isValid(serverLastModified)){
        cout << "Cache valid, writing to current cached file!" << endl;
        for (int i=0; i<insertString.length(); i++){
            fileData[offset + i] = insertString[i];
        }
        addToValidRange(offset, offset + insertString.length() - 1);
    }
    else{
        cout << "Cache invalid, overwriting current cached file..." << endl;
        fileData = new char[fileLength];
        validRange.clear();
        for (int i=0; i<insertString.length(); i++){
            fileData[offset + i] = insertString[i];
        }
        addToValidRange(offset, offset + insertString.length() - 1);
    }
    return 1;
}

/**Write to file cache (whole file)
 * 
 * Params
 * data: file content to write into file
 * 
 * Returns 1
*/
int FileCache::writeFile(string data){
    fileData = data;
    return 1;
}

/**Add to valid range
 * When reading from server, adds recently accessed range of file into cache
 * 
 * Params
 * start: start byte
 * end: end byte
*/
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

        for (auto& curTuple : newValidRange){
            validRange.push_back(curTuple);                         // set validRange to newValidRange
        }
    }
}
