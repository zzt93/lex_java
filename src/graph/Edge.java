package graph;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage:
 */
public class Edge {

    /**
     * if operand is 0, it represent the ε edge
     */
    private char operand;

    private Vertex from;
    private Vertex to;

    public Edge(char operand) {
        this.operand = operand;
        from = new Vertex();
        to = new Vertex();
    }

    public Edge(Vertex main, Vertex src) {
        from = main;
        to = src;
    }

    public boolean isEpsilon() {
        return operand == 0;
    }

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }
}
