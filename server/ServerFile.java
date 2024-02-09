package server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ServerFile {
    private String path;
    private File file;

    public ServerFile(String p){
        path = p;
        try{
            file = new File(p);
            if (!file.exists()){
                throw new FileNotFoundException();
            }
        }
        catch (FileNotFoundException e){
            System.out.println("File " + path + " not found.");
            e.printStackTrace();
        }
        
    }

    public byte[] read(){
        byte[] out = new byte[(int) file.length()];
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            fileInputStream.read(out);
        }

        catch (IOException e){
        }

        return out;
    }

    public void write(byte[] in, int offset){
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] before = new byte[offset];
            byte[] after = new byte[(int) file.length() - offset];
            
            fileInputStream.read(before, 0, offset);
            fileInputStream.read(after, 0, (int) file.length() - offset);
            fileInputStream.close();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(before);
            outputStream.write(in);
            outputStream.write(after);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.close();

            System.out.println("Successfully wrote to the file " + path);
        }
        catch (IOException e){
            System.err.println("File " + path + " not found.");
            e.printStackTrace();
        }
    }
}
