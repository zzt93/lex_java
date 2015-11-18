package lex;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import util.StringHelper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zzt on 11/12/15.
 * <p>
 * Usage:
 */
public class LexOutput {
    public static final String C_HEADER =
            "#include <stdlib.h>\n" +
                    "#include <stdio.h>\n\n";

    public static final String ENUM_NUM = "#1";
    public static final String FUNCTION_NUM = "#2";
    public static final String ENUM_COUNT = "#3";
    public static final String SWITCH = "#4";

    public static final String C_BODY = "\n" +
            "enum {\n" +
            "    #1\n" +
            "};\n" +
            "\n" +
            "//put method here\n" +
            "#2\n" +
            "\n" +
            "// use last enum element to replace it\n" +
            "int count[#3];\n" +
            "\n" +
            "// the state number of DFA\n" +
            "int state = 0;\n" +
            "#define FOUND -1\n" +
            "\n" +
            "#define MAX 256\n" +
            "\n" +
            "int lexical(char);\n" +
            "\n" +
            "int main() {\n" +
            "    char c;\n" +
            "    char word[MAX];\n" +
            "    int i = 0;\n" +
            "    while (scanf(\" %c\", &c) >= 0) {\n" +
            "        word[i++] = c;\n" +
            "        int type = lexical(c);\n" +
            "        if (state == FOUND) {\n" +
            "            word[i] = '\\0';\n" +
            "            printf(\"(%d, %s, %d)\\n\", type, word, count[type]++);\n" +
            "            state = 0;\n" +
            "            i = 0;\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "int lexical(char c) {\n" +
            "    #4\n" +
            "}";


    public static final String STATE_NUM = "#stateNum";
    public static final String INNER_CASE = "#innerCase";
    public static final String OPERAND = "#operand";
    public static final String STATEMENT = "#statement";


    public static final String CASE =
            "        case " + STATE_NUM + ":\n" +
                    "            switch (c) {\n" +
                    INNER_CASE + "\n" +
                    "                default:\n" +
                    "                    exit(1);\n" +
                    "            }\n" +
                    "            break;\n";

    public static final String INNER_CASE_BODY =
            "                case '" + OPERAND + "':\n" +
                    "                    " + STATEMENT + "\n" +
                    "                    break;\n";

    public static final String SWITCH_END =
            "        default:\n" +
                    "            exit(1);\n" +
                    "    }\n";


    public static void outPutCSrc(StringBuilder cFile, Graph dfao, String outFileName) throws IOException {
        String s = produceSwitch(dfao);
        // write to file
        BufferedWriter bufferedWriter =
                new BufferedWriter(new FileWriter(outFileName));
        StringHelper.replace(cFile, SWITCH, s);
        System.out.println(cFile);
        bufferedWriter.write(cFile.toString());
        bufferedWriter.close();
    }

    private static String produceSwitch(Graph dfao) {
        dfao.makeIndex();
        StringBuilder stringBuilder = new StringBuilder("switch (state) {\n");
        for (Vertex vertex : dfao.getVertices()) {
            stringBuilder.append(CASE);
            StringHelper.replace(stringBuilder, STATE_NUM, "" + vertex.ordinal());
            for (Edge edge : vertex.getOutEdges()) {
                stringBuilder.insert(stringBuilder.indexOf(INNER_CASE), INNER_CASE_BODY);
                StringHelper.replace(stringBuilder, OPERAND, "" + edge.getOperand());
                Vertex to = edge.getTo();
                int offset = stringBuilder.indexOf(STATEMENT);
                if (to.isEndState()) {
                    stringBuilder.insert(offset, "{state = FOUND;}\n" + to.getTranslation());
                } else {
                    String str = "{state = " + to.ordinal() + ";}\n";
                    stringBuilder.insert(offset, str);
                }
                StringHelper.replace(stringBuilder, STATEMENT, "");
            }
            StringHelper.replace(stringBuilder, INNER_CASE, "");
        }
        stringBuilder.append(SWITCH_END);
        return stringBuilder.toString();
    }
}
