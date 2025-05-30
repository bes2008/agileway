package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.error.DefaultHttpResponseErrorHandler;
import com.jn.agileway.httpclient.core.error.HttpResponseErrorHandler;
import com.jn.agileway.httpclient.core.error.exception.*;
import com.jn.agileway.httpclient.core.interceptor.*;
import com.jn.agileway.httpclient.core.content.multipart.MultiPartsForm;
import com.jn.agileway.httpclient.core.content.*;
import com.jn.agileway.httpclient.core.plugin.*;
import com.jn.agileway.httpclient.core.underlying.*;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.plugin.PluginRegistry;
import com.jn.langx.plugin.SimpleLoadablePluginRegistry;
import com.jn.langx.plugin.SpiPluginLoader;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.concurrent.promise.AsyncCallback;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Promises;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.RetryInfo;
import com.jn.langx.util.retry.Retryer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.*;

/**
 * 建议的做法是，一个系统里，只创建一个 HttpExchanger 对象即可。一个 HttpExchanger通常会关联一个 ExecutorService （线程池）
 */
public class HttpExchanger extends AbstractInitializable {
    @NonNull
    private UnderlyingHttpRequestFactory requestFactory;
    private HttpExchangerConfiguration configuration;
    /**
     * 对请求进行拦截处理
     */
    private List<HttpRequestInterceptor> requestInterceptors = Lists.newArrayList();
    /**
     * 主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestContentWriter> requestContentWriters = Lists.newArrayList();

    /**
     * 对正常响应进行反序列化
     */
    private List<HttpResponseContentReader> responseContentReaders = Lists.newArrayList();
    /**
     * 对4xx,5xx的响应进行处理
     */
    @Nullable
    private HttpResponseErrorHandler globalHttpResponseErrorHandler;
    /**
     * 响应拦截器
     */
    private List<HttpResponseInterceptor> responseInterceptors = Lists.newArrayList();

    private PluginRegistry httpMessagePluginRegistry;


    /**
     * 该方法要在 自定义的 interceptor, readers, writers 完成之后调用
     */
    @Override
    protected void doInit() throws InitializationException {
        if (configuration == null) {
            configuration = new HttpExchangerConfiguration();
        }

        if (this.httpMessagePluginRegistry == null) {
            SimpleLoadablePluginRegistry httpMessagePluginRegistry = new SimpleLoadablePluginRegistry();
            httpMessagePluginRegistry.setPluginLoader(new SpiPluginLoader());
            this.httpMessagePluginRegistry = httpMessagePluginRegistry;
        }

        List<HttpMessageProtocolPlugin> plugins = this.httpMessagePluginRegistry.find(HttpMessageProtocolPlugin.class);

        // requestInterceptors
        for (HttpMessageProtocolPlugin plugin : plugins) {
            this.requestInterceptors.add(new PluginBasedHttpRequestInterceptor(plugin));
        }
        this.requestInterceptors.add(new HttpRequestMultiPartsFormInterceptor());
        this.requestInterceptors.add(new HttpRequestHeadersInterceptor(configuration.getFixedHeaders()));
        this.requestInterceptors.add(new HttpRequestLoggingInterceptor());
        this.requestInterceptors.add(0, new HttpRequestUriInterceptor(configuration.getAllowedSchemes(), configuration.getAllowedAuthorities(), configuration.getNotAllowedAuthorities()));
        this.requestInterceptors.add(0, new HttpRequestMethodInterceptor(configuration.getAllowedMethods(), configuration.getNotAllowedMethods()));
        this.requestInterceptors = Lists.immutableList(requestInterceptors);


        // responseInterceptors
        this.responseInterceptors.add(new HttpResponseLoggingInterceptor());
        for (HttpMessageProtocolPlugin plugin : plugins) {
            this.responseInterceptors.add(new PluginBasedHttpResponseInterceptor(plugin));
        }
        this.responseInterceptors = Lists.immutableList(responseInterceptors);

        // requestBodyWriters
        for (HttpMessageProtocolPlugin plugin : plugins) {
            this.requestContentWriters.add(new PluginBasedHttpRequestWriter(plugin));
        }
        this.requestContentWriters.add(new GeneralFormHttpRequestWriter());
        this.requestContentWriters.add(new GeneralMultiPartsFormHttpRequestWriter());
        this.requestContentWriters = Lists.immutableList(requestContentWriters);

        // responseBodyReaders
        for (HttpMessageProtocolPlugin plugin : plugins) {
            responseContentReaders.add(new PluginBasedHttpResponseReader(plugin));
        }
        this.responseContentReaders.add(new GeneralAttachmentReader());
        this.responseContentReaders.add(new GeneralTextHttpResponseReader());
        this.responseContentReaders.add(new GeneralResourceHttpResponseReader());
        this.responseContentReaders.add(0, new GeneralBytesHttpResponseReader());
        this.responseContentReaders = Lists.immutableList(responseContentReaders);

        // httpResponseErrorHandler
        if (this.globalHttpResponseErrorHandler == null) {
            this.globalHttpResponseErrorHandler = new DefaultHttpResponseErrorHandler();
        }

        if (this.requestFactory == null) {
            UnderlyingHttpRequestFactoryBuilder requestFactoryBuilder = UnderlyingHttpRequestFactoryBuilderSupplier.getInstance().get();
            requestFactoryBuilder.connectTimeoutMills(configuration.getConnectTimeoutMillis());
            requestFactoryBuilder.readTimeoutMills(configuration.getConnectTimeoutMillis());
            requestFactoryBuilder.keepAliveDurationMills(configuration.getKeepAliveDurationMills());
            requestFactoryBuilder.hostnameVerifier(configuration.getHostnameVerifier());
            requestFactoryBuilder.sslContextBuilder(configuration.getSslContextBuilder());
            requestFactoryBuilder.poolMaxIdleConnections(configuration.getPoolMaxIdleConnections());
            requestFactoryBuilder.executor(configuration.getExecutor());
            this.requestFactory = requestFactoryBuilder.build();
        }
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

    public void addRequestContentWriter(HttpRequestContentWriter writer) {
        if (writer != null) {
            this.requestContentWriters.add(writer);
        }
    }

    public void addResponseContentReader(HttpResponseContentReader reader) {
        if (reader != null) {
            this.responseContentReaders.add(reader);
        }
    }

    public void setGlobalHttpResponseErrorHandler(HttpResponseErrorHandler errorResponseHandler) {
        this.globalHttpResponseErrorHandler = errorResponseHandler;
    }
    public <O> Promise<HttpResponse<O>> exchange(boolean async, final HttpRequest request, final Type responseType) {
        return exchange(async, request, responseType, null, null);
    }

    public <O> Promise<HttpResponse<O>> exchange(boolean async, final HttpRequest request, final Type responseType, HttpResponseContentExtractor contentExtractor) {
        return exchange(async, request, responseType, contentExtractor, null);
    }

    /**
     * @param async
     * @param request
     * @param responseType
     * @param contentExtractor 用于覆盖默认的 HttpResponseContentReader 的能力
     * @param <O>
     * @return
     */
    public <O> Promise<HttpResponse<O>> exchange(boolean async, final HttpRequest request, final Type responseType, HttpResponseContentExtractor contentExtractor, HttpResponseContentExtractor errorContentExtractor) {
        Task<UnderlyingHttpResponse> sendRequestTask = new Task<UnderlyingHttpResponse>() {

            @Override
            public UnderlyingHttpResponse run(Handler<UnderlyingHttpResponse> resolve, ErrorHandler reject) {
                if (requestInterceptors != null) {
                    for (HttpRequestInterceptor interceptor : requestInterceptors) {
                        interceptor.intercept(request);
                    }
                }
                try {
                    UnderlyingHttpRequest underlyingHttpRequest = requestFactory.create(request.getMethod(), request.getUri(), request.getHeaders());

                    if (HttpClientUtils.isWriteable(request.getMethod()) && request.getPayload() != null) {
                        HttpRequestContentWriter requestBodyWriter = Pipeline.of(requestContentWriters)
                                .findFirst(new Predicate<HttpRequestContentWriter>() {
                                    @Override
                                    public boolean test(HttpRequestContentWriter writer) {
                                        return writer.canWrite(request);
                                    }
                                });
                        if (requestBodyWriter != null) {
                            requestBodyWriter.write(request, underlyingHttpRequest);
                        } else {
                            throw new NotFoundHttpContentWriterException();
                        }
                    }

                    return underlyingHttpRequest.exchange();
                } catch (Exception ex) {
                    reject.handle(ex);
                    return null;
                }
            }
        };

        Promise<UnderlyingHttpResponse> promise = async ? new Promise<UnderlyingHttpResponse>(this.configuration.getExecutor(), sendRequestTask) : new Promise<UnderlyingHttpResponse>(sendRequestTask);

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
                                              final MediaType contentType = Objs.useValueIfNull(underlyingHttpResponse.getHeaders().getContentType(), MediaType.TEXT_HTML);
                                              HttpResponseContentReader reader = Pipeline.of(responseContentReaders)
                                                      .findFirst(new Predicate<HttpResponseContentReader>() {
                                                          @Override
                                                          public boolean test(HttpResponseContentReader httpResponseBodyReader) {
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
                                  if (globalHttpResponseErrorHandler != null && globalHttpResponseErrorHandler.isError(response)) {
                                      globalHttpResponseErrorHandler.handle(response);
                                  }
                                  if (responseInterceptors != null) {
                                      for (HttpResponseInterceptor interceptor : responseInterceptors) {
                                          interceptor.intercept(response);
                                      }
                                  }
                                  return response;
                              } catch (Exception ex) {
                                  throw Throwables.wrapAsRuntimeException(ex);
                              } finally {
                                  underlyingHttpResponse.close();
                              }
                          }
                      }
                );
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
                if (request.getPayload() instanceof MultiPartsForm) {
                    return false;
                }
                return (throwable instanceof HttpRequestServerErrorException) || Throwables.hasCause(throwable, ConnectException.class);
            }
        } : errorRetryPredicate;
        Predicate<HttpResponse<O>> theResultRetryPredicate = resultRetryPredicate == null ? new Predicate<HttpResponse<O>>() {
            @Override
            public boolean test(HttpResponse<O> oHttpResponse) {
                if (request.getPayload() instanceof MultiPartsForm) {
                    return false;
                }
                return oHttpResponse.hasError() && oHttpResponse.getStatusCode() >= 500;
            }
        } : resultRetryPredicate;
        return Promises.of(async ? this.configuration.getExecutor() : null, new Callable<HttpResponse<O>>() {
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

    public void setHttpMessagePluginRegistry(PluginRegistry httpMessagePluginRegistry) {
        if (httpMessagePluginRegistry != null) {
            this.httpMessagePluginRegistry = httpMessagePluginRegistry;
        }
    }
}
