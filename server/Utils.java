package server;
import java.nio.charset.Charset;
import java.nio.ByteOrder;

/**Generic utilities class
 * <pre>
 * Contains mashalling methods and hexadecimal printing. All methods are static.
 * </pre>
 * 
 */
public final class Utils {
    private final static ByteOrder byteOrder = ByteOrder.nativeOrder();         // local byte order (big/little endian ordering)
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();   // integer to hex conversion

    /**Marshal int
     * <pre>
     * Returns byte array
     * Converts to big endian to transmit over network
     * </pre>
     * @param b byte array to store marshalled data
     * @param x integer to marshal
     * @param start start index to store marshalled data
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

    /**Marshal long int
     * <pre>
     * Returns byte array
     * Converts to big endian to transmit over network
     * </pre>
     * @param b byte array to store marshalled data
     * @param x long integer to marshal
     * @param start start index to store marshalled data
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

    /**Marshal string
     * 
     * @param b byte array to store marshalled data
     * @param s string to marshal
     * @param start start index to store marshalled data
     */
    public static void marshalString(byte[] b, String s, int start){
        for (int i=start; i<s.length() + start; i++){
            b[i] = (byte) s.charAt(i-start);
        }
    }

    /**Marshal Bytes
     * @param b byte array buffer
     * @param in input byte array to marshal
     * @param start start index to store marshalled data
     */
    public static void marshalBytes(byte[] b, byte[] in, int start){
        for (int i=start; i<in.length + start; i++){
            b[i] = (byte) in[i-start];
        }
    }
    
    /**Marshal error string
     * 
     * @param b byte array buffer
     * @param s error string to marshal
     */
    public static void marshalErrorString(byte[] b, String s){
        marshalInt(b, HandlerNum.toInt(HandlerNum.ERROR_REPLY), 0);     // adds error_handler to start of byte array
        marshalInt(b, s.length(), 4); // adds error string length to byte array
        marshalString(b, s, 4);       // adds error string to byte array
    }

    /* Unmarshal 4 bytes into int
     * Assumes network transmits in big endian
     * Converts to host endian after
     */
    
    /**Unmarshal 4 bytes into int
     * <pre>
     * Assumes network transmits in big endian
     * Converts to host endian after
     * </pre>
     * @param b byte array buffer
     * @param start start index to unmarshal
     * @return unmarshalled integer
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

    /* Unmarshal 8 bytes into long
     * Assumes network transmits in big endian
     * Converts to host endian after
     */

    /**Unmarshal 8 bytes into long
     * <pre>
     * Assumes network transmits in big endian
     * Converts to host endian after
     * </pre>
     * @param b byte array buffer
     * @param start start index to unmarshal
     * @return unmarshalled long integer
     */
    public static long unmarshalLong(byte[] b, int start){
        long out;
        if (byteOrder.toString() == "BIG_ENDIAN"){
            out = (b[start + 7] << (Byte.SIZE * 7));
            out |= (b[start + 6] & 0xFF) << (Byte.SIZE * 6);
            out |= (b[start + 5] & 0xFF) << (Byte.SIZE * 5);
            out |= (b[start + 4] & 0xFF) << (Byte.SIZE * 4);
            out |= (b[start + 3] & 0xFF) << (Byte.SIZE * 3);
            out |= (b[start + 2] & 0xFF) << (Byte.SIZE * 2);
            out |= (b[start + 1] & 0xFF) << (Byte.SIZE * 1);
            out |= (b[start] & 0xFF);
            return out;
        }
        else{
            out = (b[start] << (Byte.SIZE * 7));
            out |= (b[start + 1] & 0xFF) << (Byte.SIZE * 6);
            out |= (b[start + 2] & 0xFF) << (Byte.SIZE * 5);
            out |= (b[start + 3] & 0xFF) << (Byte.SIZE * 4);
            out |= (b[start + 4] & 0xFF) << (Byte.SIZE * 3);
            out |= (b[start + 5] & 0xFF) << (Byte.SIZE * 2);
            out |= (b[start + 6] & 0xFF) << (Byte.SIZE * 1);
            out |= (b[start + 7] & 0xFF);
        }
        return out;
    }

    /**Unmarshal bytes to string (assuming UTF-8)
     * <pre>
     * Requires start index and string length
     * Returns string
     * </pre>
     * @param b byte array buffer
     * @param start start index to unmarshal
     * @param length length of string
     * @return unmarshalled string
     */
    public static String unmarshalString(byte[] b, int start, int length){
        byte[] c = new byte[length];
        for (int i=start; i<start+length; i++){
            c[i-start] = b[i];
        }
        return new String(c, Charset.forName("UTF-8"));
    }

    /**Bytes to Hex String
     * <pre>
     * Converts byte array to hex string.
     * </pre>
     * 
     * @param bytes input byte array
     * @return hex string
     */
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
