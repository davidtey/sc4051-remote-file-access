package server;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.io.FileNotFoundException;
import java.io.IOException;

/**Read Request Handler Class
 * <pre>
 * Handles read requests by unmarshalling request, invoking methods, marshalling reply and returning.
 * Handles printing of requests and replies onto server console.
 * </pre>
 */
public class ReadRequestHandler extends RequestHandler {
    String filePath;            // request argument: file path
    int offset;                 // request argument: offset
    int numBytes;               // request argument: number of bytes to read

    long serverLastModified;    // return: database file last modified time
    int fileLength;             // return: database file length
    int fileContentLength;      // return: requested database file content length
    byte[] fileContent;         // return: database file content

    /**Constructor for ReadRequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public ReadRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.READ_FILE_REQUEST;
    }

    /**Unmarshal read request according to predefined protocol.
     */
    public void unmarshalRequest(){
        int filePathLength = Utils.unmarshalInt(request, 8);              // get filePath length
        filePath = Utils.unmarshalString(request, 12, filePathLength);    // get filePath
        offset = Utils.unmarshalInt(request, filePathLength + 12);              // get offset
        numBytes = Utils.unmarshalInt(request, filePathLength + 16);            // get numBytes
    }

    /**Print read request on to server console.
     */
    public void printRequest(){
        System.out.println("[From " + clientAddr + ":" + clientPort + "] Read File Request #" + requestID + " from " + 
        filePath + " with " + offset + " bytes offset " + " for " + numBytes + " bytes."); 
    }

    /**Execute read request, invoke methods from Database.
     */
    public void executeRequest(){
        try{
            String fullFilePath = Database.databasePath + filePath;             // add database path in front of file path              
            serverLastModified = Database.getFileLastModified(fullFilePath);        // get server last modified time
            fileLength = Database.getFileLength(fullFilePath);                      // get file length
            fileContent = Database.readFromFile(fullFilePath, offset, numBytes);    // get file content length
            fileContentLength = fileContent.length;                             // get file content
        }
        catch (FileNotFoundException e){                                        // handle if file not found
            errorEncountered = true;
            errorMessage = "Error encountered! File at " + filePath + " could not be found.";
            errorMessageLength = errorMessage.length();
        }
        catch (OutOfFileRangeException e){                                      // handle if offset exceed file length
            errorEncountered = true;
            errorMessage = "Error encountered! File offset exceeds the file length. Offset: " + offset + 
            " File length: " + Database.getFileLength(filePath);
            errorMessageLength = errorMessage.length();
        }
        catch (IOException e){                                                  // handle IOException
            errorEncountered = true;
            errorMessage = e.getMessage();
            errorMessageLength = errorMessage.length();
        }
    }
    
    /**Marshals read request reply
     * <pre>
     * Successful reply:
     * int (4 bytes): READ_FILE_REPLY handler number
     * long (8 bytes): server file last modified time
     * int (4 bytes): file length
     * int (4 bytes): file content length
     * String (n bytes): file content
     * 
     * Error reply: 
     * int (4 bytes): ERROR_REPLY handler number
     * int (4 bytes): error message length
     * String (n bytes): error message
     * </pre>
     */
    public void marshalReply(){
        if (!errorEncountered){     // successful reply
            reply = new byte[fileContentLength + 20];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.READ_FILE_REPLY), 0);
            Utils.marshalLong(reply, serverLastModified, 4);
            Utils.marshalInt(reply, fileLength, 12);
            Utils.marshalInt(reply, fileContentLength, 16);
            Utils.marshalBytes(reply, fileContent, 20);
        }
        else{   // error reply
            reply = new byte[errorMessageLength + 8];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.ERROR_REPLY), 0);
            Utils.marshalInt(reply, errorMessageLength, 4);
            Utils.marshalString(reply, errorMessage, 8);
        }
    }

    /**Print reply on to server console.
     */
    public void printReply(){   
        if (!errorEncountered){     // successful reply
            System.out.println("[To " + clientAddr + ":" + clientPort + "] " + new String(fileContent, StandardCharsets.UTF_8));
        }
        else{
            System.err.println("[To " + clientAddr + ":" + clientPort + "] " + errorMessage);
        }
    }
}
