package lex;

import util.StringHelper;

import java.util.ArrayList;
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

    public static String expend(String find) {
        int index = find.indexOf(CLASS_SEPARATOR);
        StringBuilder to = new StringBuilder("(");
        while (index >= 0) {
            char c = find.charAt(index - 1);
            char end = find.charAt(index + 1);
            while (c <= end) {
                to.append(c).append(OR);
                c++;
            }
            index = find.indexOf(CLASS_SEPARATOR, index + 1);
        }
        to.setCharAt(to.length() - 1, ')');
        return to.toString();
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
                    if (exRegexClass.matcher(find).find()) {
                        StringHelper.replaceAll(sb, find, expend(find));
                    } else {
                        StringHelper.replaceAll(sb, find, addOr(find));
                    }
                }
        );
        return sb.toString();
    }

    private static String addOr(String find) {
        StringBuilder stringBuilder = new StringBuilder(find.length() * 2);
        stringBuilder.append("(");
        for (int i = 1; i < find.toCharArray().length - 1; i++) {
            char c = find.charAt(i);
            stringBuilder.append(c);
            if (c != '\\' || (find.charAt(i - 1) == '\\')) {
                stringBuilder.append(OR);
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static String addDot(String s) {
        ArrayList<Character> stack = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '(':
                case '[':
                    stack.add(c);
                    break;
                case ')':
                case ']':
                    break;
                default:
                    assert Character.isAlphabetic(c) || Character.isDigit(c);

            }
        }


        Pattern groupPattern = Pattern.compile(REGEX_PART);
        StringBuilder builder = new StringBuilder(s);
        Matcher matcher = groupPattern.matcher(builder);
        while (matcher.find()) {
            String group = matcher.group();
            int next = builder.indexOf(group) + group.length();
            if (Operators.isOneOperand(builder.charAt(next))) {
                next++;
            }
            if (next < builder.length() && builder.charAt(next) != OR) {
                builder.insert(next, '.');
            }
        }
        return builder.toString();
    }
}
