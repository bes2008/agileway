package com.jn.agileway.jaxrs.rr.requestmapping;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.net.http.HttpMethod;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.util.List;

public class JaxrsRequestMappingAccessor<E extends Annotation> implements RequestMappingAccessor<E> {
    private E mappingAnno;
    private Path pathAnno;
    private Consumes consumesAnno;
    private Produces producesAnno;
    private List<String> paths;
    private List<HttpMethod> methods;

    public JaxrsRequestMappingAccessor(E mappingAnno, Path pathAnno, Produces producesAnno, Consumes consumesAnno, List<String> paths) {
        setMapping(mappingAnno);
        this.pathAnno = pathAnno;
        this.producesAnno = producesAnno;
        this.consumesAnno = consumesAnno;
        this.paths = paths;
        initMethods();
    }

    private void initMethods(){
        HttpMethod method = null;
        if (mappingAnno instanceof javax.ws.rs.HttpMethod) {
            method = Enums.ofName(HttpMethod.class, ((javax.ws.rs.HttpMethod) mappingAnno).value());
        } else if (mappingAnno instanceof GET) {
            method = HttpMethod.GET;
        } else if (mappingAnno instanceof POST) {
            method = HttpMethod.POST;
        } else if (mappingAnno instanceof DELETE) {
            method = HttpMethod.DELETE;
        } else if (mappingAnno instanceof PATCH) {
            method = HttpMethod.PATCH;
        }
        else if(mappingAnno instanceof  PUT){
            method = HttpMethod.PUT;
        }else if(mappingAnno instanceof  OPTIONS){
            method = HttpMethod.OPTIONS;
        }
        else if(mappingAnno instanceof  HEAD){
            method = HttpMethod.HEAD;
        }
        else {
            method = HttpMethod.TRACE;
        }
        this.methods = Collects.immutableArrayList(method);
    }


    @Override
    public E getMapping() {
        return this.mappingAnno;
    }

    @Override
    public void setMapping(E mapping) {
        this.mappingAnno = mapping;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public List<String> values() {
        return paths;
    }

    @Override
    public List<String> paths() {
        return paths;
    }

    @Override
    public List<String> params() {
        return null;
    }

    @Override
    public List<String> headers() {
        return null;
    }



    @Override
    public List<HttpMethod> methods() {
        return this.methods;
    }

    @Override
    public List<String> consumes() {
        return consumesAnno == null ? Collects.<String>emptyArrayList() : Collects.asList(consumesAnno.value());
    }

    @Override
    public List<String> produces() {
        return producesAnno == null ? Collects.<String>emptyArrayList() : Collects.asList(producesAnno.value());
    }
}
