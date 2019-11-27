package lib.datastructure.nonlinear.graph.undirected;

/**
 * Ungerichtete Kante eines Graphen
 *
 * @author Stefan Christian Kohlmeier
 * @version 02.04.2019
 */
public class Edge
{

    private Vertex[] vertices;
    private double weight;
    private boolean mark;

    /**
     * Erstellt eine neue Kante zwischen zwei Knoten.
     *
     * @param pVertex        Knoten 1
     * @param pAnotherVertex Knoten 2
     * @param pWeight        Gewicht
     */
    public Edge(Vertex pVertex, Vertex pAnotherVertex, double pWeight)
    {
        vertices = new Vertex[]{pVertex, pAnotherVertex};
        weight = pWeight;
        // Mark is initialsed to false by default
    }

    /**
     * Gibt die verbundenen Knoten.
     *
     * @return neues Array der verbundenen Kanten
     */
    public Vertex[] getVertices()
    {
        return new Vertex[]{vertices[0], vertices[1]};
    }

    /**
     * Gibt das Gewicht.
     *
     * @return Gewicht
     */
    public double getWeight()
    {
        return weight;
    }

    /**
     * Setzt das Gewicht.
     *
     * @param pWeight Gewicht
     */
    public void setWeight(double pWeight)
    {
        weight = pWeight;
    }

    /**
     * Prüft, ob die Kante markiert ist.
     *
     * @return ob die Kante markiert ist
     */
    public boolean isMarked()
    {
        return mark;
    }

    /**
     * Setzt die Markierung der Kante.
     *
     * @param pMark Markierung
     */
    public void setMark(boolean pMark)
    {
        mark = pMark;
    }
}
