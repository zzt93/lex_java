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

    /**
     * create a epsilon edge
     * @param from From edge
     * @param to To edge
     */
    public Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
    }

    public Edge() {

    }

    public Edge(Vertex from, Vertex to, char c) {
        this.from = from;
        this.to = to;
        this.operand = c;
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

    public char getOperand() {
        return operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return operand == edge.operand;

    }

    @Override
    public int hashCode() {
        return (int) operand;
    }

    public static Edge epsilon() {
        return new Edge();
    }
}
