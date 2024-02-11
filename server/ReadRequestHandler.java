package server;

import java.net.SocketAddress;

public class ReadRequestHandler extends RequestHandler {
    String filePath;
    int offset;
    int numBytes;
    byte[] reply;

    public ReadRequestHandler(SocketAddress addr, int port, byte[] req){
        super(addr, port, req);
        requestType = HandlerNum.READ_FILE_HANDLER;
    }

    public void unmarshalRequest(){
        int cur = 20;   // skip to first argument
        int filePathLength = Utils.unmarshalInt(request, cur); // get filePath length
        cur += 4;       // increment to start of filePath

        filePath = Utils.unmarshalString(request, cur, filePathLength); // get filePath
        cur += filePathLength; // increment to start of offset

        offset = Utils.unmarshalInt(request, cur); // get offset
        cur += 4;   //increment to start of numBytes

        numBytes = Utils.unmarshalInt(request, cur);    // get numBytes
    }

    public void executeRequest(){

    }

    public void marshalReply(){

    }

    public String toString(){
        return "Read File Request -> " + "File path: " + filePath + " Offset: " + offset + " Number of bytes to read: " + numBytes;
    }
}
