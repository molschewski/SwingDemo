package com.example.swingdemo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeDecoder {

        public static String decode(String input) {

//            String testString = "surrogate char \\uD83D\\uDD2E foo\\u2728  \\u30A8\\u30ED\\u30A2\\u30CB\\u30E1";

            Pattern pattern = Pattern.compile("(\\\\u)([0-9a-fA-F]{4})");
            Matcher matcher = pattern.matcher(input);
            StringBuilder result = new StringBuilder();

            while (matcher.find()) {
                String unicodeSequence = matcher.group(2);
                char unicodeChar = (char) Integer.parseInt(unicodeSequence, 16);
                matcher.appendReplacement(result, Character.toString(unicodeChar));
            }
            matcher.appendTail(result);
            return result.toString();
        }

}