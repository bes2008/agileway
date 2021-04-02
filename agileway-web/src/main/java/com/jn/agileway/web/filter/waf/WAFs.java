package com.jn.agileway.web.filter.waf;

import com.jn.agileway.web.filter.waf.xss.JavaScriptXssHandler;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Supplier0;

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


    public static String clearIfContainsJavaScript(Supplier0<String> dataSupplier) {
        if (Objs.isNotEmpty(dataSupplier)) {
            JavaScriptXssHandler xssHandler = JAVA_SCRIPT_XSS_HANDLER.get();
            String data = dataSupplier.get();
            if (xssHandler != null) {
                return xssHandler.apply(data);
            }else{
                return data;
            }
        }
        return null;
    }

    public static String clearIfContainsJavaScript(final String data) {
        return clearIfContainsJavaScript(new Supplier0<String>() {
            @Override
            public String get() {
                return data;
            }
        });
    }


}
