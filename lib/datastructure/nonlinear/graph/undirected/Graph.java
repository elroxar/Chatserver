package lib.datastructure.nonlinear.graph.undirected;

import libary.datastructure.linear.list.List;

/**
 * ungerichteter Graph
 *
 * @author Stefan Christian Kohlmeier
 * @version 04.03.2019
 */
public class Graph
{

    private List<Vertex> vertices = new List<>();
    private List<Edge> edges = new List<>();

    /**
     * Erstellt einen neuen Graphen.
     */
    public Graph()
    {

    }

    /**
     * Gibt alle Knoten.
     *
     * @return eine neue Liste aller Knoten
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
        return result;
    }

    /**
     * Gibt alle Kanten.
     *
     * @return eine neue Liste aller Kanten
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
        return result;
    }

    /**
     * Fügt einen Knoten hinzu, wenn er <code>!= null</code> ist und seine <code>Id != null</code> ist.
     *
     * @param pVertex Knoten
     */
    public void addVertex(Vertex pVertex)
    {
        if (pVertex != null && pVertex.getId() != null)
        {
            vertices.toFirst();
            while (vertices.hasAccess())
            {
                if (vertices.getContent().getId().equals(pVertex.getId()))
                {
                    return;
                }
                vertices.next();
            }
            vertices.append(pVertex);
        }
    }

    /**
     * Fügt eine Kante hinzu, wenn diese noch nicht im Graphen vorhanden ist, <code>!= null</code> ist und beide Knoten
     * nicht die selben sind.
     *
     * @param pEdge Kante
     */
    public void addEdge(Edge pEdge)
    {
        if (pEdge != null && pEdge.getVertices()[0] != pEdge.getVertices()[1])
        {
            boolean verticesInGraph[] = new boolean[2];
            vertices.toFirst();
            while (vertices.hasAccess() && (!verticesInGraph[0] || !verticesInGraph[1]))
            {
                Vertex v = vertices.getContent();
                if (v == pEdge.getVertices()[0])
                {
                    verticesInGraph[0] = true;
                }
                else if (v == pEdge.getVertices()[1])
                {
                    verticesInGraph[1] = true;
                }
                vertices.next();
            }
            if (verticesInGraph[0] && verticesInGraph[1] &&
                    getEdge(pEdge.getVertices()[0], pEdge.getVertices()[1]) == null)
            {
                edges.append(pEdge);
            }
        }
    }


    /**
     * Gib einen Knoten mit einer bestimmten Id zurück.
     *
     * @param pId Identifikation des Knotens
     * @return wenn Knoten mit <code>pId</code> vorhandne ist <code>true</code>, ansonsten <code>false</code>
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
     * Löscht einen Knoten.
     *
     * @param pVertex Knoten
     */
    public void removeVertex(Vertex pVertex)
    {
        vertices.toFirst();
        while (vertices.hasAccess())
        {
            if (vertices.getContent() == pVertex)
            {
                vertices.remove();
                break;
            }
            vertices.next();
        }
        edges.toFirst();
        while (edges.hasAccess())
        {
            if (edges.getContent().getVertices()[0] == pVertex || edges.getContent().getVertices()[1] == pVertex)
            {
                edges.remove();
            }
            else
            {
                edges.next();
            }
        }
    }

    /**
     * Löscht eine Kante.
     *
     * @param pEdge Kante
     */
    public void removeEdge(Edge pEdge)
    {
        edges.toFirst();
        while (edges.hasAccess())
        {
            if (edges.getContent() == pEdge)
            {
                edges.remove();
                return;
            }
            edges.next();
        }
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
     * Markiert alle Kanten.
     *
     * @param pMark Markierung
     */
    public void setAllEdgeMarks(boolean pMark)
    {
        edges.toFirst();
        while (edges.hasAccess())
        {
            edges.getContent().setMark(pMark);
            edges.next();
        }
    }

    /**
     * Prüft, ob alle Kanten markiert sind.
     *
     * @return ob alle Kanten markiert sind
     */
    public boolean allVerticexMarked()
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
     * Prüft, ob alle Kanten markiert sind.
     *
     * @return ob alle Kanten markiert sind
     */
    public boolean allEdgesMarked()
    {
        edges.toFirst();
        while (edges.hasAccess())
        {
            if (!edges.getContent().isMarked())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Gibt die Nachbarn eines Knotens.
     *
     * @param pVertex Knoten
     * @return neue Liste der Nachbarn
     */
    public List<Vertex> getNeighbours(Vertex pVertex)
    {
        List<Vertex> result = new List<>();
        edges.toFirst();
        while (edges.hasAccess())
        {
            if (edges.getContent().getVertices()[0] == pVertex)
            {
                result.append(edges.getContent().getVertices()[1]);
            }
            else if (edges.getContent().getVertices()[1] == pVertex)
            {
                result.append(edges.getContent().getVertices()[0]);
            }
            edges.next();
        }
        return result;
    }

    /**
     * Gibt die Kanten eines Knotens.
     *
     * @param pVertex Knoten
     * @return Liste der Kanten
     */
    public List<Edge> getEdges(Vertex pVertex)
    {
        List<Edge> result = new List<>();
        edges.toFirst();
        while (edges.hasAccess())
        {
            if (edges.getContent().getVertices()[0] == pVertex || edges.getContent().getVertices()[1] == pVertex)
            {
                result.append(edges.getContent());
            }
            edges.next();
        }
        return result;
    }

    /**
     * Gibt die Kante zwischen zwei Knoten, wenn keine existiert wird "null" zurückgegeben,
     *
     * @param pVertex        Knoten
     * @param pAnotherVertex Knoten
     * @return Kante zwischen den Knoten
     */
    public Edge getEdge(Vertex pVertex, Vertex pAnotherVertex)
    {
        edges.toFirst();
        while (edges.hasAccess())
        {
            if ((edges.getContent().getVertices()[0] == pVertex && edges.getContent().getVertices()[1] == pAnotherVertex)
                    || (edges.getContent().getVertices()[1] == pVertex && edges.getContent().getVertices()[0]
                    == pAnotherVertex))
            {
                return edges.getContent();
            }
            edges.next();
        }
        return null;
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
