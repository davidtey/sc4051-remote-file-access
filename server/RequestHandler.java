package server;

import java.net.SocketAddress;

/* Request Handler class (abstract class)
 * Unmarshals requests, invoke methods and marshals replies
 */

public abstract class RequestHandler{
    SocketAddress clientAddr;
    int clientPort;
    HandlerNum requestType;
    byte[] request;

    public RequestHandler(SocketAddress addr, int port, byte[] req){
        clientAddr = addr;
        clientPort = port;
        request = req;
    }

    public abstract void unmarshalRequest();
    public abstract void executeRequest();
    public abstract void marshalReply();
}