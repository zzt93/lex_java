package graph;

import java.util.ArrayList;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage: represent a state in the FA
 */
public class Vertex {

    private ArrayList<Edge> outEdges = new ArrayList<>();

    private TraversalState state = TraversalState.NOT_VISIT;
    private int index;

    // if this is end state, it will be the translation rule
    private String translation = null;


    public boolean isEndState() {
        return translation != null;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public ArrayList<Edge> getOutEdges() {
        return outEdges;
    }

    public static void mergeOutEdge(Vertex res, Vertex src) {
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
            newVertex.addOutEdge(e1);
            newVertex.addOutEdge(e2);
        } else {
            e1 = new Edge(main, newVertex);
            e2 = new Edge(src, newVertex);
            main.addOutEdge(e1);
            src.addOutEdge(e2);
        }
        return newVertex;
    }

    public void addOutEdge(Edge edge) {
        outEdges.add(edge);
    }

    public boolean notVisit() {
        return state == TraversalState.NOT_VISIT;
    }

    public void setTravel(TraversalState travel) {
        this.state = travel;
    }

    public Integer ordinal() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Edge outEdge : outEdges) {
            stringBuilder.append(outEdge.toString());
        }
        if (translation == null) {
            return "Vertex{" +
                    "index=" + index +
                    ", outEdges=" + stringBuilder.toString() +
                    '}';
        }
        return "Vertex{" +
                "index=" + index +
                ", translation='" + translation + '\'' +
                ", outEdges=" + stringBuilder.toString() +
                '}';
    }

    public Vertex getNeighbor(char operand) {
        for (Edge outEdge : outEdges) {
            if (operand == outEdge.getOperand()) {
                return outEdge.getTo();
            }
        }
        return null;
    }

    public void recoverState() {
        state = TraversalState.NOT_VISIT;
    }
}
