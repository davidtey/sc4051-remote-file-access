package server;
import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeleteFromFileRequestHandler extends RequestHandler{
    String filePath;
    int offset;
    int numBytes;

    public DeleteFromFileRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.DELETE_FROM_FILE_REQUEST;
    }

    public void unmarshalRequest(){
        int cur = 8;   // skip to first argument
        int filePathLength = Utils.unmarshalInt(request, cur); // get filePath length
        cur += 4;       // increment to start of filePath

        filePath = Utils.unmarshalString(request, cur, filePathLength); // get filePath
        cur += filePathLength; // increment to start of offset

        offset = Utils.unmarshalInt(request, cur); // get offset
        cur += 4;   // increment to start of numBytes

        numBytes = Utils.unmarshalInt(request, cur);    // get numBytes
    }

    public void executeRequest(){
        try{
            ServerFile serverFile = new ServerFile(filePath);
            try{
                serverFile.delete(offset, numBytes);
                reply = new byte[4];
                Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.DELETE_FROM_FILE_ACK), 0);
                MonitorRequestHandler.notifyUpdate(filePath, serverFile);
            }
            catch (OutOfFileRangeException e){
                String errorString = "File offset exceeds the file length. Offset: " + offset + " File length: " + 
                serverFile.getFile().length();
    
                reply = new byte[errorString.length() + 8];
                Utils.marshalErrorString(reply, errorString);
            }
        }
        catch (FileNotFoundException e){
            String errorString = "File at " + filePath + " could not be found.";
            reply = new byte[errorString.length() + 8];
            Utils.marshalErrorString(reply, errorString);
        }
    }

    public String toString(){
        return "\nDelete from File Request from " + clientAddr + ":" + clientPort + "\nRequest ID: " + requestID + "\nFile path: " + filePath + 
        "\nOffset: " + offset + "\nNumber of bytes to delete: " + numBytes;
    }
}
