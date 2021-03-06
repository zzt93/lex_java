package util;

/**
 * Created by zzt on 11/11/15.
 * <p>
 * Usage:
 */
public class StringHelper {

    public static final String ONE_LINE_COMMENT = "[\\s]*((/\\*.*\\*/)|//.*)";

    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }

    public static void replace(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        builder.replace(index, index + from.length(), to);
    }

    public static boolean emptyLineOrComment(String line) {
        return line.trim().isEmpty() || line.matches(ONE_LINE_COMMENT);
    }
}
