package com.jn.agileway.eimessage.core;


import com.jn.langx.IdGenerator;
import com.jn.langx.util.id.UlidGenerator;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * The headers for a {@link Message}.
 *
 * <p><b>IMPORTANT</b>: This class is immutable. Any mutating operation such as
 * {@code put(..)}, {@code putAll(..)} and others will throw
 * {@link UnsupportedOperationException}.
 * <p>Subclasses do have access to the raw headers, however, via {@link #getRawHeaders()}.
 *
 * <p>One way to create message headers is to use the
 * <pre class="code">
 * MessageBuilder.withPayload("foo").setHeader("key1", "value1").setHeader("key2", "value2");
 * </pre>
 *
 * A second option is to create
 * passing a payload as {@link Object} and headers as a {@link Map java.util.Map}:
 * <pre class="code">
 * Map headers = new HashMap();
 * headers.put("key1", "value1");
 * headers.put("key2", "value2");
 * new GenericMessage("foo", headers);
 * </pre>
 *
 * A third option is to use
 * or one of its subclasses to create specific categories of headers.
 *
 */
public class MessageHeaders implements Map<String, Object>, Serializable {

    public static final UUID ID_VALUE_NONE = new UUID(0,0);

    /**
     * The key for the Message ID. This is an automatically generated UUID and
     * should never be explicitly set in the header map <b>except</b> in the
     * case of Message deserialization where the serialized Message's generated
     * UUID is being restored.
     */
    public static final String ID = "id";

    public static final String TIMESTAMP = "timestamp";

    public static final String CONTENT_TYPE = "contentType";

    public static final String REPLY_CHANNEL = "replyChannel";

    public static final String ERROR_CHANNEL = "errorChannel";


    private static final long serialVersionUID = 7035068984263400920L;

    private static final Logger logger = Loggers.getLogger(MessageHeaders.class);

    private static final IdGenerator defaultIdGenerator = new UlidGenerator();

    private static volatile IdGenerator idGenerator = null;


    private final Map<String, Object> headers;


    /**
     * Construct a {@link MessageHeaders} with the given headers. An {@link #ID} and
     * {@link #TIMESTAMP} headers will also be added, overriding any existing values.
     * @param headers a map with headers to add
     */
    public MessageHeaders(Map<String, Object> headers) {
        this(headers, null, null);
    }

    /**
     * Constructor providing control over the ID and TIMESTAMP header values.
     * @param headers a map with headers to add
     * @param id the {@link #ID} header value
     * @param timestamp the {@link #TIMESTAMP} header value
     */
    protected MessageHeaders(Map<String, Object> headers, UUID id, Long timestamp) {
        this.headers = (headers != null ? new HashMap<String, Object>(headers) : new HashMap<String, Object>());

        if (id == null) {
            this.headers.put(ID, getIdGenerator().get());
        }
        else if (id == ID_VALUE_NONE) {
            this.headers.remove(ID);
        }
        else {
            this.headers.put(ID, id);
        }

        if (timestamp == null) {
            this.headers.put(TIMESTAMP, System.currentTimeMillis());
        }
        else if (timestamp < 0) {
            this.headers.remove(TIMESTAMP);
        }
        else {
            this.headers.put(TIMESTAMP, timestamp);
        }
    }

    /**
     * Copy constructor which allows for ignoring certain entries.
     * Used for serialization without non-serializable entries.
     * @param original the MessageHeaders to copy
     * @param keysToIgnore the keys of the entries to ignore
     */
    private MessageHeaders(MessageHeaders original, Set<String> keysToIgnore) {
        this.headers = new HashMap<String, Object>(original.headers.size() - keysToIgnore.size());
        for (Map.Entry<String, Object> entry : original.headers.entrySet()) {
            if (!keysToIgnore.contains(entry.getKey())) {
                this.headers.put(entry.getKey(), entry.getValue());
            }
        }
    }


    protected Map<String, Object> getRawHeaders() {
        return this.headers;
    }

    protected static IdGenerator getIdGenerator() {
        return (idGenerator != null ? idGenerator : defaultIdGenerator);
    }


    public Long getTimestamp() {
        return get(TIMESTAMP, Long.class);
    }

    public Object getReplyChannel() {
        return get(REPLY_CHANNEL);
    }

    public Object getErrorChannel() {
        return get(ERROR_CHANNEL);
    }


    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        Object value = this.headers.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Incorrect type specified for header '" +
                    key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
        }
        return (T) value;
    }


    // Delegating Map implementation

    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return Collections.unmodifiableMap(this.headers).entrySet();
    }

    public Object get(Object key) {
        return this.headers.get(key);
    }

    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }

    public int size() {
        return this.headers.size();
    }

    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.headers.values());
    }


    // Unsupported Map operations

    /**
     * Since MessageHeaders are immutable, the call to this method
     * will result in {@link UnsupportedOperationException}.
     */
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    /**
     * Since MessageHeaders are immutable, the call to this method
     * will result in {@link UnsupportedOperationException}.
     */
    public void putAll(Map<? extends String, ? extends Object> map) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    /**
     * Since MessageHeaders are immutable, the call to this method
     * will result in {@link UnsupportedOperationException}.
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }

    /**
     * Since MessageHeaders are immutable, the call to this method
     * will result in {@link UnsupportedOperationException}.
     */
    public void clear() {
        throw new UnsupportedOperationException("MessageHeaders is immutable");
    }


    // Serialization methods

    private void writeObject(ObjectOutputStream out) throws IOException {
        Set<String> keysToIgnore = new HashSet<String>();
        for (Map.Entry<String, Object> entry : this.headers.entrySet()) {
            if (!(entry.getValue() instanceof Serializable)) {
                keysToIgnore.add(entry.getKey());
            }
        }

        if (keysToIgnore.isEmpty()) {
            // All entries are serializable -> serialize the regular MessageHeaders instance
            out.defaultWriteObject();
        }
        else {
            // Some non-serializable entries -> serialize a temporary MessageHeaders copy
            if (logger.isDebugEnabled()) {
                logger.debug("Ignoring non-serializable message headers: " + keysToIgnore);
            }
            out.writeObject(new MessageHeaders(this, keysToIgnore));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }


    // equals, hashCode, toString

    @Override
    public boolean equals(Object other) {
        return (this == other ||
                (other instanceof MessageHeaders && this.headers.equals(((MessageHeaders) other).headers)));
    }

    @Override
    public int hashCode() {
        return this.headers.hashCode();
    }

    @Override
    public String toString() {
        return this.headers.toString();
    }

}

