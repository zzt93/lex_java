package lex;

import graph.Edge;
import graph.Graph;
import util.Stack;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage:
 */
public class Operand implements Op {

    private char operand;

    public Operand(char c) {
        this.operand = c;
    }

    @Override
    public String toString() {
        return "Operand{" + operand +
                '}';
    }

    @Override
    public void operateOnStack(Stack<Graph> graphStack) {
        Graph g = new Graph();
        Edge edge = new Edge(operand);
        // have to add from first because it's the beginning of the graph
        g.addVertex(edge.getFrom()).addVertex(edge.getTo());
        g.addExit(edge.getTo());
        graphStack.push(g);
    }
}
