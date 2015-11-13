package lex;

/**
 * Created by zzt on 11/12/15.
 * <p>
 * Usage:
 */
public class LexOutput {
    public static final String C_HEADER =
            "#include <stdlib.h>\n" +
                    "#include <stdio.h>\n\n";
    public static final String ENUM_NUM = "$1";
    public static final String FUNCTION_NUM = "$2";
    public static final String ENUM_COUNT = "$3";
    static final String C_BODY = "\n" +
            "enum {\n" +
            "    $1\n" +
            "};\n" +
            "\n" +
            "//put method here\n" +
            "$2\n" +
            "\n" +
            "// use last enum element to replace it\n" +
            "int count[$3];\n" +
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
}
