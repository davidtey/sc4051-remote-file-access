package server;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.net.DatagramPacket;

/**Server class
 * <pre>
 * Main class with main while loop receiving requests, handling requests and replying clients. Also implements the invocation semantics menu. 
 * </pre>
 */
public class Server{
    private static Invocation invocation;
    static UDPServer udpServer = new UDPServer();
    
    /** Main loop
     * 
     * @param args currently unused
     */
    public static void main(String[] args){
        invocationMenu();
        DatagramPacket request;
        System.out.println("\n ---------- Request log ----------");

        while (true){
            request = udpServer.receive();                                                // await request
            byte[] reply = RequestHandler.handleIncomingRequest(request);                 // handle request
            udpServer.send(reply, reply.length, request.getAddress(), request.getPort()); // reply to client
        }
    }

    /**Server invocation menu to allow host to select invocation semantics
     */
    private static void invocationMenu(){
        boolean validChoice = false;
        Scanner myScanner = new Scanner(System.in);
        int choice;

        while (!validChoice){
            System.out.println("\n===== Remove File Access System Server =====");
            System.out.println("1. At-least-once invocation semantics");
            System.out.println("2. At-most-once invocation semantics");
            System.out.print("Please choose the invocation semantics: ");
            
            try{
                choice = myScanner.nextInt();
                if (choice != 1 && choice != 2){    // catch invalid integer input
                    System.err.println("Please enter either 1 or 2!");
                    continue;
                }
            }
            catch(InputMismatchException e){        // catch non integer input
                System.err.println("Please enter either 1 or 2!");
                continue;
            }

            if (choice == 1){
                invocation = Invocation.AT_LEAST_ONCE;
            }
            else{
                invocation = Invocation.AT_MOST_ONCE;
            }

            System.out.println(invocation + " invocation semantics has been selected.");

            validChoice = true;
            myScanner.close();
        }
    }
}
