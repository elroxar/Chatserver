package lib.network.client;

/**
 * Die eigentliche Client-Anwendung muss eine Kindklasse dieser
 * Klasse sein.
 *
 * @author T. Hammersen
 * @version Juli 2019
 */
public abstract class Client
{
    //Attribt horcht staendig, ob eine Nachricht vom Server kommt:
    private Nachrichtenverarbeiter zNachrichtenverarbeiter;

    /**
     * Der Client baut eine Verbindung zum Server auf.
     *
     * @param pIPAdresse Die IP-Adresse des Servers
     * @param pPortNr    Die Portnummer des Servers
     */
    public Client(String pServerIP, int pServerPort)
    {
        try
        {
            zNachrichtenverarbeiter = new Nachrichtenverarbeiter(this, pServerIP, pServerPort);
            zNachrichtenverarbeiter.start();//den horchenden Thread starten
        }
        catch (Exception pFehler)
        {
            System.err.println("Fehler beim Starten des Clients: " + pFehler);
        }
    }

    public boolean isConnected()
    {
        return zNachrichtenverarbeiter.istAktiv();
    }

    /**
     * Eine Zeichenkette an den Server senden
     */
    public void send(String pMessage)
    {
        if (isConnected())
        {
            zNachrichtenverarbeiter.sende(pMessage);
        }
    }

    /**
     * Eine Nachricht vom Server wird bearbeitet. Methode wird automatisch vom
     * Nachrichtenverarbeiter-Thread aufgerufen.
     * Diese abstrakte Methode muss in Unterklassen ueberschrieben werden.
     *
     * @param pNachricht Die empfangene Nachricht, die bearbeitet werden soll
     */
    public abstract void processMessage(String pMessage);

    /**
     * Die Verbindung zum Server wird geschlossen.
     */
    public void close()
    {
        zNachrichtenverarbeiter.beenden();
    }
}