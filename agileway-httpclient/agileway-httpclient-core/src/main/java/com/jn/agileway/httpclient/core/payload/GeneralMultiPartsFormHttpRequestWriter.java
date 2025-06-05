package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.eipchannel.core.endpoint.mapper.UnsupportedObjectException;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.payload.multipart.Part;
import com.jn.agileway.httpclient.core.payload.multipart.ResourcePart;
import com.jn.agileway.httpclient.core.payload.multipart.TextPart;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GeneralMultiPartsFormHttpRequestWriter implements HttpRequestPayloadWriter, HttpRequestAttachmentPayloadLogging {
    @Override
    public boolean canWrite(HttpRequest<?> request) {
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
    public void loggingPayload(HttpRequest<?> request, OutputStream output) {
        try {
            write(request, output, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {
        write(request, output, false);
    }

    private void write(HttpRequest<?> request, OutputStream output, boolean loggingMode) throws Exception {
        Object body = request.getPayload();
        MediaType contentType = request.getHttpHeaders().getContentType();
        MultiPartsForm form = (MultiPartsForm) body;
        Charset formCharset = form.getCharset() == null ? Charsets.UTF_8 : form.getCharset();
        String boundary = contentType.getParameter("boundary");

        for (Part part : form.getParts()) {
            if (part != null) {
                if (part instanceof TextPart) {
                    writeTextPart((TextPart) part, output, boundary, formCharset);
                } else if (part instanceof ResourcePart) {
                    writeFilePart((ResourcePart) part, output, boundary, loggingMode);
                } else {
                    throw new UnsupportedObjectException("unsupported multiple part type: " + part.getClass());
                }
            }
        }
        output.write(("--" + boundary + Strings.CRLF).getBytes(Charsets.US_ASCII));
    }


    private void writeTextPart(TextPart part, OutputStream output, String boundary, Charset formCharset) throws IOException {
        output.write(("--" + boundary + Strings.CRLF).getBytes(Charsets.US_ASCII));
        String contentDisposition = part.getContentDisposition().asString();
        output.write((contentDisposition + Strings.CRLF).getBytes(Charsets.US_ASCII));
        Charset charset = part.getCharset() == null ? formCharset : part.getCharset();
        output.write(("Content-Type: " + part.getContentType() + "; charset=" + charset.name() + Strings.CRLF).getBytes(Charsets.US_ASCII));
        output.write("Content-Transfer-Encoding: 8bit".getBytes(Charsets.US_ASCII));
        output.write(Strings.CRLF.getBytes(StandardCharsets.US_ASCII));
        output.write(part.getContent().getBytes(charset));
    }

    private void writeFilePart(ResourcePart part, OutputStream output, String boundary, boolean loggingMode) throws IOException {

        output.write(("--" + boundary + Strings.CRLF).getBytes(Charsets.US_ASCII));
        String contentDisposition = part.getContentDisposition().asString();
        output.write((contentDisposition + Strings.CRLF).getBytes(Charsets.US_ASCII));
        output.write(("Content-Type: " + part.getContentType() + Strings.CRLF).getBytes(Charsets.US_ASCII));
        output.write(("Content-Transfer-Encoding: binary" + Strings.CRLF).getBytes(Charsets.US_ASCII));
        output.write("\r\n".getBytes(StandardCharsets.US_ASCII));

        if (loggingMode) {
            output.write(("<binary>" + Strings.CRLF).getBytes(Charsets.UTF_8));
        } else {
            Resource resource = part.getContent();
            try {
                IOs.copy(resource.getInputStream(), output);
            } finally {
                IOs.close(resource);
            }
        }
    }

    private static final List<String> COMPRESSIBLE_FILE_EXTENSIONS = Lists.newArrayList(
            ".txt", ".text",
            ".log",
            ".java", ".py", ".js", ".ts", ".c", ".cpp", ".h", ".cs", "rb", ".swift", ".go", ".php", ".sql", ".lua", ".r", ".sh", ".cmd", ".bat",
            ".xml", ".yaml", ".yml", ".html", ".htm", ".xhtml", ".csv", ".tsv", ".json", ".md",
            ".css",
            ".cfg", ".ini", ".conf", ".config", ".properties", ".toml", ".env",
            ".tex", ".rtf", ".srt",
            ".asc",
            "gitignore",
            "dockerfile", ".makefile",
            ".htaccess"
    );

    private boolean isCompressibleResourcePart(Part part) throws IOException {
        if (part instanceof TextPart) {
            return false;
        }
        ResourcePart resourcePart = (ResourcePart) part;
        String contentType = resourcePart.getContentType();
        if (Strings.isNotBlank(contentType)) {
            if (contentType.contains("xml")) {
                return true;
            }
            if (contentType.contains("json")) {
                return true;
            }
            if (contentType.contains("text")) {
                return true;
            }
            if (contentType.contains("javascript")) {
                return true;
            }
        }

        String filename = resourcePart.getContentDisposition().getFilename();
        if (Strings.isNotBlank(filename)) {
            if (Pipeline.of(COMPRESSIBLE_FILE_EXTENSIONS).noneMatch(new Predicate<String>() {
                @Override
                public boolean test(String extension) {
                    return filename.endsWith(extension);
                }
            })) {
                return false;
            }
        }

        // > 100 KB时才启用
        long fileLength = resourcePart.getContent().contentLength();
        if (fileLength >= 0 && fileLength < 100 * 1024) {
            return false;
        }

        return true;
    }
}
