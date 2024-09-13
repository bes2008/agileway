package com.jn.agileway.eipchannel.core.message;


import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * The headers for a {@link Message}.<br>
 * IMPORTANT: MessageHeaders are immutable. Any mutating operation (e.g., put(..), putAll(..) etc.)
 * will result in {@link UnsupportedOperationException}
 * To create MessageHeaders instance use fluent MessageBuilder API
 * <pre>
 * MessageBuilder.withPayload("foo").setHeader("key1", "value1").setHeader("key2", "value2");
 * </pre>
 * or create an instance of GenericMessage passing payload as {@link Object} and headers as a regular {@link Map}
 * <pre>
 * Map headers = new HashMap();
 * headers.put("key1", "value1");
 * headers.put("key2", "value2");
 * new GenericMessage("foo", headers);
 * </pre>
 */
public final class MessageHeaders extends LinkedHashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Loggers.getLogger(MessageHeaders.class);

    /**
     * The key for the Message ID. This is an automatically generated UUID and
     * should never be explicitly set in the header map <b>except</b> in the
     * case of Message deserialization where the serialized Message's generated
     * UUID is being restored.
     */
    public static final String ID = "id";

    public static final String TIMESTAMP = "timestamp";

    public static final String CORRELATION_ID = "correlationId";

    public static final String REPLY_CHANNEL = "replyChannel";

    public static final String ERROR_CHANNEL = "errorChannel";

    public static final String EXPIRATION_DATE = "expirationDate";

    public static final String PRIORITY = "priority";

    public static final String SEQUENCE_NUMBER = "sequenceNumber";

    public static final String SEQUENCE_SIZE = "sequenceSize";

    public static final String SEQUENCE_DETAILS = "sequenceDetails";



    public MessageHeaders(Map<String, Object> headers) {
        super(Collects.newHashMap(headers));
        this.put(ID, UUID.randomUUID());
        this.put(TIMESTAMP,System.currentTimeMillis());
    }


    public UUID getId() {
        return this.get(ID, UUID.class);
    }

    public Long getTimestamp() {
        return this.get(TIMESTAMP, Long.class);
    }

    public Long getExpirationDate() {
        return this.get(EXPIRATION_DATE, Long.class);
    }

    public Object getCorrelationId() {
        return this.get(CORRELATION_ID);
    }

    public Object getReplyChannel() {
        return this.get(REPLY_CHANNEL);
    }

    public Object getErrorChannel() {
        return this.get(ERROR_CHANNEL);
    }

    public Integer getSequenceNumber() {
        Integer sequenceNumber = this.get(SEQUENCE_NUMBER, Integer.class);
        return (sequenceNumber != null ? sequenceNumber : 0);
    }

    public Integer getSequenceSize() {
        Integer sequenceSize = this.get(SEQUENCE_SIZE, Integer.class);
        return (sequenceSize != null ? sequenceSize : 0);
    }

    public Integer getPriority() {
        return this.get(PRIORITY, Integer.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        Object value = this.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Incorrect type specified for header '" + key + "'. Expected [" + type
                    + "] but actual type is [" + value.getClass() + "]");
        }
        return (T) value;
    }

    /*
     * Serialization methods
     */

    private void writeObject(ObjectOutputStream out) throws IOException {
        List<String> keysToRemove = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            if (!(entry.getValue() instanceof Serializable)) {
                keysToRemove.add(entry.getKey());
            }
        }
        for (String key : keysToRemove) {
            if (logger.isInfoEnabled()) {
                logger.info("removing non-serializable header: {}", key);
            }
            this.remove(key);
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

}
