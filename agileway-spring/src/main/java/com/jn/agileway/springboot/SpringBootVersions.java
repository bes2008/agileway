package com.jn.agileway.springboot;

import com.jn.langx.util.Strings;

public class SpringBootVersions {

    public static String getVersion() {
        try {
            return org.springframework.boot.SpringBootVersion.getVersion();
        }catch (Throwable ex){
            return null;
        }
    }

    public static int getMajor() {
        return getMajor(getVersion());
    }

    public static int getMajor(String springBootVersion) {
        String[] segments = Strings.split(springBootVersion, ".");
        return Integer.parseInt(segments[0]);
    }

}
