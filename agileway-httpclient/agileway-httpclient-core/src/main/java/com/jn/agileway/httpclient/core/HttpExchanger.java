package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.concurrent.promise.AsyncCallback;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.net.uri.component.UriComponentsBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class HttpExchanger extends AbstractInitializable {
    /**
     * 执行同步执行时，可以为 null
     */
    @Nullable
    private Executor executor;
    @NonNull
    private UnderlyingHttpRequestFactory requestFactory;

    /**
     * 对请求进行拦截处理
     */
    private final List<HttpRequestInterceptor> builtinRequestInterceptors = Lists.asList(new HttpRequestHeadersInterceptor());
    private final List<HttpRequestInterceptor> customRequestInterceptors = Lists.newArrayList();
    private List<HttpRequestInterceptor> requestInterceptors;
    /**
     * 主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private final List<HttpRequestBodyWriter> requestBodyWriters = Lists.newArrayList();

    /**
     * 对正常的响应进行反序列化
     */
    private final List<HttpResponseBodyReader> responseBodyReaders = Lists.newArrayList();
    /**
     * 对4xx,5xx的响应进行处理
     */
    @Nullable
    private HttpResponseErrorHandler httpResponseErrorHandler;
    /**
     * 响应拦截器
     */
    private final List<HttpResponseInterceptor> builtinResponseInterceptors = Lists.newArrayList();
    private final List<HttpResponseInterceptor> customResponseInterceptors = Lists.newArrayList();
    private List<HttpResponseInterceptor> responseInterceptors;

    @Override
    protected void doInit() throws InitializationException {
        List<HttpRequestInterceptor> requestInterceptors = Lists.newArrayList();
        if (customRequestInterceptors != null) {
            requestInterceptors.addAll(customRequestInterceptors);
        }
        requestInterceptors.addAll(builtinRequestInterceptors);
        this.requestInterceptors = Lists.immutableList(requestInterceptors);


        List<HttpResponseInterceptor> responseInterceptors = Lists.newArrayList();
        if (customResponseInterceptors != null) {
            responseInterceptors.addAll(customResponseInterceptors);
        }
        responseInterceptors.addAll(builtinResponseInterceptors);
        this.responseInterceptors = Lists.immutableList(responseInterceptors);
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setRequestFactory(UnderlyingHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
        if (interceptor != null) {
            this.customRequestInterceptors.add(interceptor);
        }
    }

    public void addResponseInterceptor(HttpResponseInterceptor interceptor) {
        if (interceptor != null) {
            this.customResponseInterceptors.add(interceptor);
        }
    }

    public void addRequestBodyWriter(HttpRequestBodyWriter writer) {
        if (writer != null) {
            this.requestBodyWriters.add(writer);
        }
    }

    public void addResponseBodyReader(HttpResponseBodyReader reader) {
        if (reader != null) {
            this.responseBodyReaders.add(reader);
        }
    }

    public void setHttpResponseErrorHandler(HttpResponseErrorHandler errorResponseHandler) {
        this.httpResponseErrorHandler = errorResponseHandler;
    }

    public <I, O> Promise<HttpResponse<O>> exchange(boolean async,
                                                    @NonNull HttpMethod method,
                                                    @NonNull String uriTemplate,
                                                    @Nullable MultiValueMap<String, String> queryParams,
                                                    @Nullable Map<String, Object> uriVariables,
                                                    @Nullable HttpHeaders headers,
                                                    @Nullable I body,
                                                    @Nullable final Type responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriTemplate);
        if (queryParams != null) {
            uriBuilder.queryParams(queryParams);
        }
        if (uriVariables != null) {
            uriBuilder.uriVariables(uriVariables);
        }
        URI uri = uriBuilder.build().toUri();

        return exchange(async, new HttpRequest(uri, method, headers, body), responseType);
    }

    public <I, O> Promise<HttpResponse<O>> exchange(boolean async, final HttpRequest request, final Type responseType) {
        Task<UnderlyingHttpResponse> sendRequestTask = new Task<UnderlyingHttpResponse>() {

            @Override
            public UnderlyingHttpResponse run(Handler<UnderlyingHttpResponse> resolve, ErrorHandler reject) {
                if (request.getMethod() == null) {
                    throw new HttpRequestInvalidException("HTTP method is required");
                }
                if (request.getUri() == null) {
                    throw new HttpRequestInvalidException("HTTP uri is required");
                }
                if (requestInterceptors != null) {
                    for (HttpRequestInterceptor interceptor : requestInterceptors) {
                        interceptor.intercept(request);
                    }
                }
                try {
                    UnderlyingHttpRequest underlyingHttpRequest = requestFactory.create(request.getMethod(), request.getUri(), request.getHeaders().getContentType());

                    if (request.getBody() != null) {
                        HttpRequestBodyWriter requestBodyWriter = Pipeline.of(requestBodyWriters)
                                .findFirst(new Predicate<HttpRequestBodyWriter>() {
                                    @Override
                                    public boolean test(HttpRequestBodyWriter writer) {
                                        return writer.canWrite(request.getBody(), request.getHeaders().getContentType());
                                    }
                                });
                        if (requestBodyWriter != null) {
                            requestBodyWriter.write(request.getBody(), request.getHeaders().getContentType(), underlyingHttpRequest);
                        } else {
                            throw new NotFoundHttpRequestBodyWriterException();
                        }
                    }

                    return underlyingHttpRequest.exchange();
                } catch (Throwable ex) {
                    reject.handle(ex);
                    return null;
                }
            }
        };

        Promise<UnderlyingHttpResponse> promise = async ? new Promise<UnderlyingHttpResponse>(executor, sendRequestTask) : new Promise<UnderlyingHttpResponse>(sendRequestTask);

        return promise
                .then(new AsyncCallback<UnderlyingHttpResponse, HttpResponse<O>>() {
                          // 读取响应
                          @Override
                          public HttpResponse<O> apply(UnderlyingHttpResponse underlyingHttpResponse) {
                              try {
                                  HttpResponse<O> response = null;
                                  boolean needReadBody = needReadBody(underlyingHttpResponse);
                                  if (needReadBody) {
                                      if (underlyingHttpResponse.getStatusCode() >= 400) {
                                          response = new HttpResponse<>(underlyingHttpResponse, null, true);
                                      } else {
                                          MediaType contentType = underlyingHttpResponse.getHeaders().getContentType();
                                          HttpResponseBodyReader reader = Pipeline.of(responseBodyReaders)
                                                  .findFirst(new Predicate<HttpResponseBodyReader>() {
                                                      @Override
                                                      public boolean test(HttpResponseBodyReader httpResponseBodyReader) {
                                                          return httpResponseBodyReader.canRead(underlyingHttpResponse, contentType, responseType);
                                                      }
                                                  });
                                          if (reader != null) {
                                              O bodyEntity = reader.read(underlyingHttpResponse, contentType, responseType);
                                              response = new HttpResponse<>(underlyingHttpResponse, bodyEntity);
                                          } else {
                                              response = new HttpResponse<>(underlyingHttpResponse, null, true);
                                          }
                                      }
                                  } else {
                                      response = new HttpResponse<>(underlyingHttpResponse);
                                  }
                                  if (httpResponseErrorHandler != null && httpResponseErrorHandler.isError(response)) {
                                      httpResponseErrorHandler.handle(response);
                                  }
                                  if (responseInterceptors != null) {
                                      for (HttpResponseInterceptor interceptor : responseInterceptors) {
                                          interceptor.intercept(response);
                                      }
                                  }
                                  return response;
                              } catch (IOException ex) {
                                  throw Throwables.wrapAsRuntimeIOException(ex);
                              } finally {
                                  underlyingHttpResponse.close();
                              }
                          }
                      }
                );
    }

    private boolean needReadBody(UnderlyingHttpResponse underlyingHttpResponse) {
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

        if (!underlyingHttpResponse.getHeaders().containsKey("Transfer-Encoding")) {
            long contentLength = underlyingHttpResponse.getHeaders().getContentLength();
            if (contentLength == 0L) {
                return false;
            }
        }
        return true;
    }
}
