package server;
import java.util.TimerTask;

public class CallbackExpiration extends TimerTask{
    String filePath;
    CallbackRecord callbackRecord;

    public CallbackExpiration(String filepath, CallbackRecord c){
        filePath = filepath;
        callbackRecord = c;
    }

    public void run(){
        MonitorRequestHandler.deleteCallbackRecord(filePath, callbackRecord);
        MonitorRequestHandler.notifyExpiration(filePath, callbackRecord);
    }
}
