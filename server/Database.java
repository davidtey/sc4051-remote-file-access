package server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.lang.Math;

/**Database Class: 
 * Allows reading, writing and deleting content within files in database.
 * Static methods only.
 */
public class Database {
    public static final String databasePath = new File("").getAbsolutePath().replace('\\', '/') 
                                                                                        + "/server/database/";              // default path to directory
    /**Reads from file in database
     * @param filePath path of queried file
     * @param offset integer offset from start of file
     * @param numBytes number of bytes to read
     * @return byte array of file content from offset to offset + numBytes
     */
    public static byte[] readFromFile(String filePath, int offset, int numBytes) throws FileNotFoundException, OutOfFileRangeException, IOException{
        if (!checkFileExists(filePath)){                // check if file exists
            throw new FileNotFoundException();
        }
        if (!checkOffsetInFileRange(filePath, offset)){ // check if offset is in file range
            throw new OutOfFileRangeException();
        }
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        int outLength = (int) Math.min(file.length() - offset, numBytes); // set byte array length to appropriate amount (clip if offset + numBytes exceed file length)
        byte[] out = new byte[outLength];    
        
        randomAccessFile.skipBytes(offset);             // skip until offset
        randomAccessFile.read(out, 0, outLength);   // read file content
        randomAccessFile.close();

        return out;
    }

    /**Writes to file in database
     * @param filePath path of queried file
     * @param offset integer offset from start of file
     * @insertString byte array of bytes to insert
     */
    public static void writeToFile(String filePath, int offset, byte[] insertString) throws FileNotFoundException, OutOfFileRangeException, IOException{
        if (!checkFileExists(filePath)){                // check if file exists
            throw new FileNotFoundException();
        }
        if (!checkOffsetInFileRange(filePath, offset)){ // check if offset is in file range
            throw new OutOfFileRangeException();
        }
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        byte[] before = new byte[offset];                                   // file content before start of insertion
        byte[] after = new byte[(int) file.length() - offset];              // file content after start of insertion
        
        fileInputStream.read(before, 0, offset);                        // get file content before start of insertion
        fileInputStream.read(after, 0, (int) file.length() - offset);   // get file content after start of insertion
        fileInputStream.close();                                            
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(before);                                         // write file content before start of insertion
        outputStream.write(insertString);                                   // write insertString to file
        outputStream.write(after);                                          // write file content after start of insertion

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(outputStream.toByteArray());                 // write everything to file
        fileOutputStream.close();
    }

    /**Delete from file in database
     * @param filePath path of queried file
     * @param offset integer offset from start of file
     * @param numBytes number of bytes to delete
     */
    public static void deleteFromFile(String filePath, int offset, int numBytes) throws FileNotFoundException, OutOfFileRangeException, IOException{
        if (!checkFileExists(filePath)){                // check if file exists
            throw new FileNotFoundException();
        }
        if (!checkOffsetInFileRange(filePath, offset)){ // check if offset is in file range
            throw new OutOfFileRangeException();
        }
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        int end = Math.min(offset + numBytes, (int) file.length() - 1);     // find last index to delete from
        byte[] before = new byte[offset];                                   // file content before start of deletion
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        fileInputStream.read(before, 0, offset);                        // get file content before start of deletion
        outputStream.write(before);                                         // write file content before start of deletion to output array

        if (end < file.length()){                                           // if end of deletion is not end of file, need to save file content after
            byte[] after = new byte[(int) file.length() - end];             // file content after end of deletion
            fileInputStream.readNBytes(numBytes);                           // skip until after end of deletion
            fileInputStream.read(after, 0, (int) file.length() - end);  // get file content after end of deletion
            outputStream.write(after);                                      // write file content after end of deletion
        }
        fileInputStream.close();
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(outputStream.toByteArray());                 // write everything to file
        fileOutputStream.close();
    }

    /**List directory of given path
     * @param filePath path of queried file
     * @return If path is a directory, function returns string of the list of folders and files within the directory. If path is a file, returns file content.
     */
    public static byte[] listDirectory(String filePath) throws FileNotFoundException, IOException{
        if (!checkPathExists(filePath)){
            throw new FileNotFoundException();
        }
        File file = new File(filePath);

        if (file.isDirectory()){                // if path is a directory
            String[] dirList = file.list();     // list of directories/files
            String out = "";
            for (String dir : dirList){         // iterate through directories/files
                out += dir + "\n";              // save directory/file to output
            }

            return out.getBytes();              // return string of list of folders and files
        }
        else{                                   // if path is a file
            return readAll(filePath);           // return file content
        }
    }

    /** Read whole content from file in database
     * @param filePath path of queried file
     * @return byte array of file content
     */
    public static byte[] readAll(String filePath) throws FileNotFoundException, IOException{
        if (!checkFileExists(filePath)){                // check if file exists
            throw new FileNotFoundException();
        }
        File file = new File(filePath);
        byte[] out = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(out);
        fileInputStream.close();

        return out;
    }

    /**Check if offset is in file range.
     * @param filePath path of queried file
     * @param offset integer offset from start of file
     * @return true if offset is within file range
     */
    public static boolean checkOffsetInFileRange(String filePath, int offset) throws FileNotFoundException, IOException{
        byte[] file = readAll(filePath);
        if (offset <= file.length){
            return true;
        }
        return false;
    }

    /**Check if file exists.
     * Automatically adds database directory '/server/database/' in front of file path.
     * @param filePath path of queried file
     * @return true if file exists in database
     */
    public static boolean checkFileExists(String filePath){
        File file = new File(filePath);
        if (file.exists() && file.isFile()){            // if path exists and is a file
            return true;
        }
        return false;
    }

    /**Check if path exists.
     * Path can be either directory or file.
     * @param filePath path of queried file
     * @return true if specified path is a directory or file
     */
    public static boolean checkPathExists(String filePath){
        File file = new File(filePath);
        if (file.exists()){                             // if path exists (can be directory/file)
            return true;
        }
        return false;
    }

    /**Get length of file contents
     * <pre>
     * Assumes file exists.
     * </pre>
     * @param filePath path of queried file 
     * @return length of file content
     */
    public static int getFileLength(String filePath){
        try{
            byte[] file = readAll(filePath);
            return file.length;
        }
        catch(IOException e){
            return -1;
        }
    }

    /**Get last modified time of file
     * @param filePath path of queried file 
     * @return long int representing time file was last modified, in milliseconds from UNIX epoch
     */
    public static long getFileLastModified(String filePath) throws FileNotFoundException{
        if (!checkFileExists(filePath)){                // check if file exists
            throw new FileNotFoundException();
        }
        File file = new File(filePath);

        return file.lastModified();
    }

    /**Set last modified time of file
     * @param filePath path of queried file
     * @param lastModified long int representing time in milliseconds from UNIX epoch
     */
    public static void setFileLastModified(String filePath, long lastModified) throws FileNotFoundException{
        if (!checkFileExists(filePath)){                // check if file exists
            throw new FileNotFoundException();
        }
        File file = new File(filePath);

        file.setLastModified(lastModified);
    }
}


