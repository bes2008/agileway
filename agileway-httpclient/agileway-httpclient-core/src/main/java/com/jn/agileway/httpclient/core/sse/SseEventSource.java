package com.jn.agileway.httpclient.core.sse;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.local.SimpleEventPublisher;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Supplier;
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

/**
 * Http Status Code 处理：
 * 1）301， 307 由底层 Http Client处理
 * 2）200， 代表连接成功，消息接收成功
 * 3）204， 代表响应成功，但不再有消息了
 * 4）其他，代表错误。直接 调用 close()
 */
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
    private long lastReceivedTimeInMills = -1L;

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
    private Object lock = new Object();

    public void registerEventListener(String eventTypeOrName, SseEventListener listener) {
        if (Strings.isBlank(eventTypeOrName)) {
            throw new IllegalArgumentException("eventTypeOrName is blank");
        }
        if (!inited) {
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


    public SseEventSource(String url) {
        this(url, null, null, null);
    }

    public SseEventSource(@NonNull String url,
                          @NonNull String eventDomain,
                          @NonNull HttpExchanger httpExchanger,
                          @Nullable EventPublisher eventPublisher) {
        this.url = Preconditions.checkNotNull(url, "the sse url is required");
        this.eventDomain = Objs.useValueIfEmpty(eventDomain, "SSE-" + System.currentTimeMillis());
        this.httpExchanger = Preconditions.checkNotNull(httpExchanger);
        this.eventPublisher = Objs.useValueIfNull(eventPublisher, new Supplier<EventPublisher, EventPublisher>() {
            @Override
            public EventPublisher get(EventPublisher input) {
                return new SimpleEventPublisher();
            }
        });
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public void setWithCredentials(boolean withCredentials) {
        this.withCredentials = withCredentials;
    }

    @Override
    protected void doInit() throws InitializationException {
        super.doInit();
        this.eventPublisher.addEventListener(this.eventDomain, this);
    }

    @Override
    protected void doStart() {
        super.doStart();
        pullAndHandleEvents();
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
                for (SseEventListener listener : eventListeners.get(EVENT_NAME_OPEN)) {
                    listener.on(event);
                }
                break;
            case ERROR:
                for (SseEventListener listener : eventListeners.get(EVENT_NAME_ERROR)) {
                    listener.on(event);
                }
                if (this.readyState == READY_STATE_OPEN) {
                    // reconnect if connection is closed
                } else {
                    shutdown();
                }
                break;
            case MESSAGE:
            default:
                SSE.SseMessageEvent messageEvent = (SSE.SseMessageEvent) event;
                this.lastEventId = messageEvent.getLastEventId();
                long retryDelay = messageEvent.retry();
                if (retryDelay > 0) {
                    this.reconnectInterval = retryDelay;
                }

                String name = messageEvent.getName();

                Collection<SseEventListener> listeners = eventListeners.containsKey(name) ? eventListeners.get(name) : eventListeners.get(EVENT_NAME_MESSAGE);
                for (SseEventListener listener : listeners) {
                    listener.on(event);
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

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
        if (withCredentials) {
            //
        }
        if (Strings.isNotBlank(lastEventId)) {
            headers.add("Last-Event-ID", lastEventId);
        }
        // do re-connect
        HttpRequest request = HttpRequest.create(HttpMethod.GET, url, null, null, null, headers, null);
        HttpResponse<InputStream> response = null;
        try {
            response = httpExchanger.exchange(request);
            this.response = response;
            this.lastReceivedTimeInMills = System.currentTimeMillis();
        } catch (Throwable ex) {
            LOGGER.warn("The sse event source is connect failed for domain: {}, will not to reconnect, exception: {}", eventDomain, ex.getMessage(), ex);
            eventPublisher.publish(new SSE.SseErrorEvent(this, -1, ex.getMessage()));
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
            eventPublisher.publish(SSE.SseMessageEvent.ofOpen(this));

            // 读取数据
            if (READY_STATE_OPEN == readyState) {
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
                                eventPublisher.publish(event);
                            }
                        } else {
                            if (line.startsWith(":")) {
                                // comment line, ignore it
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
                                throw new IllegalArgumentException("illegal line for sse event stream: " + line);
                            }
                        }
                    }
                });
                SSE.SseMessageEventBuilder builder = builderHolder.get();
                if (builder != null) {
                    SSE.SseMessageEvent event = builder.build();
                    builderHolder.set(null);
                    if (event != null) {
                        eventPublisher.publish(event);
                    }
                }

                if (READY_STATE_CLOSED != readyState) {
                    if (this.reconnectInterval > 0) {
                        long waitTime = lastReceivedTimeInMills + this.reconnectInterval - System.currentTimeMillis();
                        if (waitTime > 0) {
                            synchronized (this.lock) {
                                try {
                                    this.wait(waitTime);
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                            }
                        }
                    }
                }
            }

        } catch (Throwable ex) {
            eventPublisher.publish(new SSE.SseErrorEvent(this, response.getStatusCode(), ex.getMessage()));
        }

    }

    private SSE.SseErrorEvent createErrorEventIfInvalidResponse(HttpResponse response) {
        if (response.getStatusCode() == 204) {
            return new SSE.SseErrorEvent(this, 204, StringTemplates.formatWithPlaceholder("sse closed by server"));
        }
        if (response.hasError() || response.getStatusCode() != 200) {
            return new SSE.SseErrorEvent(this, response.getStatusCode(), response.getErrorMessage());
        }

        MediaType contentType = response.getHttpHeaders().getContentType();
        if (contentType == null || !MediaType.TEXT_EVENT_STREAM.equalsTypeAndSubtype(contentType)) {
            return new SSE.SseErrorEvent(this, response.getStatusCode(), StringTemplates.formatWithPlaceholder("invalid content-type in response: {}", contentType));
        }
        return null;
    }

    @Override
    protected void doStop() {
        super.doStop();
        this.readyState = READY_STATE_CLOSED;
        this.eventListeners.clear();
        closeUnderlyingResponse(this.response);
    }
}
