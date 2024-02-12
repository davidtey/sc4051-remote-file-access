package server;

import java.net.InetAddress;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class ReadRequestHandler extends RequestHandler {
    String filePath;
    int offset;
    int numBytes;

    public ReadRequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.READ_FILE_REQUEST;
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
                byte[] fileData = serverFile.readFile(offset, numBytes);
                byte[] header = new byte[8];
                Utils.marshalInt(header, HandlerNum.toInt(HandlerNum.READ_FILE_REPLY), 0);  // add handler number to reply message

                Utils.marshalInt(header, fileData.length, 4);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(header);
                outputStream.write(fileData);

                reply = outputStream.toByteArray();
            }
            catch (OutOfFileRangeException e){
                String errorString = "File offset exceeds the file length. Offset: " + offset + " File length: " + 
                serverFile.getFile().length();

                reply = new byte[errorString.length() + 8];
                Utils.marshalErrorString(reply, errorString);
            }
            catch (IOException e){
                return;
            }

        }
        catch (FileNotFoundException e){
            String errorString = "File at " + filePath + " could not be found.";
            reply = new byte[errorString.length() + 8];
            Utils.marshalErrorString(reply, errorString);
        }
    }

    public String toString(){
        return "\nRead File Request from " + clientAddr + ":" + clientPort + "\nRequest ID: " + requestID + "\nFile path: " + filePath + 
        "\nOffset: " + offset + "\nNumber of bytes to read: " + numBytes;
    }
}
