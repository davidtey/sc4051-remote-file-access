package server;
import java.net.*;
import java.io.IOException;

/**UDPServer Class
 * <pre>
 * Handles all UDP communication with UDPClients
 * </pre>
 */
public class UDPServer {
    private DatagramSocket socket;                // socket object
    private final int port = 2222;                // always runs on port 2222
    private final int BUFFER_SIZE = 1024;         // buffer size
    private final double LOSS_PROBABILITY = 0.2;  // probability of packet loss

    /**UDPServer Constructor
     */
    public UDPServer(){
        try{
            socket = new DatagramSocket(port);  // create server socket
        }
        catch (SocketException e){
            System.err.println("SocketException occurred while creating socket!");
        }
    }

    /**Receives next request (Waits for the next request to arrive)
     * returns DatagramPacket containing message (byte array), length, address and port
     */
    public DatagramPacket receive(){
        try{
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);
            System.out.println("Received bytes: " + Utils.bytesToHex(request.getData()));
            return request;
        }
        catch(IOException e){
            System.err.println("IOException occurred while receiving request!");
        }
        
        return null;
    }

    /**Send reply to client
     * 
     * @param buf message buffer (byte array)
     * @param length length of message
     * @param address client IP address
     * @param port client port
     */
    public void send(byte[] buf, int length, InetAddress address, int port){
        if (Math.random() < LOSS_PROBABILITY){
            System.out.println("Simulating reply packet loss...");
        }
        else{
            try{
                System.out.println("Sending Bytes: " + Utils.bytesToHex(buf));
                DatagramPacket reply = new DatagramPacket(buf, length, address, port);
                socket.send(reply);
            }
            catch (IOException e){
                System.err.println("IOException occurred while sending reply!");
            }
        }
    }
}
