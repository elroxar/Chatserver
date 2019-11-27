package lib.datastructure.nonlinear.graph.directed;

/**
 * Gerichtete Kante eines Graphen
 *
 * @author Stefan Christian Kohlmeier
 * @version 19.03.2019
 */
public class Edge
{

    private Vertex destination;
    private double weight;
    private boolean mark;

    /**
     * Erstellt eine neue Kante zu einem Knoten.
     *
     * @param pDestination Zielknoten
     * @param pWeight      Gewicht
     */
    public Edge(Vertex pDestination, double pWeight)
    {
        destination = pDestination;
        weight = pWeight;
    }

    /**
     * Gibt das Gewicht der Kante.
     *
     * @return Gewicht
     */
    public double getWeight()
    {
        return weight;
    }

    /**
     * Setzt das Gewicht der Kante.
     *
     * @param pWeight Gewicht
     */
    public void setWeight(double pWeight)
    {
        weight = pWeight;
    }

    /**
     * Gibt den Zielknoten der Kante.
     *
     * @return Zielknoten
     */
    public Vertex getDestination()
    {
        return destination;
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

    /**
     * Prüft, ob die Kante markiert ist.
     *
     * @return wenn die Kante markiert ist <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isMarked()
    {
        return mark;
    }

}