package com.jn.agileway.httpclient.core.payload;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.transformer.MessageTransformer;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.error.exception.NotFoundHttpContentReaderException;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import java.io.IOException;

public class HttpResponsePayloadTransformer implements MessageTransformer {
    @Override
    public Message<?> transform(Message<?> message) {


        boolean needReadBody = needReadBody(underlyingHttpResponse);
        if (needReadBody) {
            if (underlyingHttpResponse.getStatusCode() >= 400) {
                if (errorContentExtractor != null) {
                    response = errorContentExtractor.extract(underlyingHttpResponse);
                }
                if (response == null) {
                    response = new HttpResponse<>(underlyingHttpResponse, null, true);
                }
            } else {
                if (contentExtractor != null) {
                    response = contentExtractor.extract(underlyingHttpResponse);
                }

                if (response == null) {
                    final MediaType contentType = Objs.useValueIfNull(underlyingHttpResponse.getHttpHeaders().getContentType(), MediaType.TEXT_HTML);
                    HttpResponsePayloadReader reader = Pipeline.of(responseContentReaders)
                            .findFirst(new Predicate<HttpResponsePayloadReader>() {
                                @Override
                                public boolean test(HttpResponsePayloadReader httpResponseBodyReader) {
                                    return httpResponseBodyReader.canRead(underlyingHttpResponse, contentType, responseType);
                                }
                            });
                    if (reader != null) {
                        O bodyEntity = (O) reader.read(underlyingHttpResponse, contentType, responseType);
                        response = new HttpResponse<>(underlyingHttpResponse, bodyEntity);
                    } else {
                        throw new NotFoundHttpContentReaderException(StringTemplates.formatWithPlaceholder("Can't find a HttpResponseBodyReader to read the response body for Content-Type {}", contentType));
                    }
                }
            }
        } else {

            if (underlyingHttpResponse.getPayload() != null) {
                // 消耗掉
                IOs.readAsString(underlyingHttpResponse.getPayload());
            }
            response = new HttpResponse<>(underlyingHttpResponse);
        }
    }


    private boolean needReadBody(UnderlyingHttpResponse underlyingHttpResponse) throws IOException {

        if (underlyingHttpResponse.getMethod() == HttpMethod.HEAD) {
            return false;
        }
        if (underlyingHttpResponse.getStatusCode() < 200) {
            return false;
        }
        if (underlyingHttpResponse.getStatusCode() == 204) {
            return false;
        }
        if (underlyingHttpResponse.getStatusCode() == 304) {
            return false;
        }

        if (underlyingHttpResponse.getPayload() == null) {
            return false;
        }

        if (!underlyingHttpResponse.getHttpHeaders().containsKey("Transfer-Encoding")) {
            long contentLength = underlyingHttpResponse.getHttpHeaders().getContentLength();
            if (contentLength == 0L) {
                return false;
            }
        }
        return true;
    }
}
