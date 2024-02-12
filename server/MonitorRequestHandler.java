package server;
import java.net.InetAddress;
import java.io.FileNotFoundException;

public class MonitorRequestHandler extends RequestHandler{
    String filePath;
    int monitorInterval;

    public MonitorRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.MONITOR_FILE_REQUEST;
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
            reply = new byte[4];
            Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.MONITOR_FILE_ACK), 0);
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
}
