package com.jn.agileway.web.filter.waf;

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

}
