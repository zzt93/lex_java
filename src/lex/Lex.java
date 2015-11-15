package lex;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import util.Read;
import util.Stack;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by zzt on 11/11/15.
 * <p>
 * Usage: a java version lex, for detail see README.md suppose all your regex are valid!
 */

public class Lex {

    private final String fileName;
    Scanner scanner;
    StringBuilder cFile;

    public Lex(String fileName) {
        this.fileName = fileName;
        cFile = new StringBuilder();

        cFile.append(LexOutput.C_HEADER + LexOutput.C_BODY);
    }

    /**
     * produce a `c source` file named `output`
     *
     * @param output Write to this file
     */
    public void produceFile(String output) throws FileNotFoundException {
        HashMap<String, String> regex2translation = Read.parseFileUpdateOut(cFile, this.fileName);
        regex2translation.keySet().forEach(System.out::println);
        HashMap<String, ArrayList<Op>> regex2suffix = toSuffix(regex2translation.keySet());
        regex2suffix.values().forEach(System.out::println);
        Graph nfa = makeNFA(regex2translation, regex2suffix);
        Graph dfa = toDFA(nfa);
        toDFAo(dfa);
        // write file
    }


    /**
     * 5. new map of DFA by table 5.1 classify by out edge -- merge same class edge 5.2 travel new map of DFAo and
     * produce switch
     * @param dfa The dfa graph
     */
    private void toDFAo(Graph dfa) {

    }

    /**
     * 4. travel the map -- NFA -- to produce table of new state
     *
     * @param nfa The NFA to convert
     */
    private Graph toDFA(Graph nfa) {
        Graph dfa = new Graph();
        for (Vertex vertex : nfa.getVertexes()) {
            ArrayList<Vertex> vertexes = new ArrayList<>();
            for (Edge edge : vertex.getOutEdges()) {
                vertexes = nfa.dfs(vertex, edge);

            }
        }
        return dfa;
    }

    /**
     * 3. analyze suffix expression, edge(a, b) operator(*, ., |) -- combine them into a big map, regex 's' -- NFA,
     * N(s), regex 't' -- NFA, N(t) - r = s|t: merge the start of both - r = st: merge the start of s and end of t - r =
     * s+: p102 - r = s*: p102 use a linked list of linked list
     *
     * @param regex2translation Map: regex => translation rule
     * @param regex2suffix      Map: regex => list of operators and operation
     *
     * @return a large map of all regex(NFA)
     */
    private Graph makeNFA(HashMap<String, String> regex2translation, HashMap<String, ArrayList<Op>> regex2suffix) {
        Graph res = new Graph();
        // make a NFA for every regex
        regex2suffix.forEach(
                (s, suffix) -> {
                    Graph graph = new Graph(suffix);
                    // assign translation rule to last vertex
                    String rule = regex2translation.get(s);
                    graph.end().setTranslation(rule);
                    // merge to a large map
                    Graph.mergeGraph(res, graph, Operators.OR);
                }
        );
        return res;
    }


    /**
     * 2. convert to suffix expression use a stack
     *
     * @param strings The regex strings
     *
     * @return map:regex => operator and operand
     */
    private HashMap<String, ArrayList<Op>> toSuffix(Set<String> strings) {
        HashMap<String, ArrayList<Op>> res = new HashMap<>();
        for (String s : strings) {
            String tmp = s + ")";
            ArrayList<Op> suffix = new ArrayList<>(tmp.length());
            Stack<Operators> operators = new Stack<>();
            operators.push(Operators.LEFT_P);
            for (int i = 0; i < tmp.length(); i++) {
                char c = tmp.charAt(i);
                if (c == '\\') {
                    i++;
                    char next = tmp.charAt(i);
                    suffix.add(new Operand(next));
                } else if (c == '(') {
                    operators.push(Operators.LEFT_P);
                } else if (c == ')') {
                    Operators operator = operators.pop();
                    while (!operator.equal(Operators.LEFT_P)) {
                        suffix.add(operator);
                        operator = operators.pop();
                    }
                } else if (Operators.isOperator(c)) {
                    Operators top = operators.top();
                    Operators operator = Operators.getOperator(c);
                    if (top.lowPrecedence(operator)) {
                        operators.push(operator);
                    } else {
                        suffix.add(operators.pop());
                        operators.push(operator);
                    }
                } else { // plain operands
                    suffix.add(new Operand(c));
                }
            }
            res.put(s, suffix);
        }
        return res;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Lex lex = new Lex("test.l");
        lex.produceFile("res");
    }
}
