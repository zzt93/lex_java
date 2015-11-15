package graph;

import java.util.ArrayList;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage: represent a state in the FA
 */
public class Vertex {

    private TraversalState state = TraversalState.NOT_VISIT;

    // if this is end state, it will be the translation rule
    private String translation = null;

    private ArrayList<Edge> outEdges = new ArrayList<>();
    private TraversalState travel;

    public boolean ifEndState() {
        return translation != null;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public TraversalState getState() {
        return state;
    }

    public ArrayList<Edge> getOutEdges() {
        return outEdges;
    }

    public static void endStartMerge(Vertex res, Vertex src) {
        // merge out edge and in edge, for src is the start state, no other in edge, so merge out is enough
        res.outEdges.addAll(src.outEdges);
    }

    public static Vertex concatVertices(Vertex main, Vertex src, boolean start) {
        Vertex newVertex = new Vertex();
        Edge e1;
        Edge e2;
        if (start) {
            e1 = new Edge(newVertex, main);
            e2 = new Edge(newVertex, src);
        } else {
            e1 = new Edge(main, newVertex);
            e2 = new Edge(src, newVertex);
        }
        newVertex.addEdge(e1);
        newVertex.addEdge(e2);
        return newVertex;
    }

    public void addEdge(Edge edge) {
        outEdges.add(edge);
    }

    public boolean visited() {
        return state == TraversalState.VISITED;
    }

    public void setTravel(TraversalState travel) {
        this.travel = travel;
    }
}
