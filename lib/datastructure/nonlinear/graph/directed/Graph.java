package lib.datastructure.nonlinear.graph.directed;

import libary.datastructure.linear.list.List;

/**
 * gerichteter Graph
 *
 * @author Stefan Christian Kohlmeier
 * @version 19.03.2019
 */
public class Graph
{

    private List<Vertex> vertices = new List<>();

    /**
     * Erstellt einen neuen leeren Graphen.
     */
    public Graph()
    {

    }

    /**
     * Fügt dem Graphen einen Knoten hinzu.
     *
     * @param pId Identifikation des Knotens
     */
    public void addVertex(String pId)
    {
        Vertex vertex = getVertex(pId);
        if (vertex == null)
        {
            vertices.append(new Vertex(pId));
        }
    }

    /**
     * Fügt eine gerichtete Kante von einem Knoten zu einem anderen hinzu, wenn beide Knoten nicht dieselben sind.
     *
     * @param pStart       Anfangsknoten
     * @param pDestination Endknoten
     * @param pWeight      Gewicht
     */
    public void addEdge(String pStart, String pDestination, double pWeight)
    {
        if (!pStart.equals(pDestination))
        {
            vertices.toFirst();
            Vertex start = null, destination = null;
            while (vertices.hasAccess() && (start == null || destination == null))
            {
                if (vertices.getContent().getId().equals(pStart))
                {
                    start = vertices.getContent();
                }
                else if (vertices.getContent().getId().equals(pDestination))
                {
                    destination = vertices.getContent();
                }
                vertices.next();
            }
            if (start != null && destination != null)
            {
                start.addEdge(destination, pWeight);
            }
        }
    }

    /**
     * Löscht einen Knoten des Graphen.
     *
     * @param pId Identifikation des Knotens
     */
    public void removeVertex(String pId)
    {
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            if (vertices.getContent().getId().equals(pId))
            {
                vertices.remove();
                break;
            }
            else
            {
                List<Edge> edges = vertices.getContent().getEdges(); // Zeiger auf Kantenliste des Knotens --> Weniger Schreibarbeit
                edges.toFirst();
                while (edges.hasAccess())
                {
                    if (edges.getContent().getDestination().getId().equals(pId))
                    {
                        edges.remove(); // Alle Kanten, die den Knoten beinhalten, löschen
                    }
                    else
                    {
                        edges.next();
                    }
                }
                vertices.next();
            }
        }
    }

    /**
     * Löscht eine gerichtete Kante.
     *
     * @param pStart       Startknoten
     * @param pDestination Zielknoten
     */
    public void removeEdge(String pStart, String pDestination)
    {
        if (!pStart.equals(pDestination))
        {
            Vertex start = getVertex(pStart);
            if (start != null)
            {
                List<Edge> edges = start.getEdges();
                edges.toFirst();
                while (edges.hasAccess())
                {
                    if (edges.getContent().getDestination().getId().equals(pDestination))
                    {
                        edges.remove();
                        return;
                    }
                    edges.next();
                }
            }
        }
    }

    /**
     * Gibt einen Knoten.
     *
     * @param pId Identifikation des Knotens
     * @return Knoten
     */
    public Vertex getVertex(String pId)
    {
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            if (vertices.getContent().getId().equals(pId))
            {
                return vertices.getContent();
            }
            vertices.next();
        }
        return null;
    }

    /**
     * Gibt alle Knoten.
     *
     * @return neue Liste aller Knoten
     */
    public List<Vertex> getVertices()
    {
        List<Vertex> result = new List<>();
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            result.append(vertices.getContent());
            vertices.next();
        }

        return vertices;
    }


    /**
     * Gibt alle Nachbarn eines Knotens.
     *
     * @param pId Identifikation des Knotens
     * @return neue Liste der Nachbarn eines Knotens
     */
    public List<Vertex> getNeighbours(String pId)
    {
        List<Vertex> neighbours = new List<>();
        Vertex vertex = getVertex(pId);
        if (vertex != null)
        {
            List<Edge> edges = vertex.getEdges();
            edges.toFirst();
            while (edges.hasAccess())
            {
                neighbours.append(edges.getContent().getDestination());
                edges.next();
            }
        }
        return neighbours;
    }

    /**
     * Gibt alle Kanten.
     *
     * @return neue Liste aller Kanten
     */
    public List<Edge> getNeighbours()
    {
        List<Edge> allEdges = new List<>();
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            allEdges.concat(vertices.getContent().getEdges());
            vertices.next();
        }
        return allEdges;
    }

    /**
     * Gibt eine Kante zwischen zwei Knoten.
     *
     * @param pStart       Startknoten
     * @param pDestination Zielknoten
     * @return Kante
     */
    public Edge getEdge(String pStart, String pDestination)
    {
        Vertex start = getVertex(pStart);
        if (start != null)
        {
            List<Edge> edges = start.getEdges();
            edges.toFirst();
            while (edges.hasAccess())
            {
                if (edges.getContent().getDestination().getId().equals(pDestination))
                {
                    return edges.getContent();
                }
                edges.next();
            }
        }
        return null;
    }

    /**
     * Markiert alle Knoten.
     *
     * @param pMark Markierung
     */
    public void setAllVertexMarks(boolean pMark)
    {
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            vertices.getContent().setMark(pMark);
            vertices.next();
        }
    }

    /**
     * Prüft, ob alle Knoten markiert sind.
     *
     * @return Ob alle Knoten markiert sind
     */
    public boolean allVerticesMarked()
    {
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            if (!vertices.getContent().isMarked())
            {
                return false;
            }
            vertices.next();
        }
        return true;
    }

    /**
     * Markiert alle Kanten.
     *
     * @param pMark Markierung
     */
    public void setAllEdgesMarks(boolean pMark)
    {
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            List<Edge> edges = vertices.getContent().getEdges();
            edges.toFirst();
            while (edges.hasAccess())
            {
                edges.getContent().setMark(pMark);
                edges.next();
            }
            vertices.next();
        }
    }

    /**
     * Prüft, ob alle Kanten markiert sind.
     *
     * @return ob alle Kanten markiert sind
     */
    public boolean allEdgesMarked()
    {
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            List<Edge> edges = vertices.getContent().getEdges();
            edges.toFirst();
            while (edges.hasAccess())
            {
                if (!edges.getContent().isMarked())
                {
                    return false;
                }
                edges.next();
            }
            vertices.next();
        }
        return true;
    }

    /**
     * Prüft, ob der Graph leer ist.
     *
     * @return wenn der Graph leer ist <code>true</code>, ansonsten <code>false</code>
     */
    public boolean isEmpty()
    {
        return vertices.isEmpty();
    }

}
