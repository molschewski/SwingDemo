package com.example.swingdemo;

import com.example.swingdemo.util.UnicodeDecoder;
import com.example.swingdemo.util.Utils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTMLDocument;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class takes a string with CivitAI parameters and
 * produces a html-page to show all the information in
 * a structured form.
 */
@Component
public class CivitaiParameter {

    public static final String parameterStart = "\\[Exif SubIFD\\] User Comment - ";

    private String POS_PROMPT = "Positiv prompt:";
    private String NEG_PROMPT = "Negative prompt:";
    private String STEPS = "Steps:";
    private String CIV_RESOURCES = "Civitai resources:";

    public CivitaiParameter() {
    }

    public HTMLDocument parse(String input) {

        StringBuilder result = new StringBuilder();

//        input.codePoints().forEach(value -> System.err.println("value: " + value
//                + " Char: " + Character.toString(value)));
//
//        input.chars().forEach(value -> System.err.println(Character.reverseBytes((char) value)));

        if (!input.contains("Civitai resources")) {
            return Utils.createDoc("<p>No CivitAI prompt found</p>");
        }

        input = input.replaceFirst(parameterStart, "");

        input = UnicodeDecoder.decode(input);

        LinkedHashMap<String, String> sections = civAIFindSections(input);
        for (Map.Entry<String, String> section : sections.entrySet()) {
            if (section.getKey().equals(STEPS)) {
                result.append("<h2>Advanced</h2>");
//                Arrays.stream(section.getValue().split(",")).forEach(s -> result.append("<p>" + s + "</p>"));
                String[] advEntries = section.getValue().split(",");
                for (String advEntry : advEntries) {
                    if (advEntry.startsWith(" Created Date")) {
                        System.err.println("CivitaiParameter.parse found date");
                        result.append(advEntry + "<br>");
                    } else {
                        result.append(advEntry + "<br>");
                    }
                }

            } else if (section.getKey().equals(CIV_RESOURCES)) {
                result.append("<h2>" + section.getKey() + "</h2>");
                result.append(formatResources(section.getValue()));
//                System.err.println("CivitaiParameter.parse CivResources: " + section.getValue());
//                result.append(section.getValue() + "<br>");
            } else {
                result.append("<h2>" + section.getKey() + "</h2>");
                result.append(section.getValue() + "<br>");
            }
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

        // reenter the value for STEPS in the results
        if (sections.containsKey(STEPS)) {
            sections.put(STEPS, STEPS + sections.get(STEPS));
        }

        return sections;
    }

    private String formatResources(String input) {

        // The order in this string must be the same as in the loop to fill "result".
        String header = "<tr>" +
                "<th>type</th>" +
                "<th>weight</th>" +
                "<th>modelName</th>" +
                "<th>modelVersionName</th>" +
                "<th>modelVersionId</th></tr>";

        class Resource {

            String type;
            String weight;
            String modelVersionId;
            String modelName;
            String modelVersionName;

            // Turn Null in ""
            private String getNullAsBlank(String input) {
                return input == null ? "" : input;
            }

            public String getType() {
                return getNullAsBlank(type);
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getWeight() {
                return getNullAsBlank(weight);
            }

            public void setWeight(String weight) {
                this.weight = weight;
            }

            public String getModelVersionId() {
                return getNullAsBlank(modelVersionId);
            }

            public void setModelVersionId(String modelVersionId) {
                this.modelVersionId = modelVersionId;
            }

            public String getModelName() {
                return getNullAsBlank(modelName);
            }

            public void setModelName(String modelName) {
                this.modelName = modelName;
            }

            public String getModelVersionName() {
                return getNullAsBlank(modelVersionName);
            }

            public void setModelVersionName(String modelVersionName) {
                this.modelVersionName = modelVersionName;
            }
        }

        StringBuilder result = new StringBuilder();
        ArrayList<Resource> resources = new ArrayList<>();

        // check if the resource has the expected format
        if (!Pattern.matches(".*\\[.*\\],\s*", input)) {
            return "<p>Not a recognized resource description</p>";
        }

        Pattern res_pattern = Pattern.compile("(\\{)([^}]*)(\\})");
        Matcher res_matcher = res_pattern.matcher(input);
        while (res_matcher.find()) {
            Resource resource = new Resource();
            String res_string = res_matcher.group(2);
            String[] split = res_string.split(",");
            for (String res1 : split) {
                String[] split1 = res1.split(":");
                String key = Utils.removeQuotationMark(split1[0]);
                String value = Utils.removeQuotationMark(split1[1]);

                switch (key) {
                    case "type" -> resource.setType(value);
                    case "weight" -> resource.setWeight(value);
                    case "modelVersionId" -> resource.setModelVersionId(value);
                    case "modelName" -> resource.setModelName(value);
                    case "modelVersionName" -> resource.setModelVersionName(value);
                }
            }
            resources.add(resource);
        }

        result.append("<table>");
        result.append(header);
        for (Resource resource : resources) {
            result.append("<tr>");
            result.append("<td>" + resource.getType() + "</td>");
            result.append("<td>" + resource.getWeight() + "</td>");
            result.append("<td>" + resource.getModelName() + "</td>");
            result.append("<td>" + resource.getModelVersionName() + "</td>");
            result.append("<td>" + resource.getModelVersionId() + "</td>");
            result.append("</tr>");
        }
        result.append("</table>");

        return result.toString();
    }

}
