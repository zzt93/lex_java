package graph;

import lex.Op;
import lex.Operators;
import util.Stack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage:
 */
public class Graph {

    private LinkedList<Vertex> vertexes = new LinkedList<>();
    private ArrayList<Vertex> exit = new ArrayList<>();

    public Graph(ArrayList<Op> suffix) {
        Stack<Graph> stack = new Stack<>();
        for (Op op : suffix) {
            op.operateOnStack(stack);
        }
        if (stack.size() != 1) {
            suffix.forEach(System.out::print);
            throw new IllegalArgumentException("invalid regex, can't construct FA");
        }
        Graph res = stack.pop();
        mergeEmpty(this, res);
    }

    private static void mergeEmpty(Graph res, Graph src) {
        res.vertexes.addAll(src.vertexes);
        res.exit.addAll(src.exit);
    }

    public boolean isEmpty() {
        return vertexes.isEmpty() || exit.isEmpty();
    }

    public Graph() {
    }

    public Vertex start() {
        if (vertexes.size() < 1) {
            throw new NoSuchElementException();
        }
        return vertexes.get(0);
    }

    public boolean oneExit() {
        return exit.size() == 1;
    }

    public Vertex end() {
        if (!oneExit()) {
            return null;
        }
        return exit.get(0);
    }

    public boolean canMergeEnd() {
        return exit.size() == 1 && !exit.get(0).ifEndState();
    }

    public LinkedList<Vertex> getVertexes() {
        return vertexes;
    }

    public static void mergeGraph(Graph res, Graph src, Operators op) {
        // handle empty res
        if (res.isEmpty()) {
            mergeEmpty(res, src);
            return;
        }
        switch (op) {
            case OR:
                // merge start
                res.addStart(Vertex.concatVertices(res.start(), src.start(), true));
                // if only one exit, merge end
                if (res.canMergeEnd() && src.canMergeEnd()) {
                    Vertex end = Vertex.concatVertices(res.end(), src.end(), false);
                    res.addVertex(end);
                    res.updateEnd(end);
                } else {
                    res.exit.addAll(src.exit);
                }
                break;
            case CON:
                if (!res.oneExit()) {
                    throw new IllegalArgumentException("res has more than one exit, can't merge");
                }
                Vertex.endStartMerge(res.end(), src.start());
                res.updateEnd(src.end());
                break;
        }
        res.vertexes.addAll(src.vertexes);
    }

    private void addStart(Vertex vertex) {
        vertexes.add(0, vertex);
    }

    private void updateEnd(Vertex end) {
        exit.set(0, end);
    }

    public Graph addVertex(Vertex from) {
        vertexes.add(from);
        return this;
    }

    /**
     * Travel the map via specific edge and including the ε closure
     *
     * @param v    The start vertex of this dfs
     * @param edge The edge to travel
     *
     * @return The dfs result set
     */
    public ArrayList<Vertex> dfs(Vertex v, Edge edge) {
        ArrayList<Vertex> res = new ArrayList<>();
        recursiveDfs(res, v, edge);
        return epsilonClosure(res);
    }

    private void recursiveDfs(ArrayList<Vertex> res, Vertex v, Edge target) {
        if (v.visited()) {
            return;
        }
        v.setTravel(TraversalState.VISITING);
        for (Edge edge1 : v.getOutEdges()) {
            if (target.equals(edge1)) {
                recursiveDfs(res, edge1.getTo(), target);
            }
        }
        v.setTravel(TraversalState.VISITED);
        res.add(v);
    }

    private ArrayList<Vertex> epsilonClosure(ArrayList<Vertex> vertexes) {
        return null;
    }
}
