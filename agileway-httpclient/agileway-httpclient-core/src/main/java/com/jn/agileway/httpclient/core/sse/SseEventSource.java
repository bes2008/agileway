package com.jn.agileway.httpclient.core.sse;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.struct.Holder;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

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
    private boolean withCredentials;

    private String lastEventId;

    /**
     * A number representing the number of milliseconds to wait between messages before retrying the connection.
     */
    private long reconnectInterval;

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

    /**
     * 如果客户端想要终止重新连接，则需要调用此方法。
     * 如果服务器想要终止重新连接，则需要返回 http status 204
     *
     * @throws IOException
     */

    private MultiValueMap<String, SseEventListener> eventListeners = new CommonMultiValueMap<>();
    @NonNull
    private EventPublisher eventPublisher;

    @NonNull
    private String eventDomain;

    @Nullable
    private HttpResponse response;

    @NonNull
    private HttpExchanger httpExchanger;

    public void registerEventListener(String eventTypeOrName, SseEventListener listener) {
        if (Strings.isBlank(eventTypeOrName)) {
            throw new IllegalArgumentException("eventTypeOrName is blank");
        }
        eventListeners.add(eventTypeOrName, listener);
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
        this(url, false);
    }

    public SseEventSource(String url, boolean withCredentials) {
        this(null, null, "SSE", url, withCredentials);
    }

    public SseEventSource(HttpExchanger httpExchanger, EventPublisher eventPublisher, String eventDomain, String url, boolean withCredentials) {
        this.httpExchanger = httpExchanger;
        this.eventDomain = Objs.useValueIfEmpty(eventDomain, "SSE");
        this.url = url;
        this.withCredentials = withCredentials;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void doStart() {
        super.doStart();
        this.eventPublisher.addEventListener(this.eventDomain, this);
    }

    @Override
    public void on(SseEvents.SseEvent event) {
        SseEvents.SseEventType eventType = event.getType();
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
                break;
            case MESSAGE:
                SseEvents.SseMessageEvent messageEvent = (SseEvents.SseMessageEvent) event;
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

    void reconnect() {
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
        headers.add(HttpHeaders.ACCEPT, "text/event-stream");
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
        } catch (Throwable ex) {
            LOGGER.warn("The sse event source is connect failed for domain: {}, will not to reconnect, exception: {}", eventDomain, ex.getMessage(), ex);
            eventPublisher.publish(new SseEvents.SseErrorEvent(this, -1, ex.getMessage()));
            return;
        }
        SseEvents.SseErrorEvent errorEvent = errorEventIfInvalidResponse(response);
        if (errorEvent != null) {
            eventPublisher.publish(errorEvent);
        }
        readyState = READY_STATE_OPEN;
        try {
            eventPublisher.publish(SseEvents.SseMessageEvent.ofOpen(this));

            // 读取数据
            while (READY_STATE_OPEN == readyState) {
                final Holder<SseEvents.SseMessageEventBuilder> builderHolder = new Holder<>();
                Resources.readLines(Resources.asInputStreamResource(response.getPayload(), "the sse event stream"), Charsets.UTF_8, new Consumer<String>() {
                    @Override
                    public void accept(String line) {
                        SseEvents.SseMessageEventBuilder builder = builderHolder.get();
                        if (builder == null) {
                            builder = SseEvents.SseMessageEventBuilder.newBuilder(SseEventSource.this);
                            builderHolder.set(builder);
                        }
                        if (Strings.isBlank(line)) {
                            SseEvents.SseMessageEvent event = builder.build();
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
            }

        } catch (Throwable ex) {
            eventPublisher.publish(new SseEvents.SseErrorEvent(this, response.getStatusCode(), ex.getMessage()));
        }

    }

    private SseEvents.SseErrorEvent errorEventIfInvalidResponse(HttpResponse response) {
        if (response.getStatusCode() == 204) {
            return new SseEvents.SseErrorEvent(this, 204, StringTemplates.formatWithPlaceholder("sse closed by server"));
        }
        if (response.hasError() || response.getStatusCode() != 200) {
            return new SseEvents.SseErrorEvent(this, response.getStatusCode(), response.getErrorMessage());
        }

        MediaType contentType = response.getHttpHeaders().getContentType();
        if (contentType == null || !MediaType.TEXT_EVENT_STREAM.equalsTypeAndSubtype(contentType)) {
            return new SseEvents.SseErrorEvent(this, response.getStatusCode(), StringTemplates.formatWithPlaceholder("invalid content-type in response: {}", contentType));
        }
        return null;
    }
}
