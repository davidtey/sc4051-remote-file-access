package server;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.io.ByteArrayOutputStream;

/**Monitor Request Handler Class
 * <pre>
 * Handles monitor requests by unmarshalling request, invoking methods, marshalling reply and returning.
 * Handles printing of requests and replies onto server console.
 * </pre>
 */
public class MonitorRequestHandler extends RequestHandler{
    String filePath;                    // request argument: file path
    int monitorInterval;                // request argument: monitor interval

    Calendar expiryTime;                // internal variable: expiry time
    static HashMap<String, List<CallbackRecord>> callbackMap = new HashMap<String, List<CallbackRecord>>();     // Maps filePath to list of callback objects

    /**Constructor for MonitorRequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public MonitorRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.MONITOR_FILE_REQUEST;
        expiryTime = Calendar.getInstance();
        expiryTime.add(Calendar.SECOND, monitorInterval);
    }

    /**Unmarshal monitor request according to predefined protocol.
     */
    public void unmarshalRequest(){
        int filePathLength = Utils.unmarshalInt(request, 8);            // get filePath length
        filePath = Utils.unmarshalString(request, 12, filePathLength);  // get filePath
        monitorInterval = Utils.unmarshalInt(request, filePathLength + 12);   // get monitorInterval
    }

    /**Print monitor request on to server console.
     */
    public void printRequest(){
        System.out.println("[From " + clientAddr + ":" + clientPort + "] Monitor File Request #" + requestID + " for " + 
        filePath + " for " + monitorInterval + " seconds."); 
    }

    /**Execute monitor request, invoke methods from Database.
     */
    public void executeRequest(){                       // success
        String fullFilePath = Database.databasePath + filePath;
        if (Database.checkFileExists(fullFilePath)){
            // add client to appropriate callback list
            CallbackRecord callbackRecord = new CallbackRecord(clientAddr, clientPort, expiryTime);
            insertCallbackRecord(filePath, callbackRecord);

            // schedule deletion of callbackRecord
            Timer timer = new Timer();
            CallbackExpiration callbackExpiration = new CallbackExpiration(filePath, callbackRecord);       // create new expiration task
            timer.schedule(callbackExpiration, monitorInterval*1000);                                       // schedule expiration task
        }
        else{                                           // handle if file not found
            errorEncountered = true;
            errorMessage = "Error encountered! File at " + filePath + " could not be found.";
            errorMessageLength = errorMessage.length();
        }
    }

    /**Marshals monitor request reply
     * <pre>
     * Successful reply:
     * int (4 bytes): MONITOR_FILE_ACK handler number
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
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.MONITOR_FILE_ACK), 0);
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
            System.out.println("[To " + clientAddr + ":" + clientPort + "] " + "MONITOR_FILE_ACK");
        }
        else{
            System.err.println("[To " + clientAddr + ":" + clientPort + "] " + errorMessage);
        }
    }

    /**Insert callback record
     * <pre>
     * Insert callback record into hash map, which maps each file path to a list of callback records
     * </pre>
     * @param filePath path of monitored file
     * @param callbackRecord CallbackRecord object to be inserted
     */
    public static void insertCallbackRecord(String filePath, CallbackRecord callbackRecord){
        if (callbackMap.containsKey(filePath)){     // if list already exists
            List<CallbackRecord> callbackList = callbackMap.get(filePath);
            callbackList.add(callbackRecord);
        }
        else{                                       // else create a new list
            List<CallbackRecord> callbackList = new ArrayList<CallbackRecord>();
            callbackList.add(callbackRecord);
            callbackMap.put(filePath, callbackList);

            System.out.println(callbackMap.get(filePath).getFirst());
        }
    }

    /**Delete callback record
     * <pre>
     * Deletes callback record from hash map (to be called at expiry)
     * </pre>
     * @param filePath path of monitored file
     * @param callbackRecord CallbackRecord object to be deleted
     */
    public static void deleteCallbackRecord(String filePath, CallbackRecord callbackRecord){
        List<CallbackRecord> callbackList = callbackMap.get(filePath);
        if (callbackList != null){
            callbackList.remove(callbackRecord);

            if (callbackList.isEmpty()){
                callbackMap.remove(filePath);
            }
        }
    }

    /**Notify expiration of monitoring service
     * <pre>
     * Notify client upon expiration of monitoring service. Marshals and sends the reply to client directly.
     * 
     * int (4 bytes): MONITOR_FILE_EXPIRE handler number
     * int (4 bytes): file path length
     * string (n bytes): file path
     * 
     * Prints expiration notification on server console
     * </pre>
     * 
     * @param filePath path of monitored file
     * @param callbackRecord CallbackRecord object that has expired
     */
    public static void notifyExpiration(String filePath, CallbackRecord callbackRecord){
        byte[] notification = new byte[8 + filePath.length()];
        Utils.marshalInt(notification, HandlerNum.toInt(HandlerNum.MONITOR_FILE_EXPIRE), 0);  // add handle to notification
        Utils.marshalInt(notification, filePath.length(), 4);                                 // add filePath length to notification
        Utils.marshalString(notification, filePath, 8);                                       // add filePath to notification
        Server.udpServer.send(notification, notification.length, callbackRecord.clientAddr, callbackRecord.port);     // send expiry notification
        System.out.println("[To " + callbackRecord.clientAddr + ":" + callbackRecord.port + "] "+ "MONITOR_FILE_EXPIRE for " + 
        filePath);
    }

    /**Notify update
     * <pre>
     * Notifies all clients who are monitoring specified file of an update.
     * 
     * int (4 bytes): MONITOR_FILE_UPDATE handler number
     * int (4 bytes): file path length
     * string (n bytes): file path
     * int (4 bytes): file content length
     * string (m bytes): file content
     * 
     * Prints notification information on server console.
     * </pre>
     * 
     * @param filePath path of monitored file
     */
    public static void notifyUpdate(String filePath){
        String fullFilePath = Database.databasePath + filePath;
        List<CallbackRecord> callbackList = callbackMap.get(filePath);
        if (callbackList == null){  // callback list is empty, no need to update
            return;
        }

        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] notificationHeader = new byte[filePath.length() + 12];
            byte[] fileContent = Database.readAll(fullFilePath);

            Utils.marshalInt(notificationHeader, HandlerNum.toInt(HandlerNum.MONITOR_FILE_UPDATE), 0); // add handle to notification header
            Utils.marshalInt(notificationHeader, filePath.length(), 4);                                // add filePath length to notification header
            Utils.marshalString(notificationHeader, filePath, 8);                                      // add filePath to notification header
            Utils.marshalInt(notificationHeader, fileContent.length, filePath.length() + 8);                 // add fileData length to notification header
            
            outputStream.write(notificationHeader);             // add notification header to output
            outputStream.write(fileContent);                    // add file content to output
            byte[] notification = outputStream.toByteArray();   // create notification byte array

            for (CallbackRecord callbackRecord : callbackList){
                System.out.println("[To " + callbackRecord.clientAddr + ":" + callbackRecord.port + "] " + "Update detected in " + 
                filePath + "! Sending updated file...");
                Server.udpServer.send(notification, notification.length, callbackRecord.clientAddr, callbackRecord.port);
            }
        }
        catch (IOException e){          // catch error on server side only, no need to send to client as this reply is not for an explicit request
            e.printStackTrace();
        }
    }
}
