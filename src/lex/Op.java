package lex;

import graph.Graph;
import util.Stack;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage:
 */
public interface Op {

    void operateOnStack(Stack<Graph> graphStack);
}
