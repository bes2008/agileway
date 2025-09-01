package com.jn.agileway.httpclient.core.sse;

import com.jn.langx.Builder;
import com.jn.langx.event.DomainEvent;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

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

    public static class SseErrorEvent extends SseEvent {
        private int statusCode = -1;
        private String errorMessage;

        SseErrorEvent(SseEventSource source, int statusCode, String errorMessage) {
            super(source, SseEventType.ERROR);
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getErrorMessage() {
            return errorMessage;
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
        private String lastEventId;
        /**
         * 重试时间，单位毫秒
         */
        private long retry;

        public SseMessageEvent(SseEventSource source, String name, String data, String lastEventId, long retry) {
            super(source, SseEventType.MESSAGE);
            this.name = Objs.useValueIfEmpty(name, SseEventSource.EVENT_NAME_MESSAGE);
            this.data = data;
            this.lastEventId = lastEventId;
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

        public String getLastEventId() {
            return lastEventId;
        }

        public String getOrigin() {
            return getSource().getUrl();
        }

        long retry() {
            return retry;
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
