package com.jn.agileway.web.filter.waf;

import com.jn.agileway.web.filter.waf.xss.JavaScriptXssHandler;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;

import java.util.List;

public class WAFs {

    public List<String> globalIgnorePaths = Collects.asList("favicon.ico",
            "/doc.html",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/static/**",
            "/actuator/**"
    );

    public static final ThreadLocal<JavaScriptXssHandler> JAVA_SCRIPT_XSS_HANDLER = new ThreadLocal<JavaScriptXssHandler>();

    public static String clearIfContainsJavaScript(String data) {
        if (Objs.isNotEmpty(data)) {
            JavaScriptXssHandler xssHandler = JAVA_SCRIPT_XSS_HANDLER.get();
            if (xssHandler != null) {
                return xssHandler.apply(data);
            }
        }
        return data;
    }

}
