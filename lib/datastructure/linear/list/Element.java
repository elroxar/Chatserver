package lib.datastructure.linear.list;

/**
 * Element einer Liste
 *
 * @param <ContentType> Datentyp, welcher verwaltet werden soll
 * @author Stefan Christian Kohlmeier
 * @version 21.11.2018
 */
public class Element<ContentType>
{

    private ContentType zInhalt;
    private Element<ContentType> zNachfolger, zVorgaenger;

    /**
     * Erstellt ein leeres Element einer Liste.
     */
    public Element()
    {

    }

    /**
     * Gibt den Inhalt zurück.
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
     * Gibt den Vorgänger des Elements.
     *
     * @return Vorgänger
     */
    public Element<ContentType> gibVorgänger()
    {
        return zVorgaenger;
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

    /**
     * Setzt den Vorgänger des Elements.
     *
     * @param pVorgaenger Vorgänger
     */
    public void setzeVorgänger(Element<ContentType> pVorgaenger)
    {
        zVorgaenger = pVorgaenger;
    }
}
