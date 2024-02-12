package server;
import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.io.ByteArrayOutputStream;

public class MonitorRequestHandler extends RequestHandler{
    String filePath;
    int monitorInterval;
    Calendar expiryTime;
    static HashMap<String, List<CallbackRecord>> callbackMap = new HashMap<String, List<CallbackRecord>>();

    public MonitorRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.MONITOR_FILE_REQUEST;
        expiryTime = Calendar.getInstance();
        expiryTime.add(Calendar.SECOND, monitorInterval);
    }

    public void unmarshalRequest(){
        int cur = 8;   // skip to first argument
        int filePathLength = Utils.unmarshalInt(request, cur); // get filePath length
        cur += 4;       // increment to start of filePath

        filePath = Utils.unmarshalString(request, cur, filePathLength); // get filePath
        cur += filePathLength; // increment to start of monitorInterval

        monitorInterval = Utils.unmarshalInt(request, cur); // get monitorInterval
        cur += 4;   // increment to start of numBytes
    }

    public void executeRequest(){
        try{
            ServerFile serverFile = new ServerFile(filePath);
            if (!serverFile.getFile().exists()){
                throw new FileNotFoundException();
            }
            reply = new byte[4];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.MONITOR_FILE_ACK), 0);

            // add client to appropriate callback list
            CallbackRecord callbackRecord = new CallbackRecord(clientAddr, clientPort, expiryTime);
            insertCallbackRecord(filePath, callbackRecord);

            // schedule deletion of callbackRecord
            Timer timer = new Timer();
            CallbackExpiration callbackExpiration = new CallbackExpiration(filePath, callbackRecord);   // create new expiration task
            timer.schedule(callbackExpiration, monitorInterval*1000);                                        // schedule expiration task
        }

        catch (FileNotFoundException e){    // return error if file cannot be found
            String errorString = "File at " + filePath + " could not be found.";
            reply = new byte[errorString.length() + 8];
            Utils.marshalErrorString(reply, errorString);
        }
    }

    public String toString(){
        return "\nMonitor File Request from " + clientAddr + ":" + clientPort + "\nRequest ID: " + requestID + "\nFile path: " + filePath + 
        "\nMonitor Interval: " + monitorInterval;
    }

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

    public static void deleteCallbackRecord(String filePath, CallbackRecord callbackRecord){
        List<CallbackRecord> callbackList = callbackMap.get(filePath);
        if (callbackList != null){
            callbackList.remove(callbackRecord);

            if (callbackList.isEmpty()){
                callbackMap.remove(filePath);
            }
        }
    }

    public static void notifyExpiration(String filePath, CallbackRecord callbackRecord){
        int cur = 0;
        byte[] notification = new byte[8 + filePath.length()];
        Utils.marshalInt(notification, HandlerNum.toInt(HandlerNum.MONITOR_FILE_EXPIRE), cur);  // add handle to notification
        cur += 4;   // increment index by 4

        Utils.marshalInt(notification, filePath.length(), cur);                                 // add filePath length to notification
        cur += 4;   // increment index by 4

        Utils.marshalString(notification, filePath, cur);                                       // add filePath to notification

        Server.udpServer.send(notification, notification.length, callbackRecord.clientAddr, callbackRecord.port);     // send expiry notification
    }

    public static void notifyUpdate(String filePath, ServerFile serverFile){
        List<CallbackRecord> callbackList = callbackMap.get(filePath);
        if (callbackList == null){
            System.out.println("Callbacklist is null");
            return;
        }

        int cur = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] fileData = serverFile.readAll();

        byte[] notificationHeader = new byte[filePath.length() + 12];
        Utils.marshalInt(notificationHeader, HandlerNum.toInt(HandlerNum.MONITOR_FILE_UPDATE), cur); // add handle to notification header
        cur += 4;   // increment index by 4

        Utils.marshalInt(notificationHeader, filePath.length(), cur);                                // add filePath length to notification header
        cur += 4;   // increment index by 4

        Utils.marshalString(notificationHeader, filePath, cur);                                      // add filePath to notification header
        cur += filePath.length();   // increment index by filePath length

        Utils.marshalInt(notificationHeader, fileData.length, cur);                                  // add fileData length to notification header

        try{
            outputStream.write(notificationHeader);
            outputStream.write(fileData);
            byte[] notification = outputStream.toByteArray();

            System.out.println("Notifying clients of updates: ");
            for (CallbackRecord callbackRecord : callbackList){
                System.out.println(callbackRecord);
                Server.udpServer.send(notification, notification.length, callbackRecord.clientAddr, callbackRecord.port);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
