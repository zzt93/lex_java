package lex;

import util.StringHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzt on 11/12/15.
 * <p>
 * Usage:
 */
public class LexInput {
    public static final String ENUM_S = "%{";
    public static final String BET_PATTERN = " ";
    public static final String SECTION_DELIM = "%%";
    public static final String REGEX_VARIABLE = "\\{[^}]*\\}";
    public static final String ENUM_E = "%}";

    /**
     * read '*.l' file store enum store method
     *
     * @return the string represented large regex
     */
    public static HashMap<String, String> parseFileUpdateOut(StringBuilder cFile, String fileName) throws FileNotFoundException {
        // handle enum part
        // find the start of enum definition
        Scanner scanner = new Scanner(new File(fileName));

        while (scanner.hasNext() && !scanner.nextLine().equals(ENUM_S)) ;

        String line;
        StringBuilder enums = new StringBuilder();
        while (scanner.hasNext() && !(line = scanner.nextLine()).equals(ENUM_E)) {
            if (StringHelper.emptyLineOrComment(line)) {
                continue;
            }
            enums.append(line).append("\n");
        }
        int enumCount = enums.toString().split(",").length;
        StringHelper.replace(cFile, LexOutput.ENUM_NUM, enums.toString());
        StringHelper.replace(cFile, LexOutput.ENUM_COUNT, "" + enumCount);

        // find next section
        while (scanner.hasNext() && !scanner.nextLine().equals(SECTION_DELIM)) ;

        // handle regex rule
        HashMap<String, String> regexMap = readAndStorePair(
                (regexs, patternPair) -> {
                    patternPair[0] = "{" + patternPair[0] + "}";
                    HashSet<String> classes =
                            Regex.getMatchedStrings(Regex.classPattern, patternPair[1]);
                    classes.forEach(s -> {
                        //update patternPair[1]
                        String replacement = regexs.get(s);
                        if (replacement == null) {
                            throw new IllegalArgumentException("unknown regex class " + s);
                        }
                        patternPair[1] = patternPair[1].replace(s, replacement);
                    });
                },
                scanner);

        // store translation rule
        HashMap<String, String> translation = readAndStorePair(
                (trans, transPair) -> {
                    HashSet<String> classes =
                            Regex.getMatchedStrings(Regex.classPattern, transPair[0]);
                    classes.forEach(
                            rClass -> {
                                String classStr = regexMap.get(rClass);
                                if (classStr == null) {
                                    throw new IllegalArgumentException("unknown regex class " + rClass);
                                }
                                transPair[0] = Regex.preProcessRegex(transPair[0].replace(rClass, classStr));
                            }
                    );
                    transPair[0] = Regex.addConcat(transPair[0]);
                },
                scanner);

        // store functions
        StringBuilder functions = new StringBuilder();
        while (scanner.hasNext()) {
            if ((line = scanner.nextLine()).trim().isEmpty()) {
                continue;
            }
            functions.append(line).append("\n");
        }
        StringHelper.replace(cFile, LexOutput.FUNCTION_NUM, functions.toString());


        // return merged regex
        // add '.' in every key
        // merge by 'or'
        //        translation.keySet();
        //                .forEach(s -> {
        //            s = Regex.addConcat(s);
        //            res.append("(").append(s).append(")").append("|");
        //        });
        //        res.deleteCharAt(res.length() - 1);

        return translation;
    }

    private static HashMap<String, String> readAndStorePair(BiConsumer<HashMap<String, String>, String[]> handle, Scanner scanner) {
        String line;
        HashMap<String, String> regexs = new HashMap<>();
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            if (StringHelper.emptyLineOrComment(line)) {
                continue;
            } else if (line.equals(SECTION_DELIM)) {
                break;
            }
            String[] patternPair = line.split(BET_PATTERN, 2);
            // for ecah != fori
            //            for (String s : patternPair) {
            //                s = handleEscapeChar(s);
            //            }
            for (int i = 0; i < patternPair.length; i++) {
                patternPair[i] = handleEscapeChar(patternPair[i]);
            }
            handle.accept(regexs, patternPair);
            regexs.put(patternPair[0], patternPair[1]);
        }
        return regexs;
    }

    public static String handleEscapeChar(String string) {
        StringBuilder res = new StringBuilder(string);
        Pattern escapeChar = Pattern.compile("\\\\.");
        Matcher matcher = escapeChar.matcher(string);
        HashSet<String> set = new HashSet<>();
        while (matcher.find()) {
            set.add(matcher.group());
        }
        set.forEach(
                s -> {
                    switch (s.charAt(s.length() - 1)) {
                        case 'n':
                            StringHelper.replaceAll(res, s, "\n");
                            break;
                        case 'r':
                            StringHelper.replaceAll(res, s, "\r");
                            break;
                        case 't':
                            StringHelper.replaceAll(res, s, "\t");
                            break;
                        default:
                            System.err.println("not support it " + s);
                    }
                }
        );
        return res.toString();
    }
}
