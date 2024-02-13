package server;
import java.net.InetAddress;
import java.io.FileNotFoundException;

/**Get File Attributes Request Handler Class
 * <pre>
 * Handles get file attributes requests by unmarshalling request, invoking methods, marshalling reply and returning.
 * Handles printing of requests and replies onto server console.
 * </pre>
 */
public class GetFileAttrRequestHandler extends RequestHandler{
    String filePath;                    // request argument: file path
    long serverLastModified;            // return: database file last modified time

    /**Constructor for GetFileAttrRequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public GetFileAttrRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.GET_FILE_ATTR_REQUEST;
    }
    
    /**Unmarshal get file attributes request according to predefined protocol.
     */
    public void unmarshalRequest(){
        int filePathLength = Utils.unmarshalInt(request, 8); // get filePath length
        filePath = Utils.unmarshalString(request, 12, filePathLength); // get filePath
    }

    /**Print get file attributes request on to server console.
     */
    public void printRequest(){
        System.out.println("[From " + clientAddr + ":" + clientPort + "] Get File Attributes Request #" + requestID + " for " + filePath); 
    }

    /**Execute read request, invoke methods from Database.
     */
    public void executeRequest(){
        try{
            String fullFilePath = Database.databasePath + filePath;                 // add database path in front of file path
            serverLastModified = Database.getFileLastModified(fullFilePath);
        }
        catch (FileNotFoundException e){                                        // handle if file not found
            errorEncountered = true;
            errorMessage = "Error encountered! File at " + filePath + " could not be found.";
            errorMessageLength = errorMessage.length();
        }
    }

    /**Marshals get file attributes request reply
     * <pre>
     * Successful reply:
     * int (4 bytes): GET_FILE_ATTR_REPLY handler number
     * long (8 bytes): server file last modified time
     * 
     * Error reply: 
     * int (4 bytes): ERROR_REPLY handler number
     * int (4 bytes): error message length
     * String (n bytes): error message
     * </pre>
     */
    public void marshalReply(){
        if (!errorEncountered){     // successful reply
            reply = new byte[12];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.GET_FILE_ATTR_REPLY), 0);
            Utils.marshalLong(reply, serverLastModified, 4);
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
            System.out.println("[To " + clientAddr + ":" + clientPort + "] " + "Sending file attributes...");
        }
        else{
            System.err.println("[To " + clientAddr + ":" + clientPort + "] " + errorMessage);
        }
    }
}
