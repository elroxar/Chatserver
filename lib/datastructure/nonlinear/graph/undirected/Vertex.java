package lib.datastructure.nonlinear.graph.undirected;

/**
 * Kante eines Graphen
 *
 * @author Stefan Christian Kohlmeier
 * @version 02.04.2019
 */
public class Vertex
{

    private String id;
    private boolean mark;

    /**
     * Erstellt einen neuen Knoten.
     *
     * @param pId Identifikation
     */
    public Vertex(String pId)
    {
        id = pId;
        // Mark is initialised to false by default
    }

    /**
     * Gibt die Identifkation des Knotens.
     *
     * @return Identifikation
     */
    public String getId()
    {
        return id;
    }

    /**
     * Markiert den Knoten.
     *
     * @param pMark Markierung
     */
    public void setMark(boolean pMark)
    {
        mark = pMark;
    }

    /**
     * Prüft, ob der Knoten markiert ist.
     *
     * @return wenn der Knoten markiert ist <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isMarked()
    {
        return mark;
    }
}
