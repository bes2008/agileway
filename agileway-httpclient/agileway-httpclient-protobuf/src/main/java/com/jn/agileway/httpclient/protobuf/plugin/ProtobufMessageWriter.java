package com.jn.agileway.httpclient.protobuf.plugin;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.util.JsonFormat;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ProtobufMessageWriter implements HttpRequestPayloadWriter {
    private JsonFormat.Printer jsonWriter;
    @Override
    public boolean canWrite(HttpRequest<?> request) {
        if (!(request.getPayload() instanceof GeneratedMessage)) {
            return false;
        }
        MediaType contentType = request.getHttpHeaders().getContentType();
        if (contentType == null) {
            return false;
        }
        if (!contentType.equalsTypeAndSubtype(MediaType.APPLICATION_PROTOBUF) && !contentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            return false;
        }
        return true;
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws Exception {
        MediaType contentType = request.getHttpHeaders().getContentType();
        // 写 为 protobuf 的二进制
        if (contentType.equalsTypeAndSubtype(MediaType.APPLICATION_PROTOBUF)) {
            writeAsProtobuf(request, output);
        }
        // 写为 json
        else if (contentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            writeAsJson(request, output);
        }
    }

    private void writeAsProtobuf(HttpRequest<?> request, OutputStream output) throws Exception {
        GeneratedMessage message = (GeneratedMessage) request.getPayload();
        CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(output);
        message.writeTo(codedOutputStream);
        codedOutputStream.flush();
    }

    private void writeAsJson(HttpRequest<?> request, OutputStream output) throws Exception {
        GeneratedMessage message = (GeneratedMessage) request.getPayload();
        OutputStreamWriter writer = new OutputStreamWriter(output, Charsets.UTF_8);
        this.jsonWriter.appendTo(message, writer);
        writer.flush();
    }
}
