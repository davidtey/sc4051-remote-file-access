package server;

import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class ListDirRequestHandler extends RequestHandler{
    String filePath;

    public ListDirRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.LIST_DIR_REQUEST;
    }

    public void unmarshalRequest(){
        int cur = 8;   // skip to first argument
        int filePathLength = Utils.unmarshalInt(request, cur); // get filePath length
        cur += 4;       // increment to start of filePath

        filePath = Utils.unmarshalString(request, cur, filePathLength); // get filePath
        cur += filePathLength; // increment to start of offset
    }

    public void executeRequest(){
        try{
            ServerFile serverFile = new ServerFile(filePath);
            if (serverFile.getFile().isFile()){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] fileData = serverFile.readAll();
                byte[] header = new byte[8];
                Utils.marshalInt(header, HandlerNum.toInt(HandlerNum.LIST_DIR_REPLY), 0);  // add handler number to reply message

                Utils.marshalInt(header, fileData.length, 4);

                outputStream.write(header);
                outputStream.write(fileData);

                reply = outputStream.toByteArray();
            }
            else if (serverFile.getFile().isDirectory()){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] fileData = serverFile.listDir();
                byte[] header = new byte[8];
                Utils.marshalInt(header, HandlerNum.toInt(HandlerNum.LIST_DIR_REPLY), 0);  // add handler number to reply message

                Utils.marshalInt(header, fileData.length, 4);

                outputStream.write(header);
                outputStream.write(fileData);

                reply = outputStream.toByteArray();
            }
        }
        catch (FileNotFoundException e){
            String errorString = "Directory at " + filePath + " could not be found.";
            reply = new byte[errorString.length() + 8];
            Utils.marshalErrorString(reply, errorString);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public String toString(){
        return "\nList Directory Request from " + clientAddr + ":" + clientPort + "\nRequest ID: " + requestID + "\nFile path: " + filePath;
    }
}
