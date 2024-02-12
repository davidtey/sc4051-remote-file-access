package server;

import java.net.InetAddress;
import java.io.FileNotFoundException;

public class WriteRequestHandler extends RequestHandler{
    String filePath;
    int offset;
    String insertString;

    public WriteRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.WRITE_FILE_REQUEST;
    }

    public void unmarshalRequest(){
        int cur = 8;   // skip to first argument
        int filePathLength = Utils.unmarshalInt(request, cur); // get filePath length
        System.out.println("filePathLength: " + filePathLength);
        cur += 4;       // increment to start of filePath

        filePath = Utils.unmarshalString(request, cur, filePathLength); // get filePath
        System.out.println("filePath: " + filePath);
        cur += filePathLength; // increment to start of offset

        offset = Utils.unmarshalInt(request, cur); // get offset
        System.out.println("offset: " + offset);
        cur += 4;   // increment to start of numBytes

        int insertStringLength = Utils.unmarshalInt(request, cur);  // get insertString length
        System.out.println("insertStringLength: " + insertStringLength);
        cur += 4;   // increment to start of insertString

        insertString = Utils.unmarshalString(request, cur, insertStringLength);
        System.out.println("insertString: " + insertString);
    }

    public void executeRequest(){
        try{
            ServerFile serverFile = new ServerFile(filePath);
            try{
                serverFile.write(insertString.getBytes(), offset);
                reply = new byte[4];
                Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.INSERTION_ACK), 0);
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
        return "\nWrite File Request from " + clientAddr + ":" + clientPort + "\nRequest ID: " + requestID + "\nFile path: " + filePath + 
        "\nOffset: " + offset + "\nString to insert: " + insertString;
    }
}
