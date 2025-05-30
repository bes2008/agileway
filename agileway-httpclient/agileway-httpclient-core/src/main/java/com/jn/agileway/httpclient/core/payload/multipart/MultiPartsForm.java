package com.jn.agileway.httpclient.core.payload.multipart;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.io.resource.InputStreamResource;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.io.Charsets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiPartsForm {
    @Nullable
    private Charset charset;
    private List<Part> parts = Lists.newArrayList();

    public MultiPartsForm(Charset charset) {
        this.charset = charset;
    }

    public MultiPartsForm() {
    }

    public void addPart(TextPart textPart) {
        parts.add(textPart);
    }

    public void addPart(ResourcePart filePart) {
        parts.add(filePart);
    }

    void addPart(Part part) {
        if (part != null) {
            if (part instanceof TextPart) {
                if (Strings.equals(part.getName(), "_charset_")) {
                    charset = Charsets.getCharset(((TextPart) part).getContent());
                    return;
                }
            }
            parts.add(part);
        }
    }

    public Charset getCharset() {
        return charset;
    }

    public List<Part> getParts() {
        return parts;
    }

    public static MultiPartsForm ofMap(Map<String, ?> form) throws IOException {
        MultiPartsForm result = new MultiPartsForm();
        for (Map.Entry<String, ?> entry : form.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            result.addPart(ofPart(fieldName, value));
        }
        return result;
    }

    static Part ofPart(String fieldName, Object value) throws IOException {
        if (value instanceof byte[]) {
            return new ResourcePart(fieldName, null, Resources.asByteArrayResource((byte[]) value), null);
        } else if (value instanceof File) {
            return new ResourcePart(fieldName, ((File) value).getName(), Resources.loadFileResource((File) value), null);
        } else if (value instanceof Path) {
            return new ResourcePart(fieldName, ((Path) value).getFileName().toString(), Resources.loadFileResource(((Path) value).toFile()), null);
        } else if (value instanceof InputStream) {
            return new ResourcePart(fieldName, null, new InputStreamResource((InputStream) value), null);
        } else if (value instanceof Resource) {
            return new ResourcePart(fieldName, null, (Resource) value, null);
        } else {
            return new TextPart(fieldName, value == null ? null : value.toString());
        }
    }

    public static MultiPartsForm ofMultiValueMap(MultiValueMap<String, ?> form) throws IOException {
        MultiPartsForm result = new MultiPartsForm();
        Set<String> fieldNames = form.keySet();
        for (String fieldName : fieldNames) {
            Collection values = form.get(fieldName);
            for (Object value : values) {
                result.addPart(ofPart(fieldName, value));
            }
        }
        return result;
    }
}
