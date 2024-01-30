package com.jn.agileway.audit.entityloader.resttemplate;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.exception.IllegalResourceDefinition;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.agileway.audit.core.resource.idresource.AbstractEntityLoader;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.function.Function2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.Regexps;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringRestTemplateEntityLoader extends AbstractEntityLoader<Object> {

    private static final Logger logger = Loggers.getLogger(SpringRestTemplateEntityLoader.class);
    private static Regexp restTemplateVariablePattern = Regexps.compile("\\{\\w+(\\.[\\w\\-]+)*}");
    private static Regexp httpUrlVariablePattern = Regexps.compile("\\$\\{\\w+(\\.[\\w\\-]+)*}");
    private Environment environment;
    private HttpRequestProvider httpRequestProvider = new DefaultHttpRequestProvider();
    private ParameterizedResponseClassProvider parameterizedResponseClassProvider = new DefaultParameterizedResponseClassProvider();
    private ResourceEntityExtractor resourceEntityExtractor = new DefaultResourceEntityExtractor();
    private RestTemplateProvider restTemplateProvider;
    private String name = "rest";


    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected List<Object> loadInternal(AuditRequest request, ResourceDefinition resourceDefinition, List<Serializable> partitionIds) {
        final List<Object> entities = Collects.emptyArrayList();
        String url = findHttpUrl(resourceDefinition);
        url = replaceAuditVariables(url, resourceDefinition, partitionIds);
        Map<String, Object> urlVariables = findSpringRestUrlVariables(url, resourceDefinition, partitionIds);

        HttpMethod httpMethod = findHttpMethod(resourceDefinition);
        HttpEntity httpEntity = httpRequestProvider.get(url, httpMethod, resourceDefinition, partitionIds);
        ParameterizedTypeReference responseEntityClass = parameterizedResponseClassProvider.get(url, httpMethod, resourceDefinition);
        RestTemplate restTemplate = restTemplateProvider.get(url, httpMethod, resourceDefinition);
        Preconditions.checkNotNull(restTemplate, "the restTemplate is null");
        ResponseEntity responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, responseEntityClass, urlVariables);
        List<Object> objs = extractResult(responseEntity);
        entities.addAll(objs);

        return entities;
    }


    protected List<Object> extractResult(ResponseEntity responseEntity) {
        Object obj = resourceEntityExtractor.extract(responseEntity);
        if (Objs.isEmpty(obj)) {
            return null;
        }
        if (obj instanceof Collection) {
            return Collects.asList(obj);
        } else {
            return Collects.newArrayList(obj);
        }
    }


    protected String findHttpUrl(final ResourceDefinition resourceDefinition) {
        final MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String url = mapAccessor.getString("httpUrl");
        if (Emptys.isEmpty(url)) {
            throw new IllegalResourceDefinition("the httpUrl property is undefined in the resource definition");
        }
        return url;
    }

    protected String replaceAuditVariables(
            @NonNull String url,
            @NonNull final ResourceDefinition resourceDefinition,
            @NonNull final List<Serializable> subIds) {
        final MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        url = StringTemplates.format(url, httpUrlVariablePattern, new Function2<String, Object[], String>() {
            public String apply(String variable, final Object[] args) {
                if (variable.startsWith("${") && variable.endsWith("}")) {
                    variable = variable.substring(2, variable.length() - 1);
                }
                if (variable.equals("resourceId")) {
                    return Strings.join(",", subIds);
                }
                String variableValue = environment.getProperty(variable);
                if (Emptys.isEmpty(variableValue)) {
                    variableValue = mapAccessor.getString(variable);
                }
                return variableValue;
            }
        }, Emptys.EMPTY_OBJECTS);

        return url;
    }

    protected Map<String, Object> findSpringRestUrlVariables(
            @NonNull String url,
            @NonNull final ResourceDefinition resourceDefinition,
            @NonNull final List<Serializable> stepIds
    ) {

        final Map<String, Object> urlVariables = new HashMap<String, Object>();
        StringTemplates.format(url, restTemplateVariablePattern, new Function2<String, Object[], String>() {
            public String apply(String variable, final Object[] args) {
                String variableValue = null;
                if (variable.startsWith("{") && variable.endsWith("}")) {
                    variable = variable.substring(1, variable.length() - 1);
                }
                if (variable.equals("resourceId")) {
                    variableValue = Strings.join(",", stepIds);
                }
                if (Emptys.isEmpty(variableValue)) {
                    variableValue = environment.getProperty(variable);
                }
                if (Emptys.isEmpty(variableValue)) {
                    variableValue = (String) resourceDefinition.get(variable);
                }
                if (Emptys.isNotEmpty(variableValue)) {
                    urlVariables.put(variable, variableValue);
                }
                return variableValue;
            }
        }, Emptys.EMPTY_OBJECTS);
        return urlVariables;
    }

    protected HttpMethod findHttpMethod(ResourceDefinition resourceDefinition) {
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String httpMethod = mapAccessor.getString("httpMethod", "GET").toUpperCase();
        return HttpMethod.resolve(httpMethod);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setResourceEntityExtractor(ResourceEntityExtractor resourceEntityExtractor) {
        if (resourceEntityExtractor != null) {
            this.resourceEntityExtractor = resourceEntityExtractor;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (Emptys.isNotEmpty(name)) {
            this.name = name;
        }
    }

    public void setHttpRequestProvider(HttpRequestProvider httpRequestProvider) {
        if (httpRequestProvider != null) {
            this.httpRequestProvider = httpRequestProvider;
        }
    }

    public void setParameterizedResponseClassProvider(ParameterizedResponseClassProvider parameterizedResponseClassProvider) {
        this.parameterizedResponseClassProvider = parameterizedResponseClassProvider;
    }

    public void setRestTemplateProvider(RestTemplateProvider restTemplateProvider) {
        this.restTemplateProvider = restTemplateProvider;
    }

}
