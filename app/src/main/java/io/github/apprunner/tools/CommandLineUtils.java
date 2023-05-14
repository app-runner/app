package io.github.apprunner.tools;

import io.github.apprunner.plugin.AppRunnerException;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author songyinyin
 * @since 2023/4/30 15:26
 */
public class CommandLineUtils {

    private static final String[] NO_ARGS = {};

    public static String[] parseArgs(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return NO_ARGS;
        }
        try {
            arguments = arguments.replace('\n', ' ').replace('\t', ' ');
            return CommandLineUtils.translateCommandline(arguments);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse arguments [" + arguments + "]", ex);
        }
    }

    public static String[] translateCommandline(String toProcess) {
        if ((toProcess == null) || (toProcess.length() == 0)) {
            return new String[0];
        }

        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        StringTokenizer tok = new StringTokenizer(toProcess, "\"\' ", true);
        Vector<String> v = new Vector<String>();
        StringBuilder current = new StringBuilder();

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
                case inQuote:
                    if ("\'".equals(nextTok)) {
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                default:
                    if ("\'".equals(nextTok)) {
                        state = inQuote;
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                    } else if (" ".equals(nextTok)) {
                        if (current.length() != 0) {
                            v.addElement(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok);
                    }
                    break;
            }
        }

        if (current.length() != 0) {
            v.addElement(current.toString());
        }

        if ((state == inQuote) || (state == inDoubleQuote)) {
            throw new AppRunnerException("unbalanced quotes in " + toProcess);
        }

        String[] args = new String[v.size()];
        v.copyInto(args);
        return args;
    }

}
