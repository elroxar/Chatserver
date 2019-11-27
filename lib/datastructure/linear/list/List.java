package lib.datastructure.linear.list;

/**
 * Liste zur Verwaltung von Daten
 *
 * @param <ContentType> Datentyp der zu verwaltenden Daten
 * @author Stefan Christian Kohlmeier
 * @version 21.11.2018
 */
public class List<ContentType>
{

    private Element<ContentType> zKopf = new Element<>(),
            zPositionszeiger = zKopf,
            zEnde = new Element<>();

    /**
     * Erstellt eine leere Liste.
     */
    public List()
    {
        zKopf.setzeNachfolger(zEnde);
        zEnde.setzeVorgänger(zKopf);
    }

    /**
     * Fügt <code>pContent</code> der Liste hinzu, wenn es <code>!= null</code> ist.
     *
     * @param pContent Element
     */
    public void append(ContentType pContent)
    {
        if (pContent != null)
        {
            Element<ContentType> lNeu = new Element<>();
            lNeu.setzeInhalt(pContent);
            zEnde.gibVorgänger().setzeNachfolger(lNeu);
            lNeu.setzeVorgänger(zEnde.gibVorgänger());
            lNeu.setzeNachfolger(zEnde);
            zEnde.setzeVorgänger(lNeu);
        }
    }

    /**
     * Fügt eine andere Liste an das Ende der Liste hinzu. Wenn <code>pList== null</code> oder die selbe wie.
     * die jetzige Liste ist, geschieht nichts.
     *
     * @param pList Liste, welche ans Ende hinzugefügt werden soll
     */
    public void concat(List<ContentType> pList)
    {
        if (pList != null && pList != this)
        {
            zEnde.gibVorgänger().setzeNachfolger(pList.zKopf.gibNachfolger());
            pList.zKopf.gibNachfolger().setzeVorgänger(zEnde.gibVorgänger());
            zEnde.setzeVorgänger(pList.zEnde.gibVorgänger());
            pList.zEnde.gibVorgänger().setzeNachfolger(zEnde);
            pList.zKopf.setzeNachfolger(pList.zEnde);
            pList.zEnde.setzeVorgänger(pList.zKopf);
        }
    }

    /**
     * Gibt den Inhalt des aktuellen Elements.
     *
     * @return Inhalt an der Stelle des aktuellen Elements
     */
    public ContentType getContent()
    {
        return hasAccess() ? zPositionszeiger.gibInhalt() : null;
    }

    /**
     * Setzt den Inhalt des aktuellen Elements, wenn der zu setzende Inhalt <code>!= null</code> ist.
     *
     * @param pContent zu setzender Inhalt
     */
    public void setContent(ContentType pContent)
    {
        if (pContent != null && hasAccess())
        {
            zPositionszeiger.setzeInhalt(pContent);
        }
    }

    /**
     * Prüft, ob es ein aktuelles Element gibt.
     *
     * @return ob es ein aktuelles Element gibt
     */
    public boolean hasAccess()
    {
        return zPositionszeiger != zKopf && zPositionszeiger != zEnde;
    }

    /**
     * Fügt ein Element an der Stelle vor dem aktuellen Element hinzu, wenn es ein aktuelles Element gibt. Wenn die
     * Liste leer ist, wird das Element der Liste hinzugefügt, ansonsten passiert nichts.
     *
     * @param pContent Element, welches der Liste hinzugefügt werden soll
     */
    public void insert(ContentType pContent)
    {
        if (pContent == null || (!isEmpty() && !hasAccess()))
        {
            ;  // Nichts machen
        }
        else
        {
            if (isEmpty())
            {
                zPositionszeiger = zEnde;
            }
            Element<ContentType> lNeu = new Element<ContentType>();
            lNeu.setzeInhalt(pContent);
            lNeu.setzeNachfolger(zPositionszeiger);
            lNeu.setzeVorgänger(zPositionszeiger.gibVorgänger());
            lNeu.gibVorgänger().setzeNachfolger(lNeu);
            zPositionszeiger.setzeVorgänger(lNeu);
        }
    }

    /**
     * Prüft, ob die Liste leer ist.
     *
     * @return wenn die Liste leer ist, wird <code>true</code> zurückgegeben, ansonsten <code>false</code>
     */
    public boolean isEmpty()
    {
        return zKopf.gibNachfolger() == zEnde;
    }

    /**
     * Der Nachfolger des aktuellen Elements wird zum aktuellen Element
     */
    public void next()
    {
        if (hasAccess())
        {
            zPositionszeiger = zPositionszeiger.gibNachfolger();
        }
    }

    /**
     * Entfernt das aktuelle Element, das darauf #+folgende Element wird zum aktuellen Element.
     */
    public void remove()
    {
        if (hasAccess())
        {
            zPositionszeiger.gibVorgänger().setzeNachfolger(zPositionszeiger.gibNachfolger());
            zPositionszeiger.gibNachfolger().setzeVorgänger(zPositionszeiger.gibVorgänger());
            zPositionszeiger = zPositionszeiger.gibNachfolger();
        }
    }

    /**
     * Wenn die Liste gefüllt ist, wird das erste ELement zum aktuellen Element.
     */
    public void toFirst()
    {
        if (!isEmpty())
        {
            zPositionszeiger = zKopf.gibNachfolger();
        }
    }

    /**
     * Wenn die Liste gefüllt ist, wird das letzte Element zum aktuellen Element.
     */
    public void toLast()
    {
        if (!isEmpty())
        {
            zPositionszeiger = zEnde.gibVorgänger();
        }
    }
}
