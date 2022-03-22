package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.springboot.SpringBootVersions;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.concurrent.ConcurrentHashSet;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpringBootErrorControllers {
    private static final Logger logger = Loggers.getLogger(SpringBootErrorControllers.class);
    private final static Map<String, Predicate<String>> errorControllerInterfaceMap = new LinkedHashMap<String, Predicate<String>>();

    static {
        // spring boot 1.x
        initSpringBoot1x();
        initSpringBoot2x();
    }

    private static void initSpringBoot1x() {
        String errorControllerInterface = "org.springframework.boot.autoconfigure.web.ErrorController";
        errorControllerInterfaceMap.put(errorControllerInterface, new Predicate<String>() {
            @Override
            public boolean test(String springbootVersion) {
                return SpringBootVersions.getMajor(springbootVersion) == 1;
            }
        });
    }

    private static void initSpringBoot2x() {
        String errorControllerInterface = "org.springframework.boot.web.servlet.error.ErrorController";
        errorControllerInterfaceMap.put(errorControllerInterface, new Predicate<String>() {
            @Override
            public boolean test(String springbootVersion) {
                return SpringBootVersions.getMajor(springbootVersion) >= 2;
            }
        });
    }

    public static String getErrorController() {
        String itfc = null;
        final String springBootVersion = SpringBootVersions.getVersion();
        if (Strings.isNotBlank(springBootVersion)) {
            Map.Entry<String, Predicate<String>> entry = (Map.Entry<String, Predicate<String>>) Collects.findFirst(errorControllerInterfaceMap, new Predicate2<String, Predicate<String>>() {
                @Override
                public boolean test(String key, Predicate<String> predicate) {
                    return predicate.test(springBootVersion);
                }
            });
            if (entry != null) {
                itfc = entry.getKey();
            }
        }
        if (Strings.isNotBlank(itfc)) {
            if (!ClassLoaders.hasClass(itfc, SpringBootErrorControllers.class.getClassLoader())) {
                logger.error("Could not found class: {} ", itfc);
                itfc = null;
            }
        }
        return itfc;
    }

    private static final String ERROR_CONTROLLER_NAME = SpringBootErrorControllers.getErrorController();
    private static ConcurrentHashSet<Method> ERROR_CONTROLLER_METHODS = new ConcurrentHashSet<Method>();

    public static boolean isSpringBootErrorControllerHandlerMethod(Method actionMethod) {

        if (ERROR_CONTROLLER_METHODS.contains(actionMethod)) {
            return true;
        }
        if (Reflects.isSubClassOrEquals(ERROR_CONTROLLER_NAME, actionMethod.getDeclaringClass())) {
            ERROR_CONTROLLER_METHODS.add(actionMethod);
            return true;
        }
        return false;
    }

    private static List<String> springWebMvcSupportedProduces = Collects.asList(
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_XML_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            "application/*+xml",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
            "application/*+json",
            "*/*");
}
