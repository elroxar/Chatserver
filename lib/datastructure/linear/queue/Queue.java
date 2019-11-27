package lib.datastructure.linear.queue;

/**
 * Warteschlange zur Verwaltung von Daten
 *
 * @param <ContentType> Datentyp der zu verwaltenden Daten
 * @author Stefan Christian kohlmeier
 * @version 14.12.2018
 */
public class Queue<ContentType>
{

    private Element<ContentType> zKopf = new Element<>(), zEnde = new Element<>();

    /**
     * Erstellt eine leere Warteschlange.
     */
    public Queue()
    {
        zKopf.setzeNachfolger(zEnde);
    }

    /**
     * Entfernt Element, welches an der Reihe ist (Am Anfang der Warteschlange).
     */
    public void dequeue()
    {
        if (!isEmpty())
        {
            zKopf.setzeNachfolger(zKopf.gibNachfolger().gibNachfolger());
        }
    }

    /**
     * Fügt der Warteschlange ein Element hinzu
     *
     * @param pContent Element, welches an das Ende der Warteschlange hinzugefügt werden soll.
     */
    public void enqueue(ContentType pContent)
    {
        if (pContent != null)
        {
            zEnde.setzeInhalt(pContent);
            Element<ContentType> lEnde = new Element<>();
            zEnde.setzeNachfolger(lEnde);
            zEnde = lEnde;
        }
    }

    /**
     * Gibt das Element, welches an der Reihe ist.
     *
     * @return Element am Anfang der Warteschlange
     */
    public ContentType front()
    {
        if (isEmpty())
        {
            return null;
        }
        return zKopf.gibNachfolger().gibInhalt();
    }

    /**
     * Prüft, ob die Warteschlange leer ist.
     *
     * @return wenn die Warteschlange leer ist <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isEmpty()
    {
        return zKopf.gibNachfolger() == zEnde;
    }
}
