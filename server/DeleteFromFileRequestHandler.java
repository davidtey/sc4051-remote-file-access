package server;
import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.io.IOException;

/**Delete From File Request Handler Class
 * <pre>
 * Handles delete from file requests by unmarshalling request, invoking methods, marshalling reply and returning.
 * Handles printing of requests and replies onto server console.
 * </pre>
 */
public class DeleteFromFileRequestHandler extends RequestHandler{
    String filePath;            // request argument: file path
    int offset;                 // request argument: offset
    int numBytes;               // request argument: number of bytes to delete

    /**Constructor for DeleteFromFileRequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public DeleteFromFileRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.DELETE_FROM_FILE_REQUEST;
    }

    /**Unmarshal delete from file request according to predefined protocol.
     */
    public void unmarshalRequest(){
        int filePathLength = Utils.unmarshalInt(request, 8);            // get filePath length
        filePath = Utils.unmarshalString(request, 12, filePathLength);  // get filePath
        offset = Utils.unmarshalInt(request, filePathLength + 12);            // get offset
        numBytes = Utils.unmarshalInt(request, filePathLength + 16);          // get numBytes
    }

    /**Print delete from file request on to server console.
     */
    public void printRequest(){
        System.out.println("[From " + clientAddr + ":" + clientPort + "] Delete From File Request #" + requestID + " from " + 
        filePath + " with " + offset + " bytes offset " + " for " + numBytes + " bytes."); 
    }

    /**Execute delete from file request, invoke methods from Database.
     */
    public void executeRequest(){
        try{
            String fullFilePath = Database.databasePath + filePath;                 // add database path in front of file path         
            Database.deleteFromFile(fullFilePath, offset, numBytes);                // delete from file
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

    /**Marshals delete from file request reply
     * <pre>
     * Successful reply:
     * int (4 bytes): DELETE_FROM_FILE_ACK handler number
     * 
     * Error reply: 
     * int (4 bytes): ERROR_REPLY handler number
     * int (4 bytes): error message length
     * String (n bytes): error message
     * </pre>
     */
    public void marshalReply(){
        if (!errorEncountered){     // successful reply
            reply = new byte[4];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.DELETE_FROM_FILE_ACK), 0);
        }
        else{                       // error reply
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
            System.out.println("[To " + clientAddr + ":" + clientPort + "] " + "DELETE_FROM_FILE_ACK");
        }
        else{
            System.err.println("[To " + clientAddr + ":" + clientPort + "] " + errorMessage);
        }
    }
}
