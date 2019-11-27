package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;

public class Server extends  {

    private ServerSocket serverSocket;
    private HashSet<Socket> clientSockets;

    public Server(int port)
    {
        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
