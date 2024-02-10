package server;
import java.nio.charset.Charset;
import java.net.*;
import java.io.*;

public class Server{
    public static void main(String[] args){
        
        DatagramSocket socket = null;
        int port = 2222;
        
        try{
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            while (true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                
                System.out.println(new String(request.getData(), Charset.forName("UTF-8")));

                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
                                        request.getAddress(), request.getPort());
                socket.send(reply);
            }
        }
        catch (IOException e){
            return;
        }
        finally{
            if (socket != null){
                socket.close();
            }
        }
        

        /* 
        ServerFile serverFile = new ServerFile("server/database/file1.txt");
        String out = new String(serverFile.read(), Charset.forName("UTF-8"));
        System.out.println(out);

        serverFile.write("hello\n".getBytes(Charset.forName("UTF-8")), 6);*/
    }
}
