package server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.lang.Math;

public class ServerFile {
    private static final String databasePath = new File("").getAbsolutePath().replace('\\', '/') 
                                                                                        + "/server/database/";
    private String path;
    private File file;

    public ServerFile(String p) throws FileNotFoundException{
        path = p;

        file = new File(databasePath + path);
        if (!file.exists()){
            System.out.println("File at path " + path + " not found.");
            throw new FileNotFoundException();
        }
    }

    public byte[] readFile(int offset, int numBytes) throws OutOfFileRangeException{
        int outLength = (int) Math.min(file.length() - offset, numBytes);
        if (outLength <= 0){
            String errorString = "File offset exceeds the file length. Offset: " + offset + " File length: " + file.length();
            throw new OutOfFileRangeException(errorString);
        }

        try{
            byte[] out = new byte[outLength];
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.skipBytes(offset);
            randomAccessFile.read(out, 0, outLength);
            randomAccessFile.close();
            return out;
        }
        catch (IOException e){
            return null;
        }
    }

    public byte[] readAll(){
        byte[] out = new byte[(int) file.length()];
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            fileInputStream.read(out);
        }

        catch (IOException e){
            e.printStackTrace();
        }

        return out;
    }

    public byte[] listDir(){
        String[] dirList = file.list();
        String out = "";
        for (String dir : dirList){
            out += dir + "\n";
        }

        return out.getBytes();
    }

    public void write(byte[] in, int offset) throws OutOfFileRangeException{
        if (offset > file.length()){
            String errorString = "File offset exceeds the file length. Offset: " + offset + " File length: " + file.length();
            throw new OutOfFileRangeException(errorString);
        }

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
            e.printStackTrace();
        }
    }

    public void delete(int offset, int numBytes) throws OutOfFileRangeException{
        if (offset > file.length()){
            String errorString = "File offset exceeds the file length. Offset: " + offset + " File length: " + file.length();
            throw new OutOfFileRangeException(errorString);
        }

        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            int end = Math.min(offset + numBytes, (int) file.length() - 1);
            byte[] before = new byte[offset];
            byte[] after;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            fileInputStream.read(before, 0, offset);
            outputStream.write(before);
            if (end < file.length()){
                after = new byte[(int) file.length() - end];
                fileInputStream.readNBytes(numBytes);
                fileInputStream.read(after, 0, (int) file.length() - end);
                outputStream.write(after);
            }
            fileInputStream.close();
            
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.close();
            
            System.out.println("Successfully deleted from the file " + path);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public File getFile(){
        return file;
    }
}


