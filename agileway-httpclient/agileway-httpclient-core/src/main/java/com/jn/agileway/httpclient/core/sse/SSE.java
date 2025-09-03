package com.jn.agileway.httpclient.core.sse;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.Builder;
import com.jn.langx.event.DomainEvent;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public class SSE {

    public static enum SseEventType {
        /**
         * Fired when a connection to an event source is opened.
         */
        OPEN,
        /**
         * Fired when a connection to an event source failed to open.
         */
        ERROR,
        /**
         * Fired when a message is received from the server.
         */
        MESSAGE
    }


    public static class SseEvent extends DomainEvent<SseEventSource> {
        private SseEventType type;

        public SseEvent(SseEventSource source, SseEventType type) {
            super(source.getEventDomain(), source);
            this.type = Objs.useValueIfEmpty(type, SseEventType.MESSAGE);
        }

        public SseEventType getType() {
            return type;
        }

        public static SseEvent ofOpen(SseEventSource source) {
            return new SseEvent(source, SseEventType.OPEN);
        }

    }

    /**
     * 假定顺利获取连接，拿到数据，并且一直不退出是一个正常情况，其它情况都设为一个错误
     */
    public static enum SseErrorType implements CommonEnum {
        /**
         * <pre>
         * 触发时机：发起请求时，如果拿不到 Http Response就出错了，则会触发这个错误
         * 连接状态：未连接
         * 默认重连：是
         * </pre>
         */
        CONNECT_FAILED(0, "CONNECT_FAILED", "Connect failed"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status >= 400 ,且 < 500时，则会触发这个错误
         * 连接状态：已关闭
         * 默认重连：是
         * </pre>
         */
        REQUEST_ERROR(1, "REQUEST_ERROR", "Request error"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status >= 500
         * 连接状态：已关闭
         * 默认重连：是
         * </pre>
         */
        SERVER_ERROR(2, "SERVER_ERROR", "Server error"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status ==200, 读取数据过程正常退出时，则会触发这个错误
         * 连接状态：已关闭
         * 默认重连：是
         * </pre>
         */
        CONNECTION_CLOSED_BY_PEER(3, "CONNECTION_CLOSED_BY_PEER", "Connection closed by peer"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status ==200, 读取数据过程异常时，则会触发这个错误
         * 连接状态：正常
         * 默认重连：否
         * </pre>
         */
        CLIENT_READ_ERROR(4, "CLIENT_READ_ERROR", "Client read data error"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status ==200, 读取数据过程发现服务端数据有问题时，则会触发这个错误
         * 连接状态：正常
         * 默认重连：是
         * </pre>
         */
        DATA_MALFORMED_ERROR(5, "DATA_MALFORMED_ERROR", "data malformed error"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status ==204, 读取数据过程异常退出时，则会触发这个错误
         * 连接状态：已关闭
         * 默认重连：否
         * </pre>
         */
        NO_CONTENT(6, "NO_CONTENT", "No content"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status ==200, 读取数据过程异常退出时，则会触发这个错误
         * 连接状态：已关闭
         * 默认重连：否
         * </pre>
         */
        UNSUPPORTED_CONTENT_TYPE(7, "UNSUPPORTED_CONTENT_TYPE", "Unsupported content type"),
        /**
         * <pre>
         * 触发时机：获取到的 Http Response 的 status ==200, 读取数据过程异常退出时，则会触发这个错误
         * 连接状态：正常
         * 默认重连：是
         * </pre>
         */
        UNKNOWN_ERROR(8, "UNKNOWN_ERROR", "Unknown error");


        private EnumDelegate delegate;

        SseErrorType(int errorCode, String name, String description) {
            this.delegate = new EnumDelegate(errorCode, name, description);
        }

        public int getCode() {
            return delegate.getCode();
        }

        public String getDisplayText() {
            return delegate.getDisplayText();
        }

        public String getName() {
            return delegate.getName();
        }
    }

    public static class SseErrorEvent extends SseEvent {
        private SseErrorType type;
        private int statusCode = -1;
        private String errorMessage;
        private Throwable cause;

        SseErrorEvent(SseEventSource source, SseErrorType type, int statusCode, String errorMessage) {
            super(source, SseEventType.ERROR);
            this.type = Objs.useValueIfEmpty(type, SseErrorType.UNKNOWN_ERROR);
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
        }

        SseErrorEvent(SseEventSource source, SseErrorType type, HttpResponse response, Throwable ex) {
            super(source, SseEventType.ERROR);
            this.type = Objs.useValueIfEmpty(type, SseErrorType.UNKNOWN_ERROR);
            if (response != null) {
                this.statusCode = response.getStatusCode();
                this.errorMessage = response.getErrorMessage();
            }
            if (ex != null) {
                this.errorMessage = ex.getMessage();
                this.cause = ex;
            }
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Throwable getCause() {
            return cause;
        }
    }


    /**
     * SSE Message Event，要通常 Listener 交给用户使用的 Event
     */
    public static class SseMessageEvent extends SseEvent {
        /**
         * event 名，默认为 message
         */
        private String name;
        private String data;
        private String id;
        /**
         * 重试时间，单位毫秒
         */
        private long retry;

        public SseMessageEvent(SseEventSource source, String name, String data, String id, long retry) {
            super(source, SseEventType.MESSAGE);
            this.name = Objs.useValueIfEmpty(name, SseEventSource.EVENT_NAME_MESSAGE);
            this.data = data;
            this.id = id;
            this.retry = retry;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getData() {
            return data;
        }

        public String getId() {
            return id;
        }

        public String getOrigin() {
            return getSource().getUrl();
        }

        long retry() {
            return retry;
        }

        public String toString() {
            return StringTemplates.formatWithPlaceholder("origin: {}, name: {}, data: {}, id: {}, retry: {}", getOrigin(), name, data, id, retry);
        }
    }


    public static class SseMessageEventBuilder implements Builder<SseMessageEvent> {

        private SseEventSource source;
        private String name;
        private StringBuilder data;
        private String lastEventId;
        private long retry = -1L;

        private SseMessageEventBuilder(SseEventSource source) {
            this.source = source;
            this.data = new StringBuilder();
        }

        public static SseMessageEventBuilder newBuilder(SseEventSource source) {
            return new SseMessageEventBuilder(source);
        }

        public SseMessageEventBuilder withEventName(String name) {
            this.name = name;
            return this;
        }

        public SseMessageEventBuilder appendData(String dataFragment) {
            this.data.append(dataFragment);
            return this;
        }

        public SseMessageEventBuilder withLastEventId(String lastEventId) {
            this.lastEventId = lastEventId;
            return this;
        }

        public SseMessageEventBuilder withRetry(long retryMills) {
            this.retry = retryMills;
            return this;
        }

        @Override
        public SseMessageEvent build() {
            if (Strings.isBlank(this.data) && retry <= 0) {
                return null;
            }
            return new SseMessageEvent(this.source, this.name, this.data.toString(), this.lastEventId, this.retry);
        }
    }

}
