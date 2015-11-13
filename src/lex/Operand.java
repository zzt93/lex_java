package lex;

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
}
