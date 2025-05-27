package com.example.swingdemo;

import com.example.swingdemo.util.Utils;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;

public class CivitaiPrompt {

    public static final StringBuilder result = new StringBuilder();

    public CivitaiPrompt() {
    }

    public HTMLDocument parse(String input) {

//        StringBuilder codePoints = new StringBuilder();
//        for (int i = 5; i > 0; i--) {
//            codePoints.append(" " + String.format("%02X ", input.codePointAt(input.length() - i)));
//        }
//        System.err.println(codePoints.toString());

        System.err.println("CivitaiPrompt input: " + input);

        if (!input.contains("Civitai resources")) {
            return Utils.createDoc("<p>No CivitAI prompt found</p>");
        }

        String input2 = input.replaceFirst("\\[Exif SubIFD\\] User Comment - ", "");

        Scanner scanner = new Scanner(input2).useDelimiter(",");
        while (scanner.hasNext()) {
            result.append("<p>" + scanner.next() + "</p>");
        }
        return Utils.createDoc(result.toString());
    }

}
