package graph;

import lex.Op;
import lex.Operators;
import util.Stack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage:
 */
public class Graph {

    private LinkedList<Vertex> vertices = new LinkedList<>();
    private ArrayList<Vertex> exit = new ArrayList<>();
    private HashMap<VertexSet, VertexSet> core2closure = new HashMap<>();
    private ArrayList<Edge> edges;
    private HashSet<Character> operands;

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

    /**
     * If you have more than one exit for a given input string, the behaviour is undefined
     *
     * @param nfa      The NFA graph
     * @param stateSet A new state of DFA
     * @param vertex   A new vertex in the graph
     */
    public static void checkEndAndSetTranslation(Graph nfa, VertexSet stateSet, Vertex vertex) {
        stateSet.getIndexes().forEach(index -> {
            Vertex v = nfa.getVertex(index);
            if (v.isEndState()) {
                vertex.setTranslation(v.getTranslation());
            }
        });
    }

    public boolean isEmpty() {
        if (vertices.isEmpty()) {
            if (exit.isEmpty()) {
                return true;
            }
            throw new IllegalArgumentException("wrong state of exit and vertices");
        }
        return false;
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
                Vertex.mergeOutEdge(res.end(), src.start());
                src.vertices.remove(src.start());
                res.updateEnd(src.end());
                break;
        }
        res.vertices.addAll(src.vertices);
    }

    public void addStart(Vertex vertex) {
        vertices.add(0, vertex);
    }

    public void updateEnd(Vertex end) {
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
     * @return The dfs result set -- which have to use the index to identify every vertex uniquely
     */
    public VertexSet dfs(Vertex v, Edge edge) {
        VertexSet res = new VertexSet();
        recursiveDfs(res, v, edge);
        recoverTravelState(res);
        v.recoverState();

        if (core2closure.containsKey(res)) {
            return core2closure.get(res);
        }
        VertexSet closure = epsilonClosure(res);
        core2closure.put(res, closure);
        return closure;
    }

    /**
     * store the vertices that `v` can walk to via `target` edge
     * <p>
     * Remember to recover the state of vertex when finish dfs
     *
     * @param res    The arrayList to contain the visited vertices index
     * @param v      The start vertex
     * @param target The target edge to through
     */
    private void recursiveDfs(VertexSet res, Vertex v, Edge target) {
        if (!v.notVisit()) {
            return;
        }
        v.setTravel(TraversalState.VISITING);
        v.getOutEdges().stream().filter(target::equals).forEach(edge -> {
            recursiveDfs(res, edge.getTo(), target);
            res.add(edge.getTo().ordinal());
        });
        v.setTravel(TraversalState.VISITED);
    }

    /**
     * fulfill by the dfs
     *
     * @param verticesIndex ArrayList of Vertices
     * @return the closure of input
     */
    public VertexSet epsilonClosure(VertexSet verticesIndex) {
        VertexSet tmp = new VertexSet();
        for (Integer i : verticesIndex.getIndexes()) {
            recursiveDfs(tmp, vertices.get(i), Edge.epsilon());
        }
        recoverTravelState(tmp);
        recoverTravelState(verticesIndex);

        verticesIndex.addAll(tmp);
        return verticesIndex;
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

        ArrayList<Integer> indexes = integers.getIndexes();
        for (Integer index : indexes) {
            Vertex vertex = vertices.get(index);
            for (Edge edge : vertex.getOutEdges()) {
                if (edge.isEpsilon()) {
                    continue;
                }
                if (res.containsKey(edge.getOperand())) {
                    VertexSet original = res.get(edge.getOperand());
                    original.add(edge.getTo().ordinal());
                } else {
                    VertexSet vertexSet = new VertexSet();
                    vertexSet.add(edge.getTo().ordinal());
                    res.put(edge.getOperand(), vertexSet);
                }
            }
        }
        for (Character character : res.keySet()) {
            VertexSet set = res.get(character);
            res.put(character, epsilonClosure(set));
        }
        return res;
    }

    public void merge(VertexSet set) {
        Vertex main = vertices.get(0);
        for (Integer integer : set.getIndexes()) {
            Vertex src = vertices.get(integer);
            merge(main, src);
            vertices.remove(src);
        }
    }

    private void merge(Vertex main, Vertex src) {
        Vertex.mergeOutEdge(main, src);
        vertices.remove(src);
        // merge the edge go in `src` to `main`
        getEdges().stream().filter(edge -> edge.getTo() == src).forEach(edge -> edge.setTo(main));
    }

    /**
     * For edge.equals() compare just operand so just use the operand
     *
     * @return The no duplicate result of operands in the edges
     */
    public HashSet<Character> getOperands() {
        if (operands == null) {
            operands = new HashSet<>();
            for (Vertex vertex : vertices) {
                operands.addAll(vertex.getOutEdges().stream().map(Edge::getOperand).collect(Collectors.toList()));
            }
        }
        return operands;
    }

    public ArrayList<Edge> getEdges() {
        if (edges == null) {
            edges = new ArrayList<>();
            for (Vertex vertex : vertices) {
                edges.addAll(vertex.getOutEdges());
            }
        }
        return edges;
    }

    public void addExit(Vertex to) {
        exit.add(to);
    }
}
