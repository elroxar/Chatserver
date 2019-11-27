package lib.datastructure.nonlinear.tree;

import libary.util.ComparableContent;

/**
 * ausgeglichener binärere Suchbaum zur Verwaltung von Daten (maximaler
 * Höhenunterschied: 1)
 *
 * @param <ContentType> Datentyp der zu verwaltenden Daten
 * @author Stefan Christian Kohlmeier
 * @version 05.02.2019
 */
public class AVLTree<ContentType extends ComparableContent<ContentType>>
{

    private ContentType zInhalt;
    private int zHöhe = -1;
    private AVLTree<ContentType> zLinkerSohn, zRechterSohn;

    /**
     * Erstellt einen leeren ausgeglichen binären Suchbaum.
     */
    public AVLTree()
    {

    }

    /**
     * Ausgleichen des Baumes, um das AVL- Kriterium zu erhalten
     */
    private void ausgleichen()
    {
        errechneHöhe(); // Höhe errechnen
        int höhenDiff = zRechterSohn.zHöhe - zLinkerSohn.zHöhe;
        if (höhenDiff > 1)
        { // Rechtsrotation
            if (zRechterSohn.zRechterSohn.zHöhe - zRechterSohn.zLinkerSohn.zHöhe < 0)
            {
                    /* Beispielszenario:
                                9
                            7
                              8
                     */
                doppelRotationRechts(); // nach Rechts Rotieren
            }
            else
            {
                /* Beispielszenario:
                            9
                        8
                    7
                 */
                rotationRechts();
            }
        }
        else if (höhenDiff < -1)
        { // Linksrotation, Szenarios umgekehrt wie bei der Rechtsrotation
            if (zLinkerSohn.zRechterSohn.zHöhe - zLinkerSohn.zLinkerSohn.zHöhe > 0)
            {
                doppelRotationLinks();
            }
            else
            {
                rotationLinks();
            }
        }
        errechneHöhe(); // Höhe nach Rotation neu errechnen
    }

    /**
     * Ausgleichsdoppelrotation im Baum für folgendes Szenario: Knoten - linker
     * Knoten - rechter Knoten
     */
    private void doppelRotationLinks()
    {
        AVLTree<ContentType> temp = zLinkerSohn.zRechterSohn;
        zLinkerSohn.zRechterSohn = temp.zLinkerSohn;
        temp.zLinkerSohn = zLinkerSohn;
        zLinkerSohn = temp;
        rotationLinks();
    }

    /**
     * Ausgleichsdoppelrotation im Baum für folgendes Szenario: Knoten - rechter
     * Knoten - linker Knoten
     */
    private void doppelRotationRechts()
    {
        AVLTree<ContentType> temp = zRechterSohn.zLinkerSohn;
        zRechterSohn.zLinkerSohn = temp.zRechterSohn;
        temp.zRechterSohn = zRechterSohn;
        zRechterSohn = temp;
        rotationRechts();
    }

    /**
     * Errechnet die Höhe des Baumes.
     */
    private void errechneHöhe()
    {
        zHöhe = Math.max(zLinkerSohn.zHöhe, zRechterSohn.zHöhe) + 1;
    }

    /**
     * Gibt den Inhalt des Baumes.
     *
     * @return Inhalt des Baumes
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
    public AVLTree<ContentType> getLeftTree()
    {
        return zLinkerSohn;
    }

    /**
     * Gibt den rechten Knoten des Baumes.
     *
     * @return rechter Knoten
     */
    public AVLTree<ContentType> getRightTree()
    {
        return zRechterSohn;
    }

    /**
     * Fügt Baum ein Element hinzu, wenn es <code>!= null</code> ist und noch nicht im Baum vorhanden ist.
     *
     * @param pContent hinzuzufügendes Element
     */
    public void insert(ContentType pContent)
    {
        if (pContent != null)
        {
            if (isEmpty())
            {
                zInhalt = pContent;
                zLinkerSohn = new AVLTree<>();
                zRechterSohn = new AVLTree<>();
            }
            else
            {   // Rekursion --> siehe search
                if (zInhalt.isGreater(pContent))
                {
                    zLinkerSohn.insert(pContent);
                }
                else if (zInhalt.isLess(pContent))
                {
                    zRechterSohn.insert(pContent);
                }
            }
            ausgleichen(); // nach dem Einfügen muss noch geprüft werden, ob der Baum gleichmaßig gefüllt ist
        }
    }

    /**
     * Prüft, ob der Baum leer ist.
     *
     * @return ob der Baum leer ist
     */
    public boolean isEmpty()
    {
        return zInhalt == null;
    }

    /**
     * Hilfsfunktion zum Löschen
     *
     * @return Kleinster Inhalt
     */
    private ContentType remMinVorgRec()
    {
        ContentType min = zLinkerSohn.zInhalt;
        if (!zLinkerSohn.zLinkerSohn.isEmpty())
        {   // Rekursion in den linken Knoten, solange er gefüllt ist
            min = zLinkerSohn.remMinVorgRec();
        }
        else
        { // wenn nicht, dann wird die Höhe von min erniedrigt,
            // da es nicht mehr an der Stelle vorhanden sein wird
            zLinkerSohn = zLinkerSohn.zRechterSohn;
        }
        ausgleichen();
        return min;
    }

    /**
     * Löscht Element des Baumes.
     *
     * @param pContent Vergleichselement
     */
    public void remove(ContentType pContent)
    {
        if (!isEmpty() && pContent != null)
        {
            if (zInhalt.isGreater(pContent))
            {
                zLinkerSohn.remove(pContent);
                ausgleichen();
            }
            else if (zInhalt.isLess(pContent))
            {
                zRechterSohn.remove(pContent);
                ausgleichen();
            }
            else
            {
                // genaue Erklärungen siehe BinarySearchTree --> remove
                if (!zLinkerSohn.isEmpty() && !zRechterSohn.isEmpty())
                { // symmetrischen Nachfolger suchen
                    if (zRechterSohn.zLinkerSohn.isEmpty())
                    {
                        zInhalt = zRechterSohn.zInhalt;
                        zRechterSohn = zRechterSohn.zRechterSohn;
                    }
                    else
                    {
                        zInhalt = zRechterSohn.remMinVorgRec();
                    }
                    ausgleichen();
                }
                else if (!zLinkerSohn.isEmpty())
                { // min mit dem linken Knoten ersetzen
                    zInhalt = zLinkerSohn.zInhalt;
                    zLinkerSohn = zLinkerSohn.zLinkerSohn;
                    ausgleichen();
                }
                else if (!zRechterSohn.isEmpty())
                { // min mit dem rechten Knoten ersetzen
                    zInhalt = zRechterSohn.zInhalt;
                    zRechterSohn = zRechterSohn.zRechterSohn;
                    ausgleichen();
                }
                else
                { // ansonsten min zu einem leeren Knoten machen
                    zInhalt = null;
                    zLinkerSohn = null;
                    zRechterSohn = null;
                    zHöhe = -1;
                }
            }
        }
    }

    /**
     * Ausgleichsrotation für folgendes Szenario: <code>Knoten - linker Sohn - linker Sohn</code>
     */
    private void rotationLinks()
    {
        tausche(zLinkerSohn);   // Rotieren...
        AVLTree<ContentType> leererSohn = zRechterSohn;
        zRechterSohn = zLinkerSohn;
        zLinkerSohn = zLinkerSohn.zLinkerSohn;
        zRechterSohn.zLinkerSohn = zRechterSohn.zRechterSohn;
        zRechterSohn.zRechterSohn = leererSohn;
        zRechterSohn.errechneHöhe(); // Neue höhen des linken und rechten Knotens -->
        // Wurzelknoten wird in insert/remove errechnet
        zLinkerSohn.errechneHöhe();
    }

    /**
     * Ausgleichsrotation für folgendes Szenatio: <code>Knoten - rechter Sohn - rechter</code>
     * Sohn
     */
    private void rotationRechts()
    {
        tausche(zRechterSohn);  // Rotieren...
        AVLTree<ContentType> leererSohn = zLinkerSohn;
        zLinkerSohn = zRechterSohn;
        zRechterSohn = zRechterSohn.zRechterSohn;
        zLinkerSohn.zRechterSohn = zLinkerSohn.zLinkerSohn;
        zLinkerSohn.zLinkerSohn = leererSohn;
        zLinkerSohn.errechneHöhe(); // Neue höhen des linken und rechten Knotens -->
        // Wurzelknoten wird in insert/remove errechnet
        zRechterSohn.errechneHöhe();
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
            AVLTree<ContentType> temp = this;
            while (!temp.isEmpty())
            {   // Rekursion
                if (temp.getContent().isLess(pContent))
                { // Führe die Methode für linken Knoten aus, wenn sein
                    // Inhalt einen geringeren Wert als pContent hat
                    temp = temp.getRightTree();
                }
                else if (temp.getContent().isGreater(pContent))
                { // Führe die Methode für den rechten Knoten aus,
                    // wenn sein Inhalt einen höhreren wert als pContent hat
                    temp = temp.getLeftTree();
                }
                else
                { // Ansonsten ist der gleichwertige Inhalt gefunden
                    return temp.getContent();
                }
            }
        }
        return null;
    }

    /**
     * Tauscht den Inhalt mit einem anderen Knoten.
     *
     * @param pKnoten Knoten zum Tauschen der Inhalte
     */
    private void tausche(AVLTree<ContentType> pKnoten)
    {
        ContentType inhalt = zInhalt;
        zInhalt = pKnoten.zInhalt;
        pKnoten.zInhalt = inhalt;
    }
}
