package lex;

import util.Read;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by zzt on 11/11/15.
 * <p>
 * Usage: a java version lex, for detail see README.md
 */

public class Lex {

    private final String fileName;
    Scanner scanner;
    StringBuilder cFile;

    public Lex(String fileName) {
        this.fileName = fileName;
        cFile = new StringBuilder();

        cFile.append(lexOutput.C_HEADER + lexOutput.C_BODY);
    }

    /**
     * produce a `c source` file named `output`
     *
     * @param output Write to this file
     */
    public void produceFile(String output) throws FileNotFoundException {
        HashMap<String, String> regex = Read.parseFileUpdateOut(cFile, this.fileName);
        regex.keySet().forEach(System.out::println);
        //        toSuffix();
        //        makeNFA();
        //        toDFA();
        //        toDFAo();
        // write file
    }


    /**
     * 5. new map of DFA by table 5.1 classify by out edge -- merge same class edge 5.2 travel new map of DFAo and
     * produce switch
     */
    private void toDFAo() {

    }

    /**
     * 4. travel the map -- NFA 4.1 to produce table of new state -- closure using set
     */
    private void toDFA() {

    }

    /**
     * 3. analyze suffix expresion, edge(a, b) operator(*, ., |) -- combine them into a big map, regex 's' -- NFA, N(s),
     * regex 't' -- NFA, N(t) - r = s|t: merge the start of both - r = st: merge the start of s and end of t - r = s+:
     * p102 - r = s*: p102 use a linked list of linked list
     */
    private void makeNFA() {

    }


    /**
     * 2. convert to suffix expression use a stack
     */
    private void toSuffix() {

    }

    public static void main(String[] args) throws FileNotFoundException {
        new Lex("test.l").produceFile("res");
    }
}
