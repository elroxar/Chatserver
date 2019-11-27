package lib.network.client;

/**
 * Dieser Thread wartet permanent auf Nachrichten vom Server und
 * ruft anschlie�end die Methode processMessage auf.
 */
public class Nachrichtenverarbeiter extends Thread
{
    private Client zClient;
    private Connection zVerbindung;
    private boolean zAktiv;

    /**
     * @param pClient     zugehoeriger Client, der die eingehenden Nachrichten bearbeitet
     * @param pConnection zugehoerige Connection, die die einkommenden Nachrichten empfaengt
     */
    public Nachrichtenverarbeiter(Client pClient, String pIPAdresse, int pPortNr)
    {
        zClient = pClient;
        zVerbindung = new Connection(pIPAdresse, pPortNr);
        zAktiv = true;
    }

    /**
     * Solange der Server Nachrichten sendet, werden diese empfangen und an die
     * Clientanwendung weitergereicht.
     */
    public void run()
    {
        String lNachricht;
        while (zAktiv)
        {
            lNachricht = zVerbindung.receive();//Warten ...            
            if (lNachricht != null)
            {
                zClient.processMessage(lNachricht);
            }
            else
            {
                zAktiv = false;
                zVerbindung.close();
            }
        }
    }

    public boolean istAktiv()
    {
        return zAktiv;
    }

    public void sende(String pMessage)
    {
        if (istAktiv())
        {
            zVerbindung.send(pMessage);
        }
    }

    public void beenden()
    {
        zAktiv = false;
        zVerbindung.close();
    }
}