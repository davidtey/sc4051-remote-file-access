package server;

import java.net.InetAddress;

/* Request Handler class (abstract class)
 * Unmarshals requests, invoke methods and marshals replies
 */

public abstract class RequestHandler{
    InetAddress clientAddr;
    int clientPort;
    int requestID;
    HandlerNum requestType;
    byte[] request;
    byte[] reply;

    public RequestHandler(InetAddress addr, int port, int reqID, byte[] req){
        clientAddr = addr;
        clientPort = port;
        requestID = reqID;
        request = req;
    }

    public abstract void unmarshalRequest();
    public abstract void executeRequest();
}