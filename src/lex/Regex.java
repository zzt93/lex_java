package lex;

import util.StringHelper;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzt on 11/12/15.
 * <p>
 * Usage:
 */
public class Regex {
    public static final String EX_REGEX_CLASS = "\\[[^\\]]+-[^\\]]+\\]";
    public static final String REGEX_CLASS = "\\[[^\\]]+\\]";
    public static final String REGEX_PART = "(" + REGEX_CLASS + ")|(" +
            "\\([^\\)]+\\))|(" + "\\w" +
            ")";

    public static final char CLASS_SEPARATOR = '-';
    public static final char OR = '|';
    public static final char CONCATE = '.';
    public static Pattern classPattern = Pattern.compile(LexInput.REGEX_VARIABLE);

    public static HashSet<String> getMatchedStrings(Pattern pattern, String s) {
        Matcher matcher = pattern.matcher(s);
        HashSet<String> classes = new HashSet<>();
        while (matcher.find()) {
            classes.add(matcher.group());
        }
        return classes;
    }

    public static HashSet<String> getMatchedStrings(Pattern pattern, StringBuilder regex) {
        Matcher matcher = pattern.matcher(regex);
        HashSet<String> classes = new HashSet<>();
        while (matcher.find()) {
            classes.add(matcher.group());
        }
        return classes;
    }

    /**
     * Expend 'a-z' to 'a|b|...z'
     *
     * @param find The aim string
     *
     * @return expended string
     */
    public static String expend(String find) {
        int index = find.indexOf(CLASS_SEPARATOR);
        StringBuilder res = new StringBuilder(find);
        while (index >= 0) {
            StringBuilder to = new StringBuilder();
            char c = find.charAt(index - 1);
            char end = find.charAt(index + 1);
            while (c <= end) {
                to.append(c).append(OR);
                c++;
            }
            to.deleteCharAt(to.length() - 1);
            String tmp = "" + find.charAt(index - 1) + CLASS_SEPARATOR + end;
            StringHelper.replaceAll(res, tmp, to.toString());
            index = find.indexOf(CLASS_SEPARATOR, index + 1);
        }
        return res.toString();
    }

    /**
     * 0. non-standard->standard [a-z] -> a|b|c...|z 1. add '.' if no operand between two operator use an array
     *
     * @param regex The target to handle
     */
    public static String preProcessRegex(String regex) {
        Pattern regexClass = Pattern.compile(REGEX_CLASS);
        Pattern exRegexClass = Pattern.compile(EX_REGEX_CLASS);
        HashSet<String> matchedStrings = getMatchedStrings(regexClass, regex);
        StringBuilder sb = new StringBuilder(regex);
        matchedStrings.forEach(
                find -> {
                    String tmp = find;
                    if (exRegexClass.matcher(find).find()) {
                        tmp = expend(find);
                        StringHelper.replaceAll(sb, find, tmp);
                    }
                    StringHelper.replaceAll(sb, tmp, addOr(tmp));
                }
        );
        return sb.toString();
    }

    /**
     * change '[...]' to '(..|..|...)'
     *
     * @param find
     *
     * @return
     */
    private static String addOr(String find) {
        StringBuilder stringBuilder = new StringBuilder(find.length() * 2);
        stringBuilder.append("(");
        for (int i = 1; i < find.toCharArray().length - 1; i++) {
            char c = find.charAt(i);
            if (c == '|') {
                continue;
            }
            if (Operators.isOperator(c)) {
                stringBuilder.append("\\");
            }
            stringBuilder.append(c);
            // TODO handle escape char
            if (c != '\\' || (find.charAt(i - 1) == '\\')) {
                stringBuilder.append(OR);
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static String addConcat(String regex) {
        StringBuilder builder = new StringBuilder(regex);
        int part = 0;
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            // TODO handle escape char
            if (c == '\\') {
                if (part == 0) {
                    part = 1;
                } else {
                    builder.insert(i, CONCATE);
                    i++;
                }
                i++;
                continue;
            }


            if (Operators.hasOneOperand(c)) {
                part = 1;
            } else if (Operators.hasTwoOperand(c)) {
                part = 0;
            } else {
                switch (c) {
                    case '(':
                    case '[':
                        if (part == 1) {
                            builder.insert(i, CONCATE);
                            i++;
                            part = 0;
                        }
                        break;
                    case ')':
                    case ']':
                        part = 1;
                        break;
                    default:
                        // all other valid operand in the regex
                        if (part == 0) {
                            part = 1;
                        } else {
                            builder.insert(i, CONCATE);
                            i++;
                        }
                }
            }
        }


        return builder.toString();
    }

}
