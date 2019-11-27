package lib.datastructure.nonlinear.tree;

/**
 * binärer Baum zur Verwaltung von Daten
 *
 * @param <ContentType> Datentyp der zu verwaltenden Daten
 * @author Stefan Christian Kohlmeier
 * @version 03.03.2019
 */
public class BinaryTree<ContentType>
{

    private ContentType zInhalt;
    private BinaryTree<ContentType> zLinkerSohn, zRechterSohn;

    /**
     * Erstellt einen leeren Binärbaum.
     */
    public BinaryTree()
    {

    }

    /**
     * Erstellt einen Binärbaum mit einem bestimmten Inhalt. Wenn der Inhalt <code>!= null</code>
     * ist, wird ein leerer Baum erstellt.
     *
     * @param pContent Inhalt
     */
    public BinaryTree(ContentType pContent)
    {
        if (pContent != null)
        {
            zInhalt = pContent;
            zLinkerSohn = new BinaryTree<>();
            zRechterSohn = new BinaryTree<>();
        }
    }

    /**
     * Erstellt neuen Binärbaum mit einem bestimmten Inhalt und zwei Knoten. Wenn
     * der Inhalt <code>!= null</code> ist, wird ein leerer Baum erstellt. Wenn einer der Knoten
     * leer ist, wird ein leerer Knoten eingefügt.
     *
     * @param pContent   Inhalt
     * @param pLeftTree  linker Knoten
     * @param pRightTree rechter Knoten
     */
    public BinaryTree(ContentType pContent, BinaryTree<ContentType> pLeftTree, BinaryTree<ContentType> pRightTree)
    {
        if (pContent != null)
        {
            zInhalt = pContent;
            if (pLeftTree == null)
            {
                zLinkerSohn = new BinaryTree<>();
            }
            else
            {
                zLinkerSohn = pLeftTree;
            }
            if (pRightTree == null)
            {
                zRechterSohn = new BinaryTree<>();
            }
            else
            {
                zRechterSohn = pRightTree;
            }
        }
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
     * Setzt pContent als Inhalt des Baumes, wenn es <code>!= null</code> ist.
     *
     * @param pContent Inhalt, welcher gesetzt werden soll
     */
    public void setContent(ContentType pContent)
    {
        if (pContent != null)
        {
            zInhalt = pContent;
        }
    }

    /**
     * Gibt den linken Knoten des Baumes.
     *
     * @return linker Knoten
     */
    public BinaryTree<ContentType> getLeftTree()
    {
        return zLinkerSohn;
    }

    /**
     * Wenn der Baum gefüllt ist, wird pKnoten als linker Knoten gesetzt, wenn er <code>!= null</code> ist.
     *
     * @param pKnoten zu setzender linker Knoten
     */
    public void setLeftTree(BinaryTree<ContentType> pKnoten)
    {
        if (!isEmpty() && pKnoten != null)
        {
            zLinkerSohn = pKnoten;
        }
    }

    /**
     * Gibt den rechten Knoten des Baum.
     *
     * @return rechter Knoten
     */
    public BinaryTree<ContentType> getRightTree()
    {
        return zRechterSohn;
    }

    /**
     * Wenn der Baum gefüllt ist, wird pKnoten als rechter Knoten gesetzt, wenn er <code>!= null</code> ist.
     *
     * @param pKnoten zu setzender rechter Knoten
     */
    public void setRightTree(BinaryTree<ContentType> pKnoten)
    {
        if (!isEmpty() && pKnoten != null)
        {
            zRechterSohn = pKnoten;
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
}
