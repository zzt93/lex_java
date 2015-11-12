package lex;

/**
 * Created by zzt on 11/12/15.
 * <p>
 * Usage:
 */
public enum Operators {
    PLUS('+', 1), QUES('?', 1),
    STAR('*', 1),
    OR('|', 2),
    CON('.', 2);

    private static int length = Operators.values().length;

    private final int operaNum;
    private final char op;

    public int getOperaNum() {
        return operaNum;
    }

    public char getOp() {
        return op;
    }

    Operators(char op, int operandNum) {
        this.op = op;
        this.operaNum = operandNum;
    }

    public boolean equal(char c) {
        return c == op;
    }

    public static boolean isOneOperand(char c) {
        for (int i = 0; i < length; i++) {
            Operators operator = Operators.values()[i];
            if (operator.op == c) {
                return operator.operaNum == 1;
            }
        }
        return false;
    }
}
