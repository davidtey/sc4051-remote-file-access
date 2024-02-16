package server;

import java.net.InetAddress;

/**Network Ping Handler
 */
public class NetworkPingHandler extends RequestHandler{

    /**Constructor for MonitorRequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public NetworkPingHandler(InetAddress addr, int port, int reqID, byte[] req){
        super(addr, port, reqID, req);
        requestType = HandlerNum.NETWORK_PING;
    }
    
    public void unmarshalRequest(){
        return;
    }

    public void printRequest(){
        System.out.println("[From " + clientAddr + ":" + clientPort + "] Connection check request"); 
    }

    public void executeRequest(){
        return;
    }

    /**Marshals network ping reply
     * <pre>
     * Successful reply:
     * int (4 bytes): NETWORK_ACK handler number
     * </pre>
     */
    public void marshalReply(){
        reply = new byte[4];
        Utils.marshalInt(reply, HandlerNum.toInt(HandlerNum.NETWORK_ACK), 0);
    }

    public void printReply(){
        System.out.println("[To " + clientAddr + ":" + clientPort + "] " + "NETWORK_ACK");
    }
}
