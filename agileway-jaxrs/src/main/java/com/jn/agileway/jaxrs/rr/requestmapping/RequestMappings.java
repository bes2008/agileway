package com.jn.agileway.jaxrs.rr.requestmapping;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

public class RequestMappings {
    public static boolean hasAnyRequestMappingAnnotation(Method method) {
        return findFirstRequestMappingAnnotation(method) != null;
    }

    public static Annotation findFirstRequestMappingAnnotation(AnnotatedElement method) {
        return Pipeline.of(Reflects.getAnnotations(method)).findFirst(new Predicate<Annotation>() {
            @Override
            public boolean test(Annotation annotation) {
                return isRequestMappingAnnotation(annotation);
            }
        });
    }

    private static final List<Class<? extends Annotation>> requestMappingAnnotations = Collects.newArrayList(
            HttpMethod.class,
            GET.class,
            DELETE.class,
            PATCH.class,
            POST.class,
            PUT.class,
            HEAD.class,
            OPTIONS.class
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

    public static RequestMappingAccessor<?> createAccessor(@NonNull Method method, @NonNull Annotation annotation) {
        Preconditions.checkNotNull(annotation);
        Consumes consumes = Reflects.getAnnotation(method, Consumes.class);
        Produces produces = Reflects.getAnnotation(method, Produces.class);
        @Nullable
        Path methodPathAnno = Reflects.getAnnotation(method, Path.class);

        Class declaringClass = method.getDeclaringClass();
        // 如果是 sub-resource，则declaringClassPathAnno 是 null
        @Nullable
        Path declaringClassPathAnno = Reflects.getAnnotation(declaringClass, Path.class);
        String path = null;
        if (declaringClassPathAnno == null) {
            //  sub-resource 暂不支持
        } else {
            path = declaringClassPathAnno.value();
            if (methodPathAnno != null) {
                path = path + methodPathAnno.value();
            }
        }
        RequestMappingAccessor accessor = new JaxrsRequestMappingAccessor(annotation, methodPathAnno, produces, consumes, Collects.newArrayList(path));
        return accessor;
    }
}
