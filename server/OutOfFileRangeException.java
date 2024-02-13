package server;

/**OutOfFileRangeException
 * <pre>
 * Used to catch instances where client tries to read byte that is outside of file range.
 * </pre>
 * 
 */
public class OutOfFileRangeException extends Exception{
    /**Empty constructor
     */
    public OutOfFileRangeException (){
        super();
    }

    /**Constructor with error message
     * @param errorMessage error message
     */
    public OutOfFileRangeException (String errorMessage){
        super(errorMessage);
    }
}
