package server;
import java.nio.charset.Charset;
import java.net.DatagramPacket;
import java.nio.ByteOrder;

public final class Utils {
    /* Generic request handler, gets request type, creates and calls the appropriate request handler.
     * Returns RequestHandler object for request/reply tracking
     */
    private final static ByteOrder byteOrder = ByteOrder.nativeOrder();
    
    public static RequestHandler handleIncomingRequest(DatagramPacket request){
        byte[] b = request.getData();
        HandlerNum requestType = HandlerNum.fromInt(Utils.unmarshalInt(b, 0));    // get request type from request
        System.out.println("requestType: " + requestType);

        int requestID = Utils.unmarshalInt(b, 4);
        System.out.println("requestID: " + requestID);
        RequestHandler requestHandler;

        if (requestType == HandlerNum.READ_FILE_REQUEST){
            requestHandler = new ReadRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.WRITE_FILE_REQUEST){ 
            requestHandler = new WriteRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.MONITOR_FILE_REQUEST){
            requestHandler = new MonitorRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else{
            return null;
        }

        return requestHandler;
    }

    /* Marshal int
     * Returns byte array
     * Converts to big endian to transmit over network
     */
    public static void marshalInt(byte[] b, int x, int start){
        if (byteOrder.toString() == "BIG_ENDIAN"){
            b[start + 3] = (byte) (x >> Byte.SIZE * 3);
            b[start + 2] = (byte) (x >> Byte.SIZE * 2);   
            b[start + 1] = (byte) (x >> Byte.SIZE);   
            b[start] = (byte) x;
        }
        else{
            b[start] = (byte) (x >> Byte.SIZE * 3);
            b[start + 1] = (byte) (x >> Byte.SIZE * 2);   
            b[start + 2] = (byte) (x >> Byte.SIZE);   
            b[start + 3] = (byte) x;
        }
    }

    /* Marshal long int
     * Returns byte array
     * Converts to big endian to transmit over network
     */
    public static void marshalLong (byte[] b, long x, int start){
        if (byteOrder.toString() == "BIG_ENDIAN"){
            b[start + 7] = (byte) (x >> Byte.SIZE * 7);
            b[start + 6] = (byte) (x >> Byte.SIZE * 6);
            b[start + 5] = (byte) (x >> Byte.SIZE * 5);
            b[start + 4] = (byte) (x >> Byte.SIZE * 4);
            b[start + 3] = (byte) (x >> Byte.SIZE * 3);
            b[start + 2] = (byte) (x >> Byte.SIZE * 2);   
            b[start + 1] = (byte) (x >> Byte.SIZE);   
            b[start] = (byte) x;
        }
        else{
            b[start] = (byte) (x >> Byte.SIZE * 7);
            b[start + 1] = (byte) (x >> Byte.SIZE * 6);
            b[start + 2] = (byte) (x >> Byte.SIZE * 5);
            b[start + 3] = (byte) (x >> Byte.SIZE * 4);
            b[start + 4] = (byte) (x >> Byte.SIZE * 3);
            b[start + 5] = (byte) (x >> Byte.SIZE * 2);   
            b[start + 6] = (byte) (x >> Byte.SIZE);   
            b[start + 7] = (byte) x;
        }
    }

    /* Marshal string
     * String is added into b at index start
     */
    public static void marshalString(byte[] b, String s, int start){
        for (int i=start; i<s.length() + start; i++){
            b[i] = (byte) s.charAt(i-start);
        }
    }

    /* Marshal error string
     * Error string is added into b at index start
     */
    public static void marshalErrorString(byte[] b, String s){
        int cur = 0;
        marshalInt(b, HandlerNum.toInt(HandlerNum.ERROR_REPLY), cur);     // adds error_handler to start of byte array
        cur += 4;
        marshalInt(b, s.length(), cur); // adds error string length to byte array
        cur += 4;
        marshalString(b, s, cur);       // adds error string to byte array
    }

    /* Unmarshal 4 bytes into int
     * Assumes network transmits in big endian
     * Converts to host endian after
     */    
    public static int unmarshalInt(byte[] b, int start){
        int out;
        if (byteOrder.toString() == "BIG_ENDIAN"){
            out = (b[start + 3] << (Byte.SIZE * 3));
            out |= (b[start + 2] & 0xFF) << (Byte.SIZE * 2);
            out |= (b[start + 1] & 0xFF) << (Byte.SIZE * 1);
            out |= (b[start] & 0xFF);
            return out;
        }
        else{
            out = (b[start] << (Byte.SIZE * 3));
            out |= (b[start + 1] & 0xFF) << (Byte.SIZE * 2);
            out |= (b[start + 2] & 0xFF) << (Byte.SIZE * 1);
            out |= (b[start + 3] & 0xFF);
        }
        return out;
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
        
        char[] hexChars = new char[bytes.length*2];
        int offset = 0;
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2 + offset] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1 + offset] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars).replaceAll("(.{" + "8" + "})", "$1 ").trim();
    }
}
