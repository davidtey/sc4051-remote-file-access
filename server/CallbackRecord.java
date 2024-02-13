package server;
import java.net.InetAddress;
import java.util.Calendar;

/**CallbackRecord class
 * <pre>
 * Record of the client's IP address, port and expiry time.
 * </pre>
 */
public class CallbackRecord {
    InetAddress clientAddr;
    int port;
    Calendar expiryTime;

    /**CallbackRecord constructor
     * @param addr client IP address
     * @param port client port
     * @param expiry expiry time
     */
    public CallbackRecord(InetAddress addr, int port, Calendar expiry){
        clientAddr = addr;
        this.port = port;
        expiryTime = expiry;
    }

    /**Overload equals operator to facilitate removal of CallbackRecord from list
     */
    public boolean equals(CallbackRecord c){
        if (this.clientAddr == c.clientAddr && this.port == c.port && this.expiryTime == c.expiryTime){
            return true;
        }
        else{
            return false;
        }
    }

    /**Overload printing function
     */
    public String toString(){
        String out = "Client: " + clientAddr.toString() + ":" + String.valueOf(port) + " Expiry: " + expiryTime.getTime().toString(); 
        return out;
    }
}
