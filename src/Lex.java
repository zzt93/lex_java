import util.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzt on 11/11/15.
 * <p>
 * Usage: see README.md
 */

public class Lex {
    public static final String C_HEADER =
            "#include <stdlib.h>\n" +
                    "#include <stdio.h>\n\n";
    private static final String C_BODY = "\n" +
            "enum {\n" +
            "    $1\n" +
            "};\n" +
            "\n" +
            "//put method here\n" +
            "$2\n" +
            "\n" +
            "// use last enum element to replace it\n" +
            "int count[$3 + 1];\n" +
            "\n" +
            "int main() {\n" +
            "    char word[256];\n" +
            "    while (scanf(\"%s\", word) >= 0) {\n" +
            "        int type = lexical(word);\n" +
            "\n" +
            "        printf(\"(%d, %s, %d)\", type, word, count[type]++);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "int lexical(char *str) {\n" +
            "    $4\n" +
            "}";

    public static final String ENUM_S = "%{";
    private static final String ENUM_E = "%}";
    public static final String ENUM_NUM = "$1";
    public static final String ONE_LINE_COMMENT = "(/\\*.*\\*/)|//.*";
    public static final String IN_PATTERN = " ";
    public static final String SECTION_DELIM = "%%";
    public static final String REGEX_CLASS = "\\{.*\\}";

    Scanner scanner;
    StringBuilder stringBuilder;

    public Lex(String fileName) {
        scanner = new Scanner(fileName);
        stringBuilder = new StringBuilder();

        stringBuilder.append(C_HEADER + C_BODY);
    }

    /**
     * produce a `c source` file named `fileName`
     *
     * @param fileName
     */
    public void produceFile(String fileName) {
        String regex = parseFile();
        preProcessRegex(regex);
        toSuffix();
        makeNFA();
        toDFA();
        toDFAo();
        // write file
    }

    /**
     * read '*.l' file store enum store method
     *
     * @return the string represented large regex
     */
    private String parseFile() {
        // handle enum part
        String line;
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext() && !scanner.nextLine().equals(ENUM_S)) ;

        while (scanner.hasNext() && !(line = scanner.nextLine()).equals(ENUM_E)) {
            builder.append(line);
        }
        StringHelper.replace(stringBuilder, ENUM_NUM, builder.toString());

        // ignore comment
        while (scanner.hasNext() && Objects.equals(scanner.nextLine(), ONE_LINE_COMMENT)) ;

        // handle regex rule
        HashMap<String, String> regexs = new HashMap<>();
        Pattern prePattern = Pattern.compile(REGEX_CLASS);
        ArrayList<String> subClass = new ArrayList<>();
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            if (line.isEmpty()) {
                // TODO empty line?
                continue;
            } else if (line.equals(SECTION_DELIM)) {
                break;
            }
            String[] patternPair = line.split(IN_PATTERN);
            Matcher matcher = prePattern.matcher(patternPair[1]);
            if (matcher.find()) {
                String contain = matcher.group();
                //update patternPair[1]
                String replacement = regexs.get(contain);
                if (replacement == null) {
                    throw new IllegalArgumentException("unknown regex class " + contain);
                }
                patternPair[1] = patternPair[1].replace(contain, replacement);
                subClass.add(contain);
            }
            regexs.put(patternPair[0], patternPair[1]);
        }

        // store translation rule
        HashMap<String, String> translation = new HashMap<>();
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            if (line.isEmpty()) {
                // TODO empty line?
                continue;
            } else if (line.equals(SECTION_DELIM)) {
                break;
            }
            String[] transPair = line.split(IN_PATTERN);
            translation.put(transPair[0], transPair[1]);
        }

        // remove sub-class regex from map
        subClass.forEach(regexs::remove);

        // return merged regex
        StringBuilder res = new StringBuilder();
        translation.values().forEach(
                res::append
        );
        return res.toString();
    }

    /**
     * 5. new map of DFA by table 5.1 classify by out edge -- merge same class edge 5.2 travel new map of DFAo and
     * produce switch
     */
    private void toDFAo() {

    }

    /**
     * 4. travel the map -- NFA 4.1 to produce table of new state -- closure use set
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

    /**
     * 0. non-standard->standard [a-z] -> a|b|c...|z 1. add '.' if no operand between two operator use an array
     *
     * @param regex
     */
    private String preProcessRegex(String regex) {
        return null;
    }
}
