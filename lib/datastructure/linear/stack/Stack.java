package lib.datastructure.linear.stack;

/**
 * Stapel zur Verwaltung von Daten
 *
 * @param <ContentType> Datentyp der zu verwaltenden Daten
 * @author Stefan Christian Kohlmeier
 * @version 30.11.2018
 */
public class Stack<ContentType>
{

    private Element<ContentType> zEnde = new Element<>();

    /**
     * Erstellt einen leeren Stapel.
     */
    public Stack()
    {

    }

    /**
     * Prüft, ob der Stapel leer ist.
     *
     * @return wenn der Stapel leer ist <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isEmpty()
    {
        return zEnde.gibVorgänger() == null;
    }

    /**
     * Löscht das oberste Element des Stapels, wenn er gefüllt ist.
     */
    public void pop()
    {
        if (!isEmpty())
        {
            zEnde.setzeVorgänger(zEnde.gibVorgänger().gibVorgänger());
        }
    }

    /**
     * Legt ein Element auf den Stapel, wenn es <code>!= null</code> ist.
     *
     * @param pContent Element
     */
    public void push(ContentType pContent)
    {
        if (pContent != null)
        {
            Element<ContentType> lContent = new Element<>();
            lContent.setzeInhalt(pContent);
            lContent.setzeVorgänger(zEnde.gibVorgänger());
            zEnde.setzeVorgänger(lContent);
        }
    }

    /**
     * Gibt das oberste Element des Stapels.
     *
     * @return wenn der Stapel leer ist <code>null</code>, ansonsten das oberste Element
     */
    public ContentType top()
    {
        if (!isEmpty())
        {
            return zEnde.gibVorgänger().gibInhalt();
        }
        return null;
    }
}
