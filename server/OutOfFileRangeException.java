package server;

public class OutOfFileRangeException extends Exception{
    public OutOfFileRangeException (String errorMessage){
        super(errorMessage);
    }
}
