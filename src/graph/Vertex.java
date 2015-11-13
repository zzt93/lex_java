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

    private ArrayList<Edge> edges = new ArrayList<>();

    public boolean ifEndState() {
        return translation != null;
    }
}
