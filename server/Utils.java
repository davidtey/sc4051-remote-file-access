package server;
import java.nio.charset.Charset;
import java.net.DatagramPacket;
import java.nio.ByteOrder;

public final class Utils {
    /* Generic request handler, gets request type, creates and calls the appropriate request handler.
     * Returns RequestHandler object for request/reply tracking
     */
    public static RequestHandler handleIncomingRequest(DatagramPacket request){
        byte[] b = request.getData();
        int cur = 0; // request bytearray index
        HandlerNum requestType = HandlerNum.fromInt(Utils.unmarshalInt(b, cur));    // get request type from request
        RequestHandler requestHandler;
        System.out.println("Request Handler: " + requestType);

        if (requestType == HandlerNum.READ_FILE_HANDLER){
            requestHandler = new ReadRequestHandler(request.getSocketAddress(), request.getPort(), b);
        }
        else{ // to do
            requestHandler = new ReadRequestHandler(request.getSocketAddress(), request.getPort(), b);
        }

        return requestHandler;
    }

    public static byte[] marshal(String s){
        return new byte[1];
    }

    /* Unmarshal 4 bytes into int
     * Assumes network transmits in big endian
     * Converts to host endian after
     */
    public static int unmarshalInt(byte[] b, int start){
        return b[start + 3] + b[start + 2]*255 + b[start + 1]*255*255 + b[start]*255*255*255;
    }

    /* Unmarshal bytes to string (assuming UTF-8)
     * Requires start index and string length
     * Returns string
     */
    public static String unmarshalString(byte[] b, int start, int length){
        byte[] c = new byte[length];
        for (int i=start; i<start+length; i++){
            c[i-start] = b[i];
        }
        return new String(c, Charset.forName("UTF-8"));
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
