package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.payload.multipart.Part;
import com.jn.agileway.httpclient.core.payload.multipart.ResourcePart;
import com.jn.agileway.httpclient.core.payload.multipart.TextPart;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GeneralMultiPartsFormHttpRequestWriter implements HttpRequestPayloadWriter {
    @Override
    public boolean canWrite(HttpRequest request) {
        Object body = request.getPayload();
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (!HttpClientUtils.isMultipartForm(contentType)) {
            return false;
        }
        if (body != null && !(body instanceof MultiPartsForm)) {
            return false;
        }
        return true;
    }

    @Override
    public void write(HttpRequest request, UnderlyingHttpRequest output) throws Exception {
        Object body = request.getPayload();
        MediaType contentType = request.getHttpHeaders().getContentType();
        MultiPartsForm form = (MultiPartsForm) body;
        Charset formCharset = form.getCharset() == null ? Charsets.UTF_8 : form.getCharset();
        String boundary = contentType.getParameter("boundary");

        for (Part part : form.getParts()) {
            writePart(part, output, boundary, formCharset);
        }
        output.getPayload().write(("--" + boundary + "--\r\n").getBytes(Charsets.US_ASCII));
    }

    private void writePart(Part part, UnderlyingHttpRequest output, String boundary, Charset formCharset) throws IOException {
        if (part == null) {
            return;
        }

        output.getPayload().write(("--" + boundary + "\r\n").getBytes(Charsets.US_ASCII));
        String contentDisposition = part.getContentDisposition().asString();
        output.getPayload().write((contentDisposition + "\r\n").getBytes(Charsets.US_ASCII));
        output.getPayload().write(("Content-Type: " + part.getContentType() + "\r\n").getBytes(Charsets.US_ASCII));
        output.getPayload().write("\r\n".getBytes(StandardCharsets.US_ASCII));


        if (part instanceof TextPart) {
            Charset charset = part.getCharset() == null ? formCharset : part.getCharset();
            output.getPayload().write(((TextPart) part).getContent().getBytes(charset));
        } else if (part instanceof ResourcePart) {
            Resource resource = ((ResourcePart) part).getContent();
            try {
                IOs.copy(resource.getInputStream(), output.getPayload());
            } finally {
                IOs.close(resource);
            }
        }
    }
}
