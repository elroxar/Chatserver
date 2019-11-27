package lib.datastructure.nonlinear.graph.directed;

import libary.datastructure.linear.list.List;

/**
 * Knoten eines Graphen
 *
 * @author Stefan Christian Kohlmeier
 * @version 19.03.2019
 */
public class Vertex
{

    private String id;
    private boolean mark;
    private List<Edge> edges = new List<>();

    /**
     * Erstellt einen neuen Knoten.
     *
     * @param pId Identifikation des Knotens
     */
    public Vertex(String pId)
    {
        id = pId;
    }

    /**
     * Gibt die Identifikation des Knotens
     *
     * @return Identifikation des Knotens
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
    public void setMark(Boolean pMark)
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

    /**
     * Fügt eine neue Kante zu <code>pVertex</code> diesem Knoten hinzu.
     *
     * @param pVertex Zielknoten der Kante
     * @param pWeight Gewicht der Kante
     */
    public void addEdge(Vertex pVertex, double pWeight)
    {
        edges.toFirst();
        while (edges.hasAccess())
        {
            if (edges.getContent().getDestination().getId().equals(pVertex.getId()))
            {
                return;
            }
            edges.next();
        }
        edges.append(new Edge(pVertex, pWeight));
    }

    /**
     * Gibt alle Kanten des Knotens.
     *
     * @return alle Kanten des Knotens neue Liste
     */
    public List<Edge> getEdges()
    {
        List<Edge> result = new List<>();
        edges.toFirst();
        while (edges.hasAccess())
        {
            result.append(edges.getContent());
            edges.next();
        }
        return edges;
    }
}
