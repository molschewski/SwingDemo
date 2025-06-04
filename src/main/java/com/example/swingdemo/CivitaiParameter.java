package com.example.swingdemo;

import com.example.swingdemo.util.UnicodeDecoder;
import com.example.swingdemo.util.Utils;

import javax.swing.text.html.HTMLDocument;
import java.util.*;

/**
 * This class takes a string with CivitAI parameters and
 * produces a html-table to show all the information in
 * a structured form.
 */
public class CivitaiParameter {

    public static final String parameterStart = "\\[Exif SubIFD\\] User Comment - ";

    public CivitaiParameter() {
    }

    public HTMLDocument parse(String input) {

        StringBuilder result = new StringBuilder();

        if (!input.contains("Civitai resources")) {
            return Utils.createDoc("<p>No CivitAI prompt found</p>");
        }

        input = input.replaceFirst(parameterStart, "");

        input = UnicodeDecoder.decode(input);

        LinkedHashMap<String, String> sections = civAIFindSections(input);
        for (Map.Entry<String, String> section : sections.entrySet()) {
            result.append("<h2>" + section.getKey() + "</h2>");
            result.append("<p>" + section.getValue() + "</p>");
        }

        return Utils.createDoc(result.toString());
    }

    /**
     * This function split the input string into sections.
     * This works only for CivitAI-Parameters.
     *
     * @param input     The input string
     * @return sections The separate sections from the input
     */
    private LinkedHashMap<String, String> civAIFindSections (String input) {

        String POS_PROMPT = "Positiv prompt:";
        String NEG_PROMPT = "Negative prompt:";
        String STEPS = "Steps:";
        String CIV_RESOURCES = "Civitai resources:";

        ArrayList<String> markers = new ArrayList<>(Arrays.asList(POS_PROMPT, NEG_PROMPT, STEPS, CIV_RESOURCES));
        LinkedHashMap<String, String> sections = new LinkedHashMap<>();

        int indexNext = 0;
        String markerStart = "";
        String markerNext = "";
        for (int i = 1; i < markers.size(); ++i) {
            int indexStart = indexNext;
            markerStart = markers.get(i -1);
            markerNext = markers.get(i);
            indexNext = input.indexOf(markerNext, indexNext);
            if (indexNext == -1) {
                System.err.println("Error, could not find \"" + markerNext + "\"");
                indexNext = indexStart;
                continue;
            }
            if (i == 1) {
                sections.put(markerStart, input.substring(indexStart, indexNext));
            } else {
                sections.put(markerStart, input.substring(indexStart + markerStart.length(), indexNext));
            }
        }
        sections.put(markerNext, input.substring(indexNext + markerNext.length(), input.length() - 1));

        return sections;
    }

}
