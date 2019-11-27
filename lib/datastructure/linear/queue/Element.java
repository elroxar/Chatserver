package lib.datastructure.linear.queue;

/**
 * Element einer Warteschlange
 *
 * @param <ContentType> Datentyp, welcher von der Warteschlange verwaltet wird
 * @author Stefan Christian Kohlmeier
 * @version 14.12.2018
 */
public class Element<ContentType>
{

    private Element<ContentType> zNachfolger;
    private ContentType zInhalt;

    /**
     * Erstellt ein leeres Element einer Warteschlange.
     */
    public Element()
    {

    }

    /**
     * Gibt den Inhalt des Elements.
     *
     * @return Inhalt
     */
    public ContentType gibInhalt()
    {
        return zInhalt;
    }

    /**
     * Gibt den Nachfolger des Elements.
     *
     * @return Nachfolger
     */
    public Element<ContentType> gibNachfolger()
    {
        return zNachfolger;
    }

    /**
     * Setzt den Inhalt des Elements.
     *
     * @param pInhalt Inhalt
     */
    public void setzeInhalt(ContentType pInhalt)
    {
        zInhalt = pInhalt;
    }

    /**
     * Setzt den Nachfolger des Elements.
     *
     * @param pNachfolger Nachfolger
     */
    public void setzeNachfolger(Element<ContentType> pNachfolger)
    {
        zNachfolger = pNachfolger;
    }
}
