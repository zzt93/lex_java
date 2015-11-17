package lex;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import graph.VertexSet;
import util.Stack;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by zzt on 11/11/15.
 * <p>
 * Usage: a java version lex, for detail see README.md suppose all your regex are valid!
 */

public class Lex {

    private final String fileName;
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
    public void produceFile(String output) throws IOException {
        HashMap<String, String> regex2translation = LexInput.parseFileUpdateOut(cFile, this.fileName);
        regex2translation.keySet().forEach(System.out::println);
        System.out.println();

        HashMap<String, ArrayList<Op>> regex2suffix = toSuffix(regex2translation.keySet());
        regex2suffix.values().forEach(System.out::println);
        System.out.println();

        Graph nfa = makeNFA(regex2translation, regex2suffix);
        nfa.makeIndex();
        nfa.getVertices().forEach(System.out::println);
        System.out.println();

        Graph dfa = toDFA(nfa);
        dfa.makeIndex();
        dfa.getVertices().forEach(System.out::println);
        System.out.println();

        Graph dfao = toDFAo(dfa);
        dfao.makeIndex();
        dfao.getVertices().forEach(System.out::println);

        //5.2 travel new graph of DFAo and produce switch
        LexOutput.outPutCSrc(cFile, dfao, output);
    }


    /**
     * 5 classify by out edge -- merge same class edge
     *
     * @param dfa The dfa graph
     */
    private Graph toDFAo(Graph dfa) {
        dfa.makeIndex();

        LinkedList<Vertex> vertices = dfa.getVertices();
        ArrayDeque<VertexSet> group = classify(new VertexSet(vertices), index -> {
            Vertex vertex = dfa.getVertex(index);
            if (!vertex.isEndState()) {
                return null;
            }
            return vertex.getTranslation();
        });
        HashSet<Character> operands = dfa.getOperands();
        int lastSize = 0;

        // look back after separate
        while (lastSize != group.size()) {
            lastSize = group.size();

            for (Character operand : operands) { // classify by every operand
                int count = group.size();
                while (count != 0) {
                    ArrayDeque<VertexSet> tmp = classify(group.getFirst(), index -> {
                        Vertex vertex = dfa.getVertex(index);
                        Vertex neighbor = vertex.getNeighbor(operand);
                        if (neighbor == null) {
                            return null;
                        }
                        for (VertexSet set : group) {
                            if (set.contain(neighbor)) {
                                return set;
                            }
                        }
                        throw new IllegalStateException("should not run to here");
                    });
                    // to make getFirst() give different result every loop
                    group.pop();
                    // if the result is still one group, this split is a failure
                    boolean fail = tmp.stream().filter(set -> !set.isEmpty()).count() == 1;
                    if (fail) {
                        count--;
                    } else {
                        // make sure very group is tried to separate
                        count = group.size() + tmp.size();
                    }
                    List<VertexSet> collect = tmp.stream().filter(set -> !set.isEmpty()).collect(Collectors.toList());
                    group.addAll(collect);
                }
            }
        }
        // make a new graph according to classification
        return dfa.mergeVertices(group);

    }

    /**
     * classify vertex in vertexSet by the rule
     *
     * @param vertexSet Vertex index set
     * @param rule      Which return the vertex group the vertex go to
     *
     * @return new vertex group
     */
    private <T> ArrayDeque<VertexSet> classify(VertexSet vertexSet, Function<Integer, T> rule) {

        HashMap<T, VertexSet> setArrayListHashMap = new HashMap<>();
        VertexSet nullSet = new VertexSet();
        for (Integer integer : vertexSet.getIndexes()) {
            T apply = rule.apply(integer);
            if (apply == null) {
                nullSet.add(integer);
                continue;
            }
            if (setArrayListHashMap.containsKey(apply)) {
                setArrayListHashMap.get(apply).add(integer);
            } else {
                VertexSet list = new VertexSet();
                list.add(integer);
                setArrayListHashMap.put(apply, list);
            }
        }
        ArrayDeque<VertexSet> sets = new ArrayDeque<>(setArrayListHashMap.values());
        sets.add(nullSet);
        return sets;
    }

    /**
     * 4. travel the map -- NFA -- to produce table of new state, and produce the new graph of DFA
     *
     * @param nfa The NFA to convert
     *
     * @return DFA graph
     */
    private Graph toDFA(Graph nfa) {
        nfa.makeIndex();

        ArrayList<VertexSet> oldState = new ArrayList<>();
        ArrayList<Vertex> nClosure = new ArrayList<>();
        VertexSet i0 = new VertexSet();
        i0.add(0);
        Vertex first = new Vertex();
        VertexSet vertexSet = nfa.epsilonClosure(i0);
        Graph.checkEndAndSetTranslation(nfa, vertexSet, first);
        oldState.add(vertexSet);
        nClosure.add(first);

        Graph dfa = new Graph();
        dfa.addVertex(first);

        for (int i = 0; i < oldState.size(); i++) {
            VertexSet startSet = oldState.get(i);
            HashMap<Character, VertexSet> destTable = nfa.move(startSet);
            Vertex from = nClosure.get(i);

            for (Character character : destTable.keySet()) {
                Vertex to;
                VertexSet list = destTable.get(character);
                if (VertexSet.contain(oldState, list)) {
                    int j = VertexSet.get(oldState, list);
                    to = nClosure.get(j);
                } else {
                    to = new Vertex();
                    Graph.checkEndAndSetTranslation(nfa, list, to);
                    oldState.add(list);
                    nClosure.add(to);
                    dfa.addVertex(to);
                }
                from.addOutEdge(new Edge(from, to, character));
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
                    Operators operator = Operators.getOperator(c);
                    while (!operators.top().lowPrecedence(operator)) {
                        suffix.add(operators.pop());
                    }
                    operators.push(operator);
                } else { // plain operands
                    suffix.add(new Operand(c));
                }
            }
            res.put(s, suffix);
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        Lex lex = new Lex("book.l");
        lex.produceFile("res");
    }
}
