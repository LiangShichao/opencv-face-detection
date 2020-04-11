package com.lsc.opencv.demo.util;

public class ResourcesUtil {

    public static String getResourcePath(String filename) {
        String path = Thread.currentThread().getContextClassLoader().getResource(filename).toString();
        System.out.println(path.substring(6));
        return path.substring(6);
    }
}
