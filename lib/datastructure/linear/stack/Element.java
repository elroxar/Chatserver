package lib.datastructure.linear.stack;

/**
 * Element eines Stapels
 *
 * @param <ContentType> Datentyp, welcher von dem Stapel verwaltet wird
 * @author Stefan Christian Kohlmeier
 * @version 30.11.2018
 */
public class Element<ContentType>
{

    private ContentType zInhalt;
    private Element<ContentType> zVorgaenger;

    /**
     * Erstellt ein leeres Element eines Stapels.
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
     * Setzt den Vorgänger des Elements.
     *
     * @param pVorgänger Vorgänger
     */
    public void setzeVorgänger(Element<ContentType> pVorgänger)
    {
        zVorgaenger = pVorgänger;
    }
}
