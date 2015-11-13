package lex;

/**
 * Created by zzt on 11/12/15.
 * <p>
 * Usage: all left associative
 */
public enum Operators implements Op {

    LEFT_P('(', 10), RIGHT_P(')', -1),

    // Precedence: the first three are equal but larger than following
    PLUS('+', 1), QUES('?', 1), STAR('*', 1),

    OR('|', 2), CON('.', 2);

    private static int length = Operators.values().length;

    private final int operaNum;
    private final char op;
    private final int precedence;

    public int getOperaNum() {
        return operaNum;
    }

    public char getOp() {
        return op;
    }

    Operators(char op, int operandNum) {
        this.op = op;
        this.operaNum = operandNum;
        this.precedence = -operandNum;
    }

    public boolean equal(char c) {
        return c == op;
    }

    public boolean hasOneOperand() {
        return operaNum == 1;
    }

    private static boolean operandNum(char c, int num) {
        for (Operators operator : Operators.values()) {
            if (operator.op == c) {
                return operator.operaNum == num;
            }
        }
        return false;
    }

    public static boolean hasOneOperand(char c) {
        return operandNum(c, 1);
    }

    public static boolean hasTwoOperand(char c) {
        return operandNum(c, 2);
    }

    public static boolean isOperator(char c) {
        for (Operators operators : Operators.values()) {
            if (operators.op == c) {
                return true;
            }
        }
        return false;
    }

    public static Operators getOperator(char c) {
        for (Operators operators : Operators.values()) {
            if (operators.op == c) {
                return operators;
            }
        }
        throw new IllegalArgumentException("no such " + c);
    }

    public boolean hasTwoOperand() {
        return operaNum == 2;
    }

    public boolean equal(Operators op) {
        return equal(op.op);
    }

    @Override
    public String toString() {
        return "Operators{" +
                "operaNum=" + operaNum +
                ", op=" + op +
                ", precedence=" + precedence +
                '}';
    }

    public boolean lowPrecedence(Operators c) {
        return precedence < c.precedence;
    }
}
