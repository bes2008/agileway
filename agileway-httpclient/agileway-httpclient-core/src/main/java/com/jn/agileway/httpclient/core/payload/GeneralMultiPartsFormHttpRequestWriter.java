package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.agileway.httpclient.core.payload.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.payload.multipart.Part;
import com.jn.agileway.httpclient.core.payload.multipart.ResourcePart;
import com.jn.agileway.httpclient.core.payload.multipart.TextPart;
import com.jn.agileway.httpclient.util.ContentEncoding;
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
import java.util.zip.GZIPOutputStream;

public class GeneralMultiPartsFormHttpRequestWriter implements HttpRequestPayloadWriter {
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
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {
        Object body = request.getPayload();
        MediaType contentType = request.getHttpHeaders().getContentType();
        MultiPartsForm form = (MultiPartsForm) body;
        Charset formCharset = form.getCharset() == null ? Charsets.UTF_8 : form.getCharset();
        String boundary = contentType.getParameter("boundary");

        boolean fileCompressDisabled = request.getHeaders().getIfAbsent(MessageHeaderConstants.REQUEST_KEY_ATTACHMENT_UPLOAD_TEXT_COMPRESS_DISABLED, false);

        for (Part part : form.getParts()) {
            writePart(part, output, boundary, formCharset, fileCompressDisabled);
        }
        output.write(("--" + boundary + "--\r\n").getBytes(Charsets.US_ASCII));
    }

    private void writePart(Part part, OutputStream output, String boundary, Charset formCharset, boolean fileCompressDisabled) throws IOException {
        if (part == null) {
            return;
        }

        boolean isCompressibleResourcePart = !fileCompressDisabled && isCompressibleResourcePart(part);

        output.write(("--" + boundary + Strings.CRLF).getBytes(Charsets.US_ASCII));
        String contentDisposition = part.getContentDisposition().asString();
        output.write((contentDisposition + Strings.CRLF).getBytes(Charsets.US_ASCII));
        output.write(("Content-Type: " + part.getContentType() + Strings.CRLF).getBytes(Charsets.US_ASCII));
        if (isCompressibleResourcePart) {
            output.write(("Content-Encoding: " + ContentEncoding.GZIP.getName() + Strings.CRLF).getBytes(Charsets.US_ASCII));
        }
        output.write("\r\n".getBytes(StandardCharsets.US_ASCII));


        if (part instanceof TextPart) {
            Charset charset = part.getCharset() == null ? formCharset : part.getCharset();
            output.write(((TextPart) part).getContent().getBytes(charset));
        } else if (part instanceof ResourcePart) {
            Resource resource = ((ResourcePart) part).getContent();
            if (!isCompressibleResourcePart) {
                try {
                    IOs.copy(resource.getInputStream(), output);
                } finally {
                    IOs.close(resource);
                }
            } else {
                OutputStream fileOutput = new GZIPOutputStream(output);
                try {
                    IOs.copy(resource.getInputStream(), fileOutput);
                } finally {
                    IOs.close(resource);
                }
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
