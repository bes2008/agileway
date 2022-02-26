package com.jn.agileway.spring.web.mvc.requestmapping;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

public class RequestMappings {
    public static boolean hasAnyRequestMappingAnnotation(AnnotatedElement annotatedElement) {
        return findFirstRequestMappingAnnotation(annotatedElement) != null;
    }

    public static boolean hasRequestMappingAnnotation(AnnotatedElement annotatedElement) {
        return getRequestMapping(annotatedElement) != null;
    }

    public static RequestMapping getRequestMapping(AnnotatedElement annotatedElement) {
        return Reflects.getAnnotation(annotatedElement, RequestMapping.class);
    }


    private static final List<Class<? extends Annotation>> requestMappingAnnotations = Collects.newArrayList(
            RequestMapping.class,
            DeleteMapping.class,
            GetMapping.class,
            PatchMapping.class,
            PostMapping.class,
            PutMapping.class
    );

    public static boolean isRequestMappingAnnotation(@NonNull final Annotation annotation) {
        Preconditions.checkNotNull(annotation);
        return Collects.anyMatch(requestMappingAnnotations, new Predicate<Class<? extends Annotation>>() {
            @Override
            public boolean test(Class<? extends Annotation> clazz) {
                return Reflects.isSubClassOrEquals(clazz, annotation.getClass());
            }
        });
    }

    public static Annotation findFirstRequestMappingAnnotation(AnnotatedElement method) {
        return Pipeline.of(Reflects.getAnnotations(method)).findFirst(new Predicate<Annotation>() {
            @Override
            public boolean test(Annotation annotation) {
                return isRequestMappingAnnotation(annotation);
            }
        });
    }

    public static RequestMethod getRequestMethod(Method method) {
        Annotation annotation = findFirstRequestMappingAnnotation(method);
        if (annotation == null) {
            return null;
        }
        RequestMappingAccessor<?> accessor = Preconditions.checkNotNull(RequestMappingAccessorFactory.createAccessor(annotation));
        List<RequestMethod> methods = Collects.clearNulls(accessor.methods());
        if (methods.isEmpty()) {
            return null;
        }
        return methods.get(0);
    }

}
