package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.exception.HttpRequestServerErrorException;
import com.jn.agileway.httpclient.core.exception.NotFoundHttpContentReaderException;
import com.jn.agileway.httpclient.core.exception.NotFoundHttpContentWriterException;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestHeadersInterceptor;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestMethodInterceptor;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestUriInterceptor;
import com.jn.agileway.httpclient.core.serialize.*;
import com.jn.agileway.httpclient.jdk.JdkHttpRequestFactory;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.concurrent.promise.AsyncCallback;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Promises;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.net.uri.component.UriComponentsBuilder;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.RetryInfo;
import com.jn.langx.util.retry.Retryer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class HttpExchanger extends AbstractInitializable {
    /**
     * 执行同步执行时，可以为 null
     */
    @Nullable
    private Executor executor;
    @NonNull
    private UnderlyingHttpRequestFactory requestFactory = new JdkHttpRequestFactory();
    private HttpExchangerConfiguration configuration;
    /**
     * 对请求进行拦截处理
     */
    private List<HttpRequestInterceptor> requestInterceptors = Lists.newArrayList();
    /**
     * 主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestBodyWriter> requestBodyWriters = Lists.newArrayList();

    /**
     * 对正常的响应进行反序列化
     */
    private List<HttpResponseBodyReader> responseBodyReaders = Lists.newArrayList();
    /**
     * 对4xx,5xx的响应进行处理
     */
    @Nullable
    private HttpResponseErrorHandler httpResponseErrorHandler;
    /**
     * 响应拦截器
     */
    private List<HttpResponseInterceptor> responseInterceptors = Lists.newArrayList();

    /**
     * 该方法要在 自定义的 interceptor, readers, writers 完成之后调用
     */
    @Override
    protected void doInit() throws InitializationException {
        if (configuration == null) {
            configuration = new HttpExchangerConfiguration();
        }
        // requestInterceptors
        this.requestInterceptors.add(new HttpRequestHeadersInterceptor(configuration.getFixedHeaders()));
        this.requestInterceptors.add(0, new HttpRequestUriInterceptor(configuration.getAllowedSchemes(), configuration.getAllowedAuthorities(), configuration.getNotAllowedAuthorities()));
        this.requestInterceptors.add(0, new HttpRequestMethodInterceptor(configuration.getAllowedMethods(), configuration.getNotAllowedMethods()));
        this.requestInterceptors = Lists.immutableList(requestInterceptors);


        // responseInterceptors
        this.responseInterceptors = Lists.immutableList(responseInterceptors);

        // requestBodyWriters
        this.requestBodyWriters.add(new GeneralJsonHttpRequestWriter());
        this.requestBodyWriters.add(new GeneralFormHttpRequestWriter());
        this.requestBodyWriters = Lists.immutableList(requestBodyWriters);

        // responseBodyReaders
        responseBodyReaders.add(new GeneralJsonHttpResponseReader());
        responseBodyReaders.add(new GeneralTextHttpResponseReader());
        responseBodyReaders.add(new GeneralResourceHttpResponseReader());
        responseBodyReaders.add(0, new GeneralBytesHttpResponseReader());
        this.responseBodyReaders = Lists.immutableList(responseBodyReaders);

        // httpResponseErrorHandler
        if (this.httpResponseErrorHandler == null) {
            this.httpResponseErrorHandler = new DefaultHttpResponseErrorHandler();
        }
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setConfiguration(HttpExchangerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setRequestFactory(UnderlyingHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
        if (interceptor != null) {
            this.requestInterceptors.add(interceptor);
        }
    }

    public void addResponseInterceptor(HttpResponseInterceptor interceptor) {
        if (interceptor != null) {
            this.responseInterceptors.add(interceptor);
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

    public <O> Promise<HttpResponse<O>> exchange(boolean async,
                                                 @NonNull HttpMethod method,
                                                 @NonNull String uriTemplate,
                                                 @Nullable MultiValueMap<String, Object> queryParams,
                                                 @Nullable Map<String, Object> uriVariables,
                                                 @Nullable HttpHeaders headers,
                                                 @Nullable Object body,
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

    public <O> Promise<HttpResponse<O>> exchange(boolean async, final HttpRequest request, final Type responseType) {
        Task<UnderlyingHttpResponse> sendRequestTask = new Task<UnderlyingHttpResponse>() {

            @Override
            public UnderlyingHttpResponse run(Handler<UnderlyingHttpResponse> resolve, ErrorHandler reject) {
                if (request.getMethod() == null) {
                    throw new BadHttpRequestException("HTTP method is required");
                }
                if (request.getUri() == null) {
                    throw new BadHttpRequestException("HTTP uri is required");
                }
                if (requestInterceptors != null) {
                    for (HttpRequestInterceptor interceptor : requestInterceptors) {
                        interceptor.intercept(request);
                    }
                }
                try {
                    UnderlyingHttpRequest underlyingHttpRequest = requestFactory.create(request.getMethod(), request.getUri(), request.getHeaders());

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
                            throw new NotFoundHttpContentWriterException();
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
                                          final MediaType contentType = Objs.useValueIfNull(underlyingHttpResponse.getHeaders().getContentType(), MediaType.TEXT_HTML);
                                          HttpResponseBodyReader reader = Pipeline.of(responseBodyReaders)
                                                  .findFirst(new Predicate<HttpResponseBodyReader>() {
                                                      @Override
                                                      public boolean test(HttpResponseBodyReader httpResponseBodyReader) {
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

    public <O> Promise<HttpResponse<O>> exchangeWithRetry(boolean async, HttpRequest request, Type responseType, final RetryConfig retryConfig, Callable<HttpResponse<O>> fallback) {
        return exchangeWithRetry(async, request, responseType, null, null, null, retryConfig, fallback);
    }

    public <O> Promise<HttpResponse<O>> exchangeWithRetry(boolean async, HttpRequest request, Type responseType, @Nullable Predicate<Throwable> errorRetryPredicate, @Nullable Predicate<HttpResponse<O>> resultRetryPredicate, @Nullable Consumer<RetryInfo<HttpResponse<O>>> attemptsListener, @Nullable final RetryConfig retryConfig, @Nullable Callable<HttpResponse<O>> fallback) {
        RetryConfig theRetryConfig = retryConfig != null ? retryConfig : RetryConfig.noneRetryConfig();
        Predicate<Throwable> theErrorRetryPredicate = errorRetryPredicate == null ? new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) {
                return (throwable instanceof HttpRequestServerErrorException) || Throwables.hasCause(throwable, ConnectException.class);
            }
        } : errorRetryPredicate;
        Predicate<HttpResponse<O>> theResultRetryPredicate = resultRetryPredicate == null ? new Predicate<HttpResponse<O>>() {
            @Override
            public boolean test(HttpResponse<O> oHttpResponse) {
                return oHttpResponse.hasError() && oHttpResponse.getStatusCode() >= 500;
            }
        } : resultRetryPredicate;
        return Promises.of(async ? executor : null, new Callable<HttpResponse<O>>() {
            @Override
            public HttpResponse<O> call() throws Exception {
                return Retryer.<HttpResponse<O>>execute(theErrorRetryPredicate, theResultRetryPredicate, theRetryConfig, attemptsListener, new Callable<HttpResponse<O>>() {
                    @Override
                    public HttpResponse<O> call() throws Exception {
                        return (HttpResponse<O>) exchange(async, request, responseType).await();
                    }
                }, fallback);
            }
        });
    }
}
