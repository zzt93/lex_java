package graph;

import lex.Op;
import lex.Operators;
import util.Stack;

import java.util.*;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage:
 */
public class Graph {

    private LinkedList<Vertex> vertices = new LinkedList<>();
    private ArrayList<Vertex> exit = new ArrayList<>();
    private HashMap<VertexSet, VertexSet> core2closure = new HashMap<>();

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
        res.vertices.addAll(src.vertices);
        res.exit.addAll(src.exit);
    }

    public static void checkEndAndSetTranslation(Graph nfa, VertexSet stateSet, Vertex vertex) {
        stateSet.getIndexes().forEach(index -> {
            Vertex v = nfa.getVertex(index);
            if (v.isEndState()) {
                vertex.setTranslation(v.getTranslation());
            }
        });
    }

    public boolean isEmpty() {
        return vertices.isEmpty() || exit.isEmpty();
    }

    public Graph() {
    }

    public Vertex start() {
        if (vertices.size() < 1) {
            throw new NoSuchElementException();
        }
        return vertices.get(0);
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
        return exit.size() == 1 && !exit.get(0).isEndState();
    }

    public LinkedList<Vertex> getVertices() {
        return vertices;
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
        res.vertices.addAll(src.vertices);
    }

    private void addStart(Vertex vertex) {
        vertices.add(0, vertex);
    }

    private void updateEnd(Vertex end) {
        exit.set(0, end);
    }

    public Graph addVertex(Vertex v) {
        vertices.add(v);
        return this;
    }

    /**
     * Travel the map via specific edge and including the ε closure
     *
     * @param v    The start vertex of this dfs
     * @param edge The edge to travel
     *
     * @return The dfs result set -- which have to use the index to identify
     * every vertex uniquely
     */
    public VertexSet dfs(Vertex v, Edge edge) {
        VertexSet res = new VertexSet();
        recursiveDfs(res, v, edge);
        recoverTravelState(res);
        // TODO check the hashCode and equals of ArrayList
        if (core2closure.containsKey(res)) {
            return core2closure.get(res);
        }
        VertexSet closure = epsilonClosure(res);
        core2closure.put(res, closure);
        return closure;
    }

    /**
     * Remember to recover the state of vertex when finish dfs
     *  @param res The arrayList to contain the visited vertices index
     * @param v The start vertex
     * @param target The target edge to through
     */
    private void recursiveDfs(VertexSet res, Vertex v, Edge target) {
        if (!v.notVisit()) {
            return;
        }
        v.setTravel(TraversalState.VISITING);
        v.getOutEdges().stream().filter(target::equals).forEach(edge -> recursiveDfs(res, edge.getTo(), target));
        v.setTravel(TraversalState.VISITED);
        res.add(v.ordinal());
    }

    public VertexSet epsilonClosure(VertexSet vertexesIndex) {
        VertexSet tmp = new VertexSet();
        for (Integer i : vertexesIndex.getIndexes()) {
            recursiveDfs(tmp, vertices.get(i), Edge.epsilon());
        }
        recoverTravelState(tmp);
        vertexesIndex.addAll(tmp);
        return vertexesIndex;
    }

    private void recoverTravelState(VertexSet tmp) {
        for (Integer i : tmp.getIndexes()) {
            vertices.get(i).setTravel(TraversalState.NOT_VISIT);
        }
    }

    public void makeIndex() {
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).setIndex(i);
        }
    }

    public Vertex getVertex(Integer index) {
        return vertices.get(index);
    }

    public HashMap<Character, VertexSet> dfs(VertexSet integers) {
        HashMap<Character, VertexSet> res = new HashMap<>();

        for (Integer index : integers.getIndexes()) {
            Vertex vertex = vertices.get(index);
            for (Edge edge : vertex.getOutEdges()) {
                if (edge.isEpsilon()) {
                    continue;
                }
                VertexSet vertexes = dfs(vertex, edge);
                res.put(edge.getOperand(), vertexes);
            }
        }
        return res;
    }
}
