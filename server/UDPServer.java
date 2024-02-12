package server;
import java.net.*;
import java.io.IOException;

/* UDPServer Class
 * Handles all UDP communication with UDPClients
 */
public class UDPServer {
    private DatagramSocket socket;
    private int port = 2222;
    private final int BUFFER_SIZE = 1024;

    /* UDPServer Constructor
     */
    public UDPServer(){
        try{
            socket = new DatagramSocket(port);  // create server socket
        }
        catch (SocketException e){
            System.err.println("SocketException occurred while creating socket!");
        }
    }

    /* Receives next request (Waits for the next request to arrive)
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

    /* Sends reply to client
     * Requires message (byte array), length, address and port
     */
    public void send(byte[] buf, int length, InetAddress address, int port){
        System.out.println("Sending Bytes: " + Utils.bytesToHex(buf));
        DatagramPacket reply = new DatagramPacket(buf, length, address, port);
        try{
            socket.send(reply);
        }
        catch (IOException e){
            System.err.println("IOException occurred while sending reply!");
        }
    }
}
