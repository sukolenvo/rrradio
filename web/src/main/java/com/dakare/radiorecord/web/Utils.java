package com.dakare.radiorecord.web;

import java.util.regex.Pattern;

public class Utils {

    private static final Pattern SPECIAL_CHARACTER = Pattern.compile("[!@#$%^&*\\-/\\\\]");
    private static final Pattern WHITESPACES_CHARACTER = Pattern.compile("\\s{2,}");

    public static String normalize(String line) {
        if (line == null) {
            return null;
        }
        line = line.trim();
        line = SPECIAL_CHARACTER.matcher(line).replaceAll("");
        line = WHITESPACES_CHARACTER.matcher(line).replaceAll(" ");
        return line;
    }
}
