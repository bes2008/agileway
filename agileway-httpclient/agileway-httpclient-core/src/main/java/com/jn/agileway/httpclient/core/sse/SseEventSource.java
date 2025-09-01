package com.jn.agileway.httpclient.core.sse;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;

import java.io.IOException;
import java.util.Collection;

/**
 * Http Status Code 处理：
 * 1）301， 307 由底层 Http Client处理
 * 2）200， 代表连接成功，消息接收成功
 * 3）204， 代表响应成功，但不再有消息了
 * 4）其他，代表错误。直接 调用 close()
 */
public class SseEventSource extends AbstractLifecycle implements SseEventListener {
    /**
     * A string representing the URL of the source.
     */
    private String url;


    /**
     * A number representing the state of the connection. Possible values are CONNECTING (0), OPEN (1), or CLOSED (2).
     */
    private int readyState;

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
    private static final String EVENT_NAME_MESSAGE = "message";

    /**
     * 如果客户端想要终止重新连接，则需要调用此方法。
     * 如果服务器想要终止重新连接，则需要返回 http status 204
     *
     * @throws IOException
     */

    private MultiValueMap<String, SseEventListener> eventListeners = new CommonMultiValueMap<>();
    private EventPublisher eventPublisher;

    private String eventDomain;

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
        this.eventDomain = eventDomain;
    }

    @Override
    protected void doStart() {
        super.doStart();
        this.eventPublisher.addEventListener(this.eventDomain, this);
    }

    @Override
    public void on(SseEvent event) {
        SseEventType eventType = event.getType();
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
                SseMessageEvent messageEvent = (SseMessageEvent) event;
                this.lastEventId = messageEvent.getLastEventId();
                messageEvent.setSource(this);
                messageEvent.setDomain(this.eventDomain);
                String name = messageEvent.getName();

                Collection<SseEventListener> listeners = eventListeners.containsKey(name) ? eventListeners.get(name) : eventListeners.get(EVENT_NAME_MESSAGE);
                for (SseEventListener listener : listeners) {
                    listener.on(event);
                }
                break;
        }
    }
}
