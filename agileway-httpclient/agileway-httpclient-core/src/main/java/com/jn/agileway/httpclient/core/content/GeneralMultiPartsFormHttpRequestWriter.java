package com.jn.agileway.httpclient.core.content;

import com.jn.agileway.httpclient.core.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.core.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.multipart.Part;
import com.jn.agileway.httpclient.core.multipart.ResourcePart;
import com.jn.agileway.httpclient.core.multipart.TextPart;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GeneralMultiPartsFormHttpRequestWriter implements HttpRequestContentWriter {
    @Override
    public boolean canWrite(Object body, MediaType contentType) {
        if (!HttpClientUtils.isMultipartForm(contentType)) {
            return false;
        }
        if (body != null && !(body instanceof MultiPartsForm)) {
            return false;
        }
        return true;
    }

    @Override
    public void write(Object body, MediaType contentType, UnderlyingHttpRequest output) throws IOException {
        MultiPartsForm form = (MultiPartsForm) body;
        Charset formCharset = form.getCharset() == null ? Charsets.UTF_8 : form.getCharset();
        String boundary = contentType.getParameter("boundary");

        for (Part part : form.getParts()) {
            writePart(part, output, boundary, formCharset);
        }
        output.getBufferedContent().write(("--" + boundary + "--\r\n").getBytes(Charsets.US_ASCII));
    }

    private void writePart(Part part, UnderlyingHttpRequest output, String boundary, Charset formCharset) throws IOException {
        if (part == null) {
            return;
        }

        output.getBufferedContent().write(("--" + boundary + "\r\n").getBytes(Charsets.US_ASCII));
        String contentDisposition = part.getContentDisposition().asString();
        output.getBufferedContent().write((contentDisposition + "\r\n").getBytes(Charsets.US_ASCII));
        output.getBufferedContent().write(("Content-Type: " + part.getContentType() + "\r\n").getBytes(Charsets.US_ASCII));
        output.getBufferedContent().write("\r\n".getBytes(StandardCharsets.US_ASCII));


        if (part instanceof TextPart) {
            Charset charset = part.getCharset() == null ? formCharset : part.getCharset();
            output.getBufferedContent().write(((TextPart) part).getContent().getBytes(charset));
        } else if (part instanceof ResourcePart) {
            Resource resource = ((ResourcePart) part).getContent();
            try {
                IOs.copy(resource.getInputStream(), output.getBufferedContent());
            } finally {
                IOs.close(resource);
            }
        }
    }
}
