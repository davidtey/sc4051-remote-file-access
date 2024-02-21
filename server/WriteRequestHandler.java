package server;

import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.io.IOException;

/**Write Request Handler Class
 * <pre>
 * Handles write requests by unmarshalling request, invoking methods, marshalling reply and returning.
 * Handles printing of requests and replies onto server console.
 * </pre>
 */
public class WriteRequestHandler extends RequestHandler{
    String filePath;            // request argument: file path
    int offset;                 // request argument: offset
    String insertString;        // request argument: insert string

    long serverLastModified;    // return: database file last modified time
    int fileLength;             // return: database file length

    /**Constructor for WriteRequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public WriteRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.WRITE_FILE_REQUEST;
    }

    /**Unmarshal write request according to predefined protocol.
     */
    public void unmarshalRequest(){
        int filePathLength = Utils.unmarshalInt(request, 8);                                // get filePath length
        filePath = Utils.unmarshalString(request, 12, filePathLength);                      // get filePath
        offset = Utils.unmarshalInt(request, filePathLength + 12);                                // get offset
        int insertStringLength = Utils.unmarshalInt(request, filePathLength + 16);                // get insertString length
        insertString = Utils.unmarshalString(request, filePathLength + 20, insertStringLength);   // get insertString
    }

    /**Print write request on to server console.
     */
    public void printRequest(){
        System.out.println("[From " + clientAddr + ":" + clientPort + "] Write File Request #" + requestID + " write '" + insertString +
        "' into " + filePath + " at offset " + offset); 
    }

    /**Execute write request, invoke methods from Database. todo
     */
    public void executeRequest(){
        String fullFilePath = Database.databasePath + filePath;
        try{
            Database.writeToFile(fullFilePath, offset, insertString.getBytes());
            serverLastModified = Database.getFileLastModified(fullFilePath);
            fileLength = Database.getFileLength(fullFilePath);
            MonitorRequestHandler.notifyUpdate(filePath);
        }
        catch (FileNotFoundException e){                                        // handle if file not found
            errorEncountered = true;
            errorMessage = "Error encountered! File at " + filePath + " could not be found.";
            errorMessageLength = errorMessage.length();
        }
        catch (OutOfFileRangeException e){                                      // handle if offset exceed file length
            errorEncountered = true;
            errorMessage = "Error encountered! File offset exceeds the file length. Offset: " + offset + 
            " File length: " + Database.getFileLength(fullFilePath);
            errorMessageLength = errorMessage.length();
        }
        catch (IOException e){                                                  // handle IOException
            errorEncountered = true;
            errorMessage = e.getMessage();
            errorMessageLength = errorMessage.length();
        }
    }

    /**Marshals write request reply
     * <pre>
     * Successful reply:
     * int (4 bytes): INSERTION_ACK handler number
     * long (8 bytes): server file last modified time
     * int (4 bytes): file length
     * 
     * Error reply: 
     * int (4 bytes): ERROR_REPLY handler number
     * int (4 bytes): error message length
     * String (n bytes): error message
     * </pre>
     */
    public void marshalReply(){
        if (!errorEncountered){     // successful reply
            reply = new byte[16];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.INSERTION_ACK), 0);
            Utils.marshalLong(reply, serverLastModified, 4);
            Utils.marshalInt(reply, fileLength, 12);
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
            System.out.println("[To " + clientAddr + ":" + clientPort + "] " + "INSERTION_ACK");
        }
        else{
            System.err.println("[To " + clientAddr + ":" + clientPort + "] " + errorMessage);
        }
    }
}
