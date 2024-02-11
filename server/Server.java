package server;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.net.DatagramPacket;

/* Server class (main class)
 */
public class Server{

    private static Invocation invocation;
    public static void main(String[] args){
        invocationMenu();
        UDPServer udpServer = new UDPServer();
        DatagramPacket request;
        System.out.println("\n ----- Request log -----");

        while (true){
            // await request
            request = udpServer.receive();

            // handle request
            System.out.println(new String(request.getData(), Charset.forName("UTF-8"))); // test print

            // send reply
            udpServer.send(request.getData(), request.getLength(), request.getAddress(), request.getPort()); // test reply
        }

    }
    /* Server invocation menu
     * Allow user to select invocation semantics
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
