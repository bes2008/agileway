package com.jn.agileway.http.rr.requestmapping;

import com.jn.langx.registry.GenericPairRegistry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.struct.Pair;

import java.lang.reflect.Method;
import java.util.List;

public class RequestMappingAccessorRegistry extends GenericPairRegistry<Method, RequestMappingAccessor, Pair<Method, RequestMappingAccessor>> {
    private final List<JavaMethodRequestMappingAccessorParser> parsers = Collects.emptyArrayList();

    public void addParser(JavaMethodRequestMappingAccessorParser parser) {
        if (parser != null) {
            this.parsers.add(parser);
        }
    }

    @Override
    public Pair<Method, RequestMappingAccessor> get(Method method) {
        Preconditions.checkNotNull(method);

        Pair<Method, RequestMappingAccessor> p = super.get(method);

        if (p == null) {
            RequestMappingAccessor accessor = null;
            for (JavaMethodRequestMappingAccessorParser parser : parsers) {
                accessor = parser.parse(method);
                if (accessor != null) {
                    break;
                }
            }
            p = new Pair<Method, RequestMappingAccessor>(method, accessor);
            register(p);
        }
        return p;
    }
}
