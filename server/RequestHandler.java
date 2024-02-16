package server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**Request Handler Class
 * <pre>
 * Abstract class. Unmarshals requests, invoke methods and marshals replies.
 * Handles printing of requests and replies onto server console.
 * </pre>
 */
public abstract class RequestHandler{
    InetAddress clientAddr;             // Client IP address
    int clientPort;                     // Client port
    String addrString;                  // Client IP address + port
    int requestID;                      // Request ID
    HandlerNum requestType;             // Request Type
    byte[] request;                     // Request buffer
    byte[] reply;                       // Reply buffer
    boolean errorEncountered = false;   // Error catch
    int errorMessageLength;             // Error message length
    String errorMessage = "";           // Error message

    static List<RequestHandler> requestHistory = new ArrayList<RequestHandler>();   // Request history (list)

    /**Constructor for RequestHandler class
     * @param addr client IP address
     * @param port client port
     * @param reqID request ID
     * @param req byte array of incoming request
     */
    public RequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        clientAddr = addr;
        clientPort = port;
        requestID = reqID;
        request = req;
        addrString = clientAddr.toString() + Integer.toString(clientPort);
    }

    /**Handle generic incoming request, sorts request type and calls the appropriate request handler to create a reply message
     * @param request incoming request from UDP server
     * @return reply message in the form of byte array
     */
    public static RequestHandler handleIncomingRequest(DatagramPacket request){
        byte[] b = request.getData();                                                   // request byte array
        HandlerNum requestType = HandlerNum.fromInt(Utils.unmarshalInt(b, 0));    // get request type from request
        System.out.println("requestType: " + requestType);      // debug print

        int requestID = Utils.unmarshalInt(b, 4);         // get request ID
        System.out.println("requestID: " + requestID);          // debug print
        RequestHandler requestHandler;

        if (requestType == HandlerNum.READ_FILE_REQUEST){               // create read file request handler
            requestHandler = new ReadRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.WRITE_FILE_REQUEST){         // create write file request handler
            requestHandler = new WriteRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.MONITOR_FILE_REQUEST){       // create monitor file request handler
            requestHandler = new MonitorRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.DELETE_FROM_FILE_REQUEST){   // create delete from file request handler
            requestHandler = new DeleteFromFileRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.LIST_DIR_REQUEST){           // create list directory request handler
            requestHandler = new ListDirRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.GET_FILE_ATTR_REQUEST){      // create get file attribute request handler
            requestHandler = new GetFileAttrRequestHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else if (requestType == HandlerNum.NETWORK_PING){   // create netwrk ping handler
            requestHandler = new NetworkPingHandler(request.getAddress(), request.getPort(), requestID, b);
        }
        else{
            return null;
        }

        return requestHandler;
    }

    /**Main function of read request handler, calls necessary procedures in order and returns reply message.
     */
    public byte[] handleRequest(){
        unmarshalRequest();
        printRequest();
        executeRequest();
        marshalReply();
        printReply();
        return reply;
    }

    public abstract void unmarshalRequest();
    public abstract void printRequest();
    public abstract void executeRequest();
    public abstract void marshalReply();
    public abstract void printReply();
}