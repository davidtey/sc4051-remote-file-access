package server;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class RequestHistory {
    private HashMap<String, List<RequestHandler>> requestHistory;

    public RequestHistory(){
        requestHistory = new HashMap<String, List<RequestHandler>>();
    }

    /**Get client request if it exists
     * 
     * @param addrString IP address & port of client
     * @param requestID request ID of client
     * @return client request (RequestHandler) if it exists in history, otherwise returns null
     */
    public RequestHandler getRequestIfExists(String addrString, int requestID){
        List<RequestHandler> clientReqHistory = requestHistory.get(addrString);
        if (clientReqHistory == null){
            return null;
        }

        for (RequestHandler clientReq : clientReqHistory){
            if (clientReq.requestID == requestID){
                return clientReq;
            }
        }
        return null;
    }

    /**Insert request handler to history
     * 
     * @param addrString IP address & port of client
     * @param requestHandler requestHandler of client request
     */
    public void insertRequest(String addrString, RequestHandler requestHandler){
        List<RequestHandler> clientReqHistory = requestHistory.get(addrString);
        if (clientReqHistory == null){
            clientReqHistory = new ArrayList<RequestHandler>();
        }
        clientReqHistory.add(requestHandler);
    }

    /**Delete request handler from histroy
     * 
     * @param addrString IP address & port of client
     * @param requestID request ID of client
     */
    public void deleteRequest(String addrString, int requestID){
        List<RequestHandler> clientReqHistory = requestHistory.get(addrString);
        if (clientReqHistory == null){
            return;
        }
        else{
            clientReqHistory.removeIf(r -> (r.requestID == requestID));
        }
    }
}
