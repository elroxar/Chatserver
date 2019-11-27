package lib.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection
{
    private Socket zSocket;
    private BufferedReader zEingang;
    private PrintWriter zAusgang;

    /**
     * Es wird eine Verbindung zum durch IP-Adresse und Portnummer angegebenen
     * Server aufgebaut, so dass Daten gesendet und empfangen werden k�nnen.
     */
    public Connection(String pServerIP, int pPort)
    {
        try
        {
            zSocket = new Socket(pServerIP, pPort);
            zAusgang = new PrintWriter(zSocket.getOutputStream(), true);
            zEingang = new BufferedReader(new InputStreamReader(zSocket.getInputStream()));
        }
        catch (Exception e)
        {
            System.out.println("Fehler beim Anlegen des Connection-Objekts auf Clientseite");
            e.printStackTrace();
        }

    }

    /**
     * Es wird auf eine eingehende Nachricht vom Server gewartet und diese
     * Nachricht zur�ckgegeben, wobei der vom Server angeh�ngte Zeilentrenner
     * entfernt wird.
     */
    public String receive()
    {
        try
        {
            if (zEingang != null)
            {
                return zEingang.readLine();//Warten auf eine Nachricht ...
            }
        }
        catch (IOException e)
        {
            System.out.println("Client-Verbindung zu " + getRemoteIP() + " " + getRemotePort() + " ist unterbrochen.");
        }
        return null;
    }


    /**
     * Die angegebene Nachricht pNachricht wird - um einen Zeilentrenner erweitert -
     * an den Server gesendet.
     */
    public void send(String pNachricht)
    {
        if (zAusgang != null)
        {
            zAusgang.println(pNachricht);
            zAusgang.flush();//noch gepufferte Daten werden in den Stream geschrieben
        }
    }

    private boolean isConnected()
    {
        return zSocket.isConnected();
    }

    private boolean isClosed()
    {
        return zSocket.isClosed();
    }

    private String getRemoteIP()
    {
        return "" + zSocket.getInetAddress();
    }

    private int getRemotePort()
    {
        return zSocket.getPort();
    }

    public void close()
    {
        try
        {
            if (zSocket != null)
            {
                if (!zSocket.isClosed())
                {
                    zAusgang.close();
                    zEingang.close();
                    zSocket.close();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Client-Fehler beim Schliessen der Client-Sockets");
            e.printStackTrace();
        }
    }
}