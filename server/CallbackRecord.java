package server;
import java.net.InetAddress;
import java.util.Calendar;

public class CallbackRecord {
    InetAddress clientAddr;
    int port;
    Calendar expiryTime;

    public CallbackRecord(InetAddress addr, int port, Calendar expiry){
        clientAddr = addr;
        this.port = port;
        expiryTime = expiry;
    }

    /* Overload equals operator 
     * Allows removal of CallbackRecord from list
     */
    public boolean equals(CallbackRecord c){
        if (this.clientAddr == c.clientAddr && this.port == c.port && this.expiryTime == c.expiryTime){
            return true;
        }
        else{
            return false;
        }
    }

    public String toString(){
        String out = "Client: " + clientAddr.toString() + ":" + String.valueOf(port) + " Expiry: " + expiryTime.getTime().toString(); 
        return out;
    }
}
