package util;

import java.util.ArrayList;

/**
 * Created by zzt on 11/13/15.
 * <p>
 * Usage:
 */
public class Stack<T> {
    private ArrayList<T> stack = new ArrayList<>();

    public boolean push(T t) {
        return stack.add(t);
    }

    public T top() {
        return stack.get(stack.size() - 1);
    }

    public T pop() {
        return stack.remove(stack.size() - 1);
    }

    public int size() {
        return stack.size();
    }
}
