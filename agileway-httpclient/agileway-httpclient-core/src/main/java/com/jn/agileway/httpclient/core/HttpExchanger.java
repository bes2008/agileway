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
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.Retryer;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class HttpExchanger extends AbstractInitializable {

    private Executor executor;
    private UnderlyingHttpRequestFactory requestFactory;

    /**
     * 对请求进行拦截处理
     */
    private List<HttpRequestInterceptor> builtinRequestInterceptors = Lists.asList(new ContentTypeHttpRequestInterceptor());
    private List<HttpRequestInterceptor> customRequestInterceptors = Lists.newArrayList();
    private List<HttpRequestInterceptor> requestInterceptors;
    /**
     * 主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestBodyWriter> requestBodyWriters;

    /**
     * 对响应进行反序列化
     */
    private List<HttpResponseBodyReader> responseBodyReaders;

    @Override
    protected void doInit() throws InitializationException {
        List<HttpRequestInterceptor> interceptors = Lists.newArrayList();
        if (customRequestInterceptors != null) {
            interceptors.addAll(customRequestInterceptors);
        }
        interceptors.addAll(builtinRequestInterceptors);
        this.requestInterceptors = Lists.immutableList(interceptors);
    }

    public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
        if (interceptor != null) {
            this.customRequestInterceptors.add(interceptor);
        }
    }

    public <I, O> Promise<HttpResponse<O>> exchange(boolean async,
                                                    @NonNull HttpMethod method,
                                                    @NonNull String uriTemplate,
                                                    @Nullable MultiValueMap<String, String> queryParams,
                                                    @Nullable Map<String, Object> uriVariables,
                                                    @Nullable HttpHeaders headers,
                                                    @Nullable I body,
                                                    @Nullable final Class responseType,
                                                    @Nullable final RetryConfig retryConfig) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriTemplate);
        if (queryParams != null) {
            uriBuilder.queryParams(queryParams);
        }
        if (uriVariables != null) {
            uriBuilder.uriVariables(uriVariables);
        }
        URI uri = uriBuilder.build().toUri();

        return exchange(async, new HttpRequest(uri, method, headers, body), responseType, retryConfig);
    }

    public <I, O> Promise<HttpResponse<O>> exchange(boolean async, final HttpRequest request, final Class responseType, @Nullable final RetryConfig retryConfig) {
        Task<UnderlyingHttpResponse> sendRequestTask = new Task<UnderlyingHttpResponse>() {

            @Override
            public UnderlyingHttpResponse run(Handler<UnderlyingHttpResponse> resolve, ErrorHandler reject) {
                RetryConfig theRetryConfig = retryConfig;
                if (theRetryConfig == null) {
                    theRetryConfig = RetryConfig.noneRetryConfig();
                }

                if (request.getMethod() == null) {
                    throw new HttpRequestInvalidException("HTTP method is required");
                }
                if (request.getUri() == null) {
                    throw new HttpRequestInvalidException("HTTP uri is required");
                }

                try {
                    return Retryer.<UnderlyingHttpResponse>execute(new Predicate<Throwable>() {
                        @Override
                        public boolean test(Throwable ex) {
                            return false;
                        }
                    }, new Predicate<UnderlyingHttpResponse>() {
                        @Override
                        public boolean test(UnderlyingHttpResponse value) {
                            return false;
                        }
                    }, theRetryConfig, null, new Callable<UnderlyingHttpResponse>() {
                        @Override
                        public UnderlyingHttpResponse call() throws Exception {

                            for (HttpRequestInterceptor interceptor : requestInterceptors) {
                                interceptor.intercept(request);
                            }

                            UnderlyingHttpRequest underlyingHttpRequest = requestFactory.create(request.getMethod(), request.getUri(), request.getHeaders().getContentType());
                            underlyingHttpRequest.addHeaders(request.getHeaders());

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
                        }
                    });

                } catch (Exception ex) {
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
                              HttpResponse<O> response = null;
                              if (underlyingHttpResponse.getStatusCode() >= 400) {
                                  response = new HttpResponse<>(underlyingHttpResponse, null, true);
                                  return response;
                              }

                              MediaType contentType = underlyingHttpResponse.getHeaders().getContentType();
                              boolean needReadBody = needReadBody(underlyingHttpResponse);
                              try {
                                  if (needReadBody) {
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
                                  } else {
                                      response = new HttpResponse<>(underlyingHttpResponse);
                                  }
                                  return response;
                              } catch (IOException ex) {
                                  throw Throwables.wrapAsRuntimeIOException(ex);
                              } finally {
                                  underlyingHttpResponse.close();
                              }
                          }
                      }
                )
                .then(new AsyncCallback<HttpResponse<O>, HttpResponse<O>>() {
                    @Override
                    public HttpResponse<O> apply(HttpResponse<O> httpResponse) {

                        return null;
                    }
                })
                .catchError(new AsyncCallback<Throwable, HttpResponse<O>>() {
                    @Override
                    public HttpResponse<O> apply(Throwable ex) {
                        return null;
                    }
                });
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
