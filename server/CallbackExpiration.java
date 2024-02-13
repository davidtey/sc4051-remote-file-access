package server;
import java.util.TimerTask;

/**CallbackExpiration Class:
 * <pre>
 * Extends TimerTask object. Used to schedule the expiration event of a CallbackRecord object. Deletes record and notifies client upon expiration.
 * </pre>
 */
public class CallbackExpiration extends TimerTask{
    String filePath;
    CallbackRecord callbackRecord;

    /**CallbackExpiration constructor
     * 
     * @param filepath path of file associated with CallbackRecord
     * @param c CallbackRecord object to be deleted
     */
    public CallbackExpiration(String filepath, CallbackRecord c){
        filePath = filepath;
        callbackRecord = c;
    }

    /**Overloads TimerTask.run(), this function is scheduled and called. Deletes record and notifies client. 
     */
    public void run(){
        MonitorRequestHandler.deleteCallbackRecord(filePath, callbackRecord);
        MonitorRequestHandler.notifyExpiration(filePath, callbackRecord);
    }
}
