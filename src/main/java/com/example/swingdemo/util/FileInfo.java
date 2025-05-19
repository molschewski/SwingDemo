package com.example.swingdemo.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;

public class FileInfo {

    public static String getFileInfo(Path path) throws Exception {
        StringBuilder result = new StringBuilder();
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        if (attrs == null) {
            result.append("No Info found");
            return result.toString();
        }

        result.append("size: " + attrs.size() + System.lineSeparator());
        result.append("size: " + getFileSizeForHumans(attrs.size()) + System.lineSeparator());
        result.append("creation time: " + attrs.creationTime() + System.lineSeparator());
        result.append("last access time: " + attrs.lastAccessTime() + System.lineSeparator());
        result.append("modified time: " + attrs.lastModifiedTime() + System.lineSeparator());
        return result.toString();
    }

    public static String getFileSizeForHumans(long size) {
        String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB"};
        long one_exabyte =  0x1000000000000000L; //1024^6;

//        if (size < 0) {
//            throw new RuntimeException("File size is negative.");
//        }

        int index = 0;
        double result = 0.0;

        if (size < 1024) {
            result = size;
        } else if (size >= one_exabyte) {
            result = (double) size / one_exabyte;
            index = 6;
        } else {
            long lower_limit = 1024L;
            long upper_limit = 1024L * 1024L;
            index = 1;
            while (size >= upper_limit) {
                lower_limit = upper_limit;
                upper_limit = upper_limit << 10;
                index++;
            }
            result = (double) size / lower_limit;
        }
        return new DecimalFormat("#,##0.#").format(result) + units[index];
    }
}
