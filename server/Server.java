package server;
import java.util.Scanner;
import java.util.InputMismatchException;

/* Server class (main class)
 */
public class Server{
    private static Invocation invocation;
    public static void main(String[] args){
        initialMenu();

        /* 
        ServerFile serverFile = new ServerFile("server/database/file1.txt");
        String out = new String(serverFile.read(), Charset.forName("UTF-8"));
        System.out.println(out);

        serverFile.write("hello\n".getBytes(Charset.forName("UTF-8")), 6);*/
    }

    private static void initialMenu(){
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
                if (choice != 1 && choice != 2){
                    System.err.println("Please enter either 1 or 2!");
                    continue;
                }
            }
            catch(InputMismatchException e){
                System.err.println("Please enter either 1 or 2!");
                continue;
            }

            if (choice == 1){
                invocation = Invocation.AT_LEAST_ONCE;
            }
            else{
                invocation = Invocation.AT_MOST_ONCE;
            }
            validChoice = true;
            myScanner.close();
        }
    }
}
