package com.jn.agileway.httpclient.core.sse;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpRequestBuilder;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.error.exception.HttpRequestClientErrorException;
import com.jn.agileway.httpclient.core.error.exception.HttpRequestServerErrorException;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.local.SimpleEventPublisher;
import com.jn.langx.exception.ParseException;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.id.uuidv7.UUIDv7Factory;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.struct.Holder;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.Executor;

import static com.jn.agileway.httpclient.core.MessageHeaderConstants.SSE_UNDERLYING_RESPONSE;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SseEventSource extends AbstractLifecycle implements SseEventListener {
    private static final Logger LOGGER = Loggers.getLogger(SseEventSource.class);
    /**
     * A string representing the URL of the source.
     */
    private String url;


    /**
     * A number representing the state of the connection. Possible values are CONNECTING (0), OPEN (1), or CLOSED (2).
     */
    private volatile int readyState;

    /**
     * A boolean value indicating whether the EventSource object was instantiated with cross-origin (CORS) credentials set (true), or not (false, the default).
     */
    private boolean withCredentials = false;

    private String lastEventId;

    /**
     * A number representing the number of milliseconds to wait between messages before retrying the connection.
     */
    private long reconnectInterval = 3 * 1000L;
    /**
     * A number representing the number of milliseconds since the last message was received.
     */
    private volatile long lastReceivedTimeInMills = -1L;

    /**
     * The connection has not yet been established, or it was closed and the user agent is reconnecting.
     */
    private static final int READY_STATE_CONNECTING = 0;
    /**
     * The user agent has an open connection and is dispatching events as it receives them.
     */
    private static final int READY_STATE_OPEN = 1;
    /**
     * The connection is not open, and the user agent is not trying to reconnect. Either there was a fatal error or the close() method was invoked.
     */
    private static final int READY_STATE_CLOSED = 2;

    private static final String EVENT_NAME_OPEN = "open";
    private static final String EVENT_NAME_ERROR = "error";
    static final String EVENT_NAME_MESSAGE = "message";
    private MultiValueMap<String, SseEventListener> eventListeners = new CommonMultiValueMap<>();
    @NonNull
    private EventPublisher eventPublisher;

    @NonNull
    private String eventDomain;

    @Nullable
    private HttpResponse response;

    @NonNull
    private HttpExchanger httpExchanger;
    private HttpRequestBuilder sseRequestBuilder;
    private Predicate<SSE.SseErrorEvent> reconnectPredicate = new Predicate<SSE.SseErrorEvent>() {
        @Override
        public boolean test(SSE.SseErrorEvent event) {
            SSE.SseErrorType type = event.getErrorType();
            if (type == SSE.SseErrorType.NO_CONTENT
                    || type == SSE.SseErrorType.UNSUPPORTED_CONTENT_TYPE
                    || type == SSE.SseErrorType.CLIENT_READ_ERROR
                    || type == SSE.SseErrorType.REQUEST_ERROR
            ) {
                return false;
            }
            return true;
        }
    };

    public void setReconnectPredicate(Predicate<SSE.SseErrorEvent> reconnectPredicate) {
        if (!inited && reconnectPredicate != null) {
            this.reconnectPredicate = reconnectPredicate;
        }
    }

    public void registerEventListener(String eventTypeOrName, SseEventListener listener) {
        if (Strings.isBlank(eventTypeOrName)) {
            throw new IllegalArgumentException("eventTypeOrName is blank");
        }
        if (!inited && listener != null) {
            eventListeners.add(eventTypeOrName, listener);
        }
    }

    public void registerOpenEventListener(SseEventListener listener) {
        registerEventListener(EVENT_NAME_OPEN, listener);
    }

    public void registerErrorEventListener(SseEventListener listener) {
        registerEventListener(EVENT_NAME_ERROR, listener);
    }

    public void registerMessageEventListener(SseEventListener listener) {
        registerEventListener(EVENT_NAME_MESSAGE, listener);
    }

    public String getUrl() {
        return url;
    }


    public SseEventSource(String url, HttpExchanger httpExchanger) {
        this(url, null, httpExchanger, null);
    }

    public SseEventSource(@NonNull String url,
                          @NonNull String eventDomain,
                          @NonNull HttpExchanger httpExchanger,
                          @Nullable EventPublisher eventPublisher) {
        this.url = Preconditions.checkNotNull(url, "the sse url is required");
        this.eventDomain = Objs.useValueIfEmpty(eventDomain, "SSE-" + UUIDv7Factory.builder().build().create());
        this.httpExchanger = Preconditions.checkNotNull(httpExchanger);
        this.eventPublisher = Objs.useValueIfNull(eventPublisher, new Supplier<EventPublisher, EventPublisher>() {
            @Override
            public EventPublisher get(EventPublisher input) {
                return new SimpleEventPublisher();
            }
        });
        this.sseRequestBuilder = new HttpRequestBuilder(HttpMethod.GET, url);
    }

    public void setWithCredentials(boolean withCredentials) {
        this.withCredentials = withCredentials;
    }

    public void setRequestBuilder(HttpRequestBuilder requestBuilder) {
        if (requestBuilder != null && !inited) {
            this.sseRequestBuilder = requestBuilder;
        }
    }

    @Override
    protected void doInit() throws InitializationException {
        super.doInit();
        this.eventPublisher.addEventListener(this.eventDomain, this);
    }


    public void startupAsync(Executor executor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                startup();
            }
        });

    }

    @Override
    public void on(SSE.SseEvent event) {
        SSE.SseEventType eventType = event.getType();
        switch (eventType) {
            case OPEN:
                this.readyState = READY_STATE_OPEN;
                try {
                    for (SseEventListener listener : eventListeners.get(EVENT_NAME_OPEN)) {
                        listener.on(event);
                    }
                } catch (Throwable ex) {
                    LOGGER.error("error when handle sse open event: {}, error:{},", event, ex.getMessage(), ex);
                }
                break;
            case ERROR:
                try {
                    for (SseEventListener listener : eventListeners.get(EVENT_NAME_ERROR)) {
                        listener.on(event);
                    }
                } catch (Throwable ex) {
                    LOGGER.error("error when handle sse error event: {}, error:{},", event, ex.getMessage(), ex);
                }
                if (this.readyState != READY_STATE_CLOSED) {
                    if (reconnectPredicate.test((SSE.SseErrorEvent) event)) {
                        waitForReconnect();
                        pullAndHandleEvents();
                    } else {
                        this.readyState = READY_STATE_CLOSED;
                        shutdown();
                    }
                } else {
                    shutdown();
                }
                break;
            case MESSAGE:
            default:
                SSE.SseMessageEvent messageEvent = (SSE.SseMessageEvent) event;
                this.lastEventId = messageEvent.getId();
                long retryDelay = messageEvent.retry();
                if (retryDelay > 0) {
                    this.reconnectInterval = retryDelay;
                }

                String name = messageEvent.getName();

                try {
                    Collection<SseEventListener> listeners = eventListeners.containsKey(name) ? eventListeners.get(name) : eventListeners.get(EVENT_NAME_MESSAGE);
                    if (Objs.isEmpty(listeners)) {
                        LOGGER.warn("No sse listener for sse event: {}", event);
                    } else {
                        for (SseEventListener listener : listeners) {
                            listener.on(event);
                        }
                    }
                } catch (Throwable ex) {
                    LOGGER.error("error when handle sse message event: {}, error:{},", event, ex.getMessage(), ex);
                }
                break;
        }
    }

    public String getEventDomain() {
        return eventDomain;
    }

    private void closeUnderlyingResponse(HttpResponse response) {
        if (response != null) {
            UnderlyingHttpResponse underlyingHttpResponse = (UnderlyingHttpResponse) response.getHeaders().get(SSE_UNDERLYING_RESPONSE);
            if (underlyingHttpResponse == null) {
                throw new IllegalStateException("The underlying http response is null");
            }
            underlyingHttpResponse.close();
        }
    }

    void pullAndHandleEvents() {
        if (READY_STATE_CLOSED == readyState) {
            LOGGER.warn("The sse event source is closed for domain: {}, will not to reconnect", eventDomain);
            return;
        }
        // 关闭 response
        if (response != null) {
            closeUnderlyingResponse(response);
        }

        readyState = READY_STATE_CONNECTING;
        LOGGER.info("The sse event source is reconnecting for domain: {}", eventDomain);

        if (withCredentials) {
            //
        }
        if (Strings.isNotBlank(lastEventId)) {
            this.sseRequestBuilder.setHeader("Last-Event-ID", lastEventId);
        }
        // do re-connect
        HttpRequest request = this.sseRequestBuilder.setHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .uriTemplate(this.url).build();
        HttpResponse<InputStream> response;
        try {
            response = httpExchanger.exchange(request);
            this.response = response;
        } catch (HttpRequestClientErrorException ex) {
            eventPublisher.publish(new SSE.SseErrorEvent(this, SSE.SseErrorType.REQUEST_ERROR, null, ex));
            return;
        } catch (HttpRequestServerErrorException ex) {
            eventPublisher.publish(new SSE.SseErrorEvent(this, SSE.SseErrorType.SERVER_ERROR, null, ex));
            return;
        } catch (Throwable ex) {
            LOGGER.warn("The sse event source is connect failed for domain: {}, exception: {}", eventDomain, ex.getMessage(), ex);
            eventPublisher.publish(new SSE.SseErrorEvent(this, SSE.SseErrorType.CONNECT_FAILED, null, ex));
            return;
        }
        SSE.SseErrorEvent errorEvent = createErrorEventIfInvalidResponse(response);
        if (errorEvent != null) {
            eventPublisher.publish(errorEvent);
            return;
        }
        readyState = READY_STATE_OPEN;
        this.lastReceivedTimeInMills = System.currentTimeMillis();
        try {
            eventPublisher.publish(SSE.SseEvent.ofOpen(this));
        } catch (Throwable ex) {
            eventPublisher.publish(new SSE.SseErrorEvent(this, SSE.SseErrorType.UNKNOWN_ERROR, response, ex));
            return;
        }

        // 读取数据
        if (READY_STATE_OPEN == readyState) {
            // 这是一个阻塞流，只要服务端在写数据，这里就会阻塞，直到服务端写完数据或者异常
            try {
                final Holder<SSE.SseMessageEventBuilder> builderHolder = new Holder<>();
                Resources.readLines(Resources.asInputStreamResource(response.getPayload(), "the sse event stream"), Charsets.UTF_8, new Consumer<String>() {
                    @Override
                    public void accept(String line) {
                        SSE.SseMessageEventBuilder builder = builderHolder.get();
                        if (builder == null) {
                            builder = SSE.SseMessageEventBuilder.newBuilder(SseEventSource.this);
                            builderHolder.set(builder);
                        }
                        if (Strings.isBlank(line)) {
                            SSE.SseMessageEvent event = builder.build();
                            builderHolder.set(null);
                            if (event != null) {
                                SseEventSource.this.lastReceivedTimeInMills = System.currentTimeMillis();
                                eventPublisher.publish(event);
                            }
                        } else {
                            if (line.startsWith(":")) {
                                // comment line, ignore it
                                LOGGER.debug("Sse {} ignored comment: {}", url, line);
                                return;
                            }
                            if (line.startsWith("event:")) {
                                builder.withEventName(Strings.substring(line, 6));
                            } else if (line.startsWith("data:")) {
                                builder.appendData(Strings.substring(line, 5));
                            } else if (line.startsWith("id:")) {
                                builder.withLastEventId(Strings.substring(line, 3));
                            } else if (line.startsWith("retry:")) {
                                builder.withRetry(Long.parseLong(Strings.substring(line, 6).trim()));
                            } else {
                                // illegal line
                                throw new ParseException("illegal line for sse event stream: " + line);
                            }
                        }
                    }
                });
                SSE.SseMessageEventBuilder builder = builderHolder.get();
                if (builder != null) {
                    SSE.SseMessageEvent event = builder.build();
                    builderHolder.set(null);
                    if (event != null) {
                        this.lastReceivedTimeInMills = System.currentTimeMillis();
                        eventPublisher.publish(event);
                    }
                }
                // 走到这里，说明读取数据已经结束，原因是 server 端主动关闭了连接
                eventPublisher.publish(new SSE.SseErrorEvent(this, SSE.SseErrorType.CONNECTION_CLOSED_BY_PEER, response, null));
            } catch (ParseException ex) {
                eventPublisher.publish(new SSE.SseErrorEvent(this, SSE.SseErrorType.DATA_MALFORMED_ERROR, response, ex));
            } catch (Throwable ex) {
                eventPublisher.publish(new SSE.SseErrorEvent(this, SSE.SseErrorType.UNKNOWN_ERROR, response, ex));
            }
        }

    }

    private void waitForReconnect() {
        if (READY_STATE_CLOSED != readyState) {
            if (this.reconnectInterval > 0) {
                long waitTime = lastReceivedTimeInMills + this.reconnectInterval - System.currentTimeMillis();
                while (READY_STATE_CLOSED != readyState && waitTime > 0) {
                    synchronized (this) {
                        try {
                            this.wait(10);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                    waitTime = lastReceivedTimeInMills + this.reconnectInterval - System.currentTimeMillis();
                }
            }
        }
    }

    private SSE.SseErrorEvent createErrorEventIfInvalidResponse(HttpResponse response) {
        Preconditions.checkNotNull(response, "the response is null");
        if (response.getStatusCode() == 204) {
            return new SSE.SseErrorEvent(this, SSE.SseErrorType.NO_CONTENT, response, null);
        }
        int statusCode = response.getStatusCode();
        if (statusCode >= 500) {
            return new SSE.SseErrorEvent(this, SSE.SseErrorType.SERVER_ERROR, response, null);
        }
        if (statusCode >= 400) {
            return new SSE.SseErrorEvent(this, SSE.SseErrorType.REQUEST_ERROR, response, null);
        }
        MediaType contentType = response.getHttpHeaders().getContentType();
        if (contentType == null || !MediaType.TEXT_EVENT_STREAM.equalsTypeAndSubtype(contentType)) {
            return new SSE.SseErrorEvent(this, SSE.SseErrorType.UNSUPPORTED_CONTENT_TYPE, response, null);
        }
        return null;
    }

    @Override
    protected void doStart() {
        super.doStart();
        pullAndHandleEvents();
    }

    @Override
    protected void doStop() {
        super.doStop();
        this.readyState = READY_STATE_CLOSED;
        this.eventListeners.clear();
        closeUnderlyingResponse(this.response);
    }
}
