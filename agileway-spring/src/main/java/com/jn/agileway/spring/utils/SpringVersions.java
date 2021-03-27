package com.jn.agileway.spring.utils;

import com.jn.langx.util.Strings;
import org.springframework.core.SpringVersion;

public class SpringVersions {
    public static String getVersion() {
        return SpringVersion.getVersion();
    }

    public static String getComparableVersion() {
        String version = getVersion();
        if (Strings.endsWithIgnoreCase(version, ".RELEASE")) {
            version = version.substring(0, version.length() - ".RELEASE".length()).toString();
        }
        return version;
    }

}
