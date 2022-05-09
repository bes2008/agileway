package com.jn.agileway.spring.web.mvc.requestmapping;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;

public class RequestMappingAccessorFactory {
    public static RequestMappingAccessor<?> createAccessor(@NonNull Annotation annotation) {
        Preconditions.checkNotNull(annotation);
        RequestMappingAccessor accessor = null;
        if (annotation instanceof RequestMapping) {
            accessor = new RequestMappingAnnotationAccessor();
            accessor.setMapping((RequestMapping) annotation);
        } else if (annotation instanceof DeleteMapping) {
            accessor = new DeleteMappingAnnotationAccessor();
            accessor.setMapping((DeleteMapping) annotation);
        } else if (annotation instanceof GetMapping) {
            accessor = new GetMappingAnnotationAccessor();
            accessor.setMapping((GetMapping) annotation);
        } else if (annotation instanceof PatchMapping) {
            accessor = new PatchMappingAnnotationAccessor();
            accessor.setMapping((PatchMapping) annotation);
        } else if (annotation instanceof PostMapping) {
            accessor = new PostMappingAnnotationAccessor();
            accessor.setMapping((PostMapping) annotation);
        } else if (annotation instanceof PutMapping) {
            accessor = new PutMappingAnnotationAccessor();
            accessor.setMapping((PutMapping) annotation);
        }
        if (accessor == null) {
            throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("{} is not a Spring RequestMapping annotation", annotation.getClass().getSimpleName()));
        }
        return accessor;
    }
}
