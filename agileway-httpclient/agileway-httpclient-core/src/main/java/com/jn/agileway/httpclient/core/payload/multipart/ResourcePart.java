package com.jn.agileway.httpclient.core.payload.multipart;

import com.jn.agileway.httpclient.util.ContentDisposition;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;


public class ResourcePart extends Part<Resource> {
    public ResourcePart(String fieldName, String filename, Resource resource, String contentType) {
        super();
        setName(Preconditions.checkNotEmpty(fieldName));
        setContent(resource);
        setContentType(Objs.useValueIfEmpty(contentType, "application/octet-stream"));

        setContentDisposition(ContentDisposition.forFormData(fieldName, filename));
    }
}
