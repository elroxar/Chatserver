package lib.datastructure.nonlinear.tree;

import libary.util.ComparableContent;

/**
 * binärer Suchbaum zur Verwaltung von Daten entsprechend einer Ordnungsrelation
 * (links von der Wurzel: Element kleiner als die Wurzel, rechts: Element größer
 * als die Wurzel)
 *
 * @param <ContentType> Datentyp der zu verwaltenden Daten
 * @author Stefan Christian Kohlmeier
 * @version 05.02.2019
 */
public class BinarySearchTree<ContentType extends ComparableContent<ContentType>>
{

    private ContentType zInhalt;
    private BinarySearchTree<ContentType> zLinkerSohn, zRechterSohn;

    /**
     * Erstellt einen leeren binären Suchbaum.
     */
    public BinarySearchTree()
    {

    }

    /**
     * Gibt den Inhalt des Baumes.
     *
     * @return Inhalt
     */
    public ContentType getContent()
    {
        return zInhalt;
    }

    /**
     * Gibt den linken Knoten des Baumes.
     *
     * @return linker Knoten
     */
    public BinarySearchTree<ContentType> getLeftTree()
    {
        return zLinkerSohn;
    }

    /**
     * Gibt den rechten Knoten des Baumes.
     *
     * @return rechter Knoten
     */
    public BinarySearchTree<ContentType> getRightTree()
    {
        return zRechterSohn;
    }

    /**
     * Setzt pContent als Inhalt des Baumes, wenn es <code>!= null</code> ist und noch nicht im Baum vorhanden ist.
     *
     * @param pContent Inhalt
     */
    public void insert(ContentType pContent)
    {
        if (pContent != null)
        {
            BinarySearchTree<ContentType> temp = this;
            while (!temp.isEmpty())
            {   // iteratives suchen --> siehe search
                if (temp.zInhalt.isLess(pContent))
                {
                    temp = temp.getRightTree();
                }
                else if (temp.zInhalt.isGreater(pContent))
                {
                    temp = temp.getLeftTree();
                }
                else
                {
                    return; // wenn pContent schon vorhanden ist, nichts machen
                }
            }
            temp.zInhalt = pContent;
            temp.zLinkerSohn = new BinarySearchTree<>();
            temp.zRechterSohn = new BinarySearchTree<>();
        }
    }

    /**
     * Prüft, ob der Baum leer ist.
     *
     * @return wenn der Baum leer ist <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isEmpty()
    {
        return zInhalt == null;
    }

    /**
     * Entfernt den Inhalt pContent aus dem Baum, wenn pContent <code>!= null</code> ist.
     *
     * @param pContent Inhalt, der entfernt werden soll
     */
    public void remove(ContentType pContent)
    {
        if (pContent != null)
        {
            BinarySearchTree<ContentType> temp = this;
            while (!temp.isEmpty())
            {
                if (temp.zInhalt.isGreater(pContent))
                { // richtigwertiger Knoten wird gesucht --> siehe search
                    temp = temp.zLinkerSohn;
                }
                else if (temp.zInhalt.isLess(pContent))
                {
                    temp = temp.zRechterSohn;
                }
                else
                {
                    if (!temp.zLinkerSohn.isEmpty() && !temp.zRechterSohn.isEmpty())
                    { // wenn der zu löschende Knoten
                        // (temp) einen linken und rechten Knoten hat
                        if (temp.zRechterSohn.zLinkerSohn.isEmpty())
                        { // rechter Nachfolger hat keinen linken Nachfolger
                            temp.zInhalt = temp.zRechterSohn.zInhalt;
                            temp.zRechterSohn = temp.zRechterSohn.zRechterSohn;
                        }
                        else
                        { // rechter Nachfolger hat einen linken Nachfolger --> um 1 "aufrutschen"
                            /* 10 soll gelöscht werden, dafür wird es durch 7, dem symetrischen Nachfolger, ersetzt
                                5
                           4        10
                                  9    11
                                7
                                 8
                         */
                            BinarySearchTree<ContentType> minVorg = temp.zRechterSohn;
                            while (!minVorg.zLinkerSohn.zLinkerSohn.isEmpty())
                            { // den Vorgänger des kleinsten
                                // symetrischen Nachfolgers von temp suchen
                                minVorg = minVorg.zLinkerSohn;
                            }
                            temp.zInhalt = minVorg.zLinkerSohn.zInhalt;
                            minVorg.zLinkerSohn = minVorg.zLinkerSohn.zRechterSohn;
                        }
                    }
                    else if (!temp.zLinkerSohn.isEmpty())
                    { // wenn es nur einen linken Knoten gibt,
                        // temp mit diesem ersetzen
                        temp.zInhalt = temp.zLinkerSohn.zInhalt;
                        temp.zLinkerSohn = temp.zLinkerSohn.zLinkerSohn;
                    }
                    else if (!temp.zRechterSohn.isEmpty())
                    { // wenn es nur einen rechten Knoten gibt,
                        // temp mit diesem ersetzen
                        temp.zInhalt = temp.zRechterSohn.zInhalt;
                        temp.zRechterSohn = temp.zRechterSohn.zRechterSohn;
                    }
                    else
                    {    // wenn es keinen rechten sowie linken Sohn gibt, temp zu einem leeren Knoten machen
                        temp.zInhalt = null;
                        temp.zLinkerSohn = null;
                        temp.zRechterSohn = null;
                    }
                    return;
                }
            }
        }
    }

    /**
     * Sucht ein Element im Baum.
     *
     * @param pContent Vergleichselement
     * @return wenn das Vergleichselement <code>!= null</code> oder das gleiche Element nicht im Baum vorhanden ist <code>null</code>,
     * ansonsten das gefundene Element
     */
    public ContentType search(ContentType pContent)
    {
        if (pContent != null)
        {
            BinarySearchTree<ContentType> temp = this;
            while (!temp.isEmpty())
            {   // iteratives Suchen
                if (temp.zInhalt.isLess(pContent))
                { // temporärer Knoten wird je nachdem, ob pContent eine  höheren
                    // oder geringeren Wert hat, dem rechten oder linken Knoten gleichgesetzt
                    temp = temp.getRightTree();
                }
                else if (temp.zInhalt.isGreater(pContent))
                {
                    temp = temp.getLeftTree();
                }
                else
                {
                    return temp.getContent(); // wenn der Wert von pContent und der Inhalt des
                    // temporären Knotens übereinstimmen, wird der Inhalt zurückgegeben
                }
            }
        }
        return null;
    }
}

