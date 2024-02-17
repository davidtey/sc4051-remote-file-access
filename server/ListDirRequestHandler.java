package server;

import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.io.IOException;

/**List Directory Request Handler Class
 * <pre>
 * Handles read requests by unmarshalling request, invoking methods, marshalling reply and returning.
 * Handles printing of requests and replies onto server console.
 * </pre>
 */
public class ListDirRequestHandler extends RequestHandler{
    String filePath;            // request argument: file path

    int directoryDataLength;    // return: directory data length
    byte[] directoryData;       // return: directory data

    /**Constructor for ListDirRequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public ListDirRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.LIST_DIR_REQUEST;
    }

    /**Unmarshal list directory request according to predefined protocol.
     */
    public void unmarshalRequest(){
        int filePathLength = Utils.unmarshalInt(request, 8); // get filePath length
        filePath = Utils.unmarshalString(request, 12, filePathLength); // get filePath
    }

    /**Print read request on to server console.
     */
    public void printRequest(){
        System.out.println("[From " + clientAddr + ":" + clientPort + "] List Directory Request #" + requestID + " for " + filePath); 
    }

    /**Execute list directory request, invoke methods from Database.
     */
    public void executeRequest(){
        try{
            String fullFilePath = Database.databasePath + filePath;                 // add database path in front of file path
            directoryData = Database.listDirectory(fullFilePath);
            directoryDataLength = directoryData.length;
        }
        catch (FileNotFoundException e){                                        // handle if file not found
            errorEncountered = true;
            errorMessage = "Error encountered! File at " + filePath + " could not be found.";
            errorMessageLength = errorMessage.length();
        }
        catch (IOException e){                                                  // handle IOException
            errorEncountered = true;
            errorMessage = e.getMessage();
            errorMessageLength = errorMessage.length();
        }
    }

    /**Marshals read directory request reply
     * <pre>
     * Successful reply:
     * int (4 bytes): LIST_DIR_REPLY handler number
     * int (4 bytes): directory data length
     * String (n bytes): directory content
     * 
     * Error reply: 
     * int (4 bytes): ERROR_REPLY handler number
     * int (4 bytes): error message length
     * String (n bytes): error message
     * </pre>
     */
    public void marshalReply(){
        if (!errorEncountered){     // successful reply
            reply = new byte[directoryDataLength + 20];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.LIST_DIR_REPLY), 0);
            Utils.marshalInt(reply, directoryDataLength, 4);
            Utils.marshalBytes(reply, directoryData, 8);
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
            String printFilePath = filePath;
            if (filePath.equals(".")){
                printFilePath = "root";
            }
            System.out.println("[To " + clientAddr + ":" + clientPort + "] " + "Sending directory information at " + printFilePath + "...");
        }
        else{
            System.err.println("[To " + clientAddr + ":" + clientPort + "] " + errorMessage);
        }
    }
}
