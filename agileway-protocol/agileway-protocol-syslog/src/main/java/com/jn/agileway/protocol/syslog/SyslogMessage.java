package com.jn.agileway.protocol.syslog;

import com.jn.langx.annotation.Nullable;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;

public class SyslogMessage {
    /**
     * Date of the message. This is the parsed date from the client.
     */
    private LocalDateTime date;
    /**
     * IP Address for the sender of the message.
     */

    private InetAddress remoteAddress;
    /**
     * Unprocessed copy of the message.
     */
    private String rawMessage;
    private MessageType type;
    /**
    Level for the message. Parsed from the message.
     */
    @Nullable
    private Integer level;
    /**
     * Version of the message.
     *
     */
    @Nullable
    private Integer version;
    /**
     * Facility of the message.
     *
     */
    @Nullable
    private Integer facility;
    /**
     * Host of the message. This is the value from the message.
     */
    @Nullable
    private String host;
    /**
     * Message part of the overall syslog message.
     */
    @Nullable
    private String message;
    /**
     * the {@code processId} attribute
     */
    @Nullable
    private String processId;

    /**
     * the {@code tag} attribute
     */
    @Nullable
    private String tag;
    /**
     * the {@code messageId} attribute
     */
    @Nullable
    private String messageId;
    /**
     * the {@code appName} attribute
     */
    @Nullable
    private String appName;
    /**
     * the {@code structuredData} attribute
     */
    @Nullable
    private List<StructuredData> structuredData;
    /**
     * the {@code deviceVendor} attribute
     */
    @Nullable
    private String deviceVendor;
    /**
     * the {@code deviceProduct} attribute
     */
    @Nullable
    private String deviceProduct;
    /**
     * the {@code deviceVersion} attribute
     */
    @Nullable
    private String deviceVersion;
    /**
     * the {@code deviceEventClassId} attribute
     */
    @Nullable
    private String deviceEventClassId;
    /**
     * the {@code name} attribute
     */
    @Nullable
    private String name;
    /**
     * the {@code severity} attribute
     */
    @Nullable
    private String severity;

    /**
     *  the {@code extension} attribute
     */
    @Nullable
    private Map<String, String> extension;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }


    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getFacility() {
        return facility;
    }

    public void setFacility(Integer facility) {
        this.facility = facility;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<StructuredData> getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(List<StructuredData> structuredData) {
        this.structuredData = structuredData;
    }

    public String getDeviceVendor() {
        return deviceVendor;
    }

    public void setDeviceVendor(String deviceVendor) {
        this.deviceVendor = deviceVendor;
    }

    public String getDeviceProduct() {
        return deviceProduct;
    }

    public void setDeviceProduct(String deviceProduct) {
        this.deviceProduct = deviceProduct;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getDeviceEventClassId() {
        return deviceEventClassId;
    }

    public void setDeviceEventClassId(String deviceEventClassId) {
        this.deviceEventClassId = deviceEventClassId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }

    /**
     * This instance is equal to all instances of {@code ImmutableSyslogMessage} that have equal attribute values.
     *
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */

    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof SyslogMessage
                && equalTo((SyslogMessage) another);
    }

    private boolean equalTo(SyslogMessage another) {
        return date.equals(another.date)
                && remoteAddress.equals(another.remoteAddress)
                && rawMessage.equals(another.rawMessage)
                && type.equals(another.type)
                && Objects.equals(level, another.level)
                && Objects.equals(version, another.version)
                && Objects.equals(facility, another.facility)
                && Objects.equals(host, another.host)
                && Objects.equals(message, another.message)
                && Objects.equals(processId, another.processId)
                && Objects.equals(tag, another.tag)
                && Objects.equals(messageId, another.messageId)
                && Objects.equals(appName, another.appName)
                && Objects.equals(structuredData, another.structuredData)
                && Objects.equals(deviceVendor, another.deviceVendor)
                && Objects.equals(deviceProduct, another.deviceProduct)
                && Objects.equals(deviceVersion, another.deviceVersion)
                && Objects.equals(deviceEventClassId, another.deviceEventClassId)
                && Objects.equals(name, another.name)
                && Objects.equals(severity, another.severity)
                && Objects.equals(extension, another.extension);
    }

    /**
     * Computes a hash code from attributes: {@code date}, {@code remoteAddress}, {@code rawMessage}, {@code type}, {@code level}, {@code version}, {@code facility}, {@code host}, {@code message}, {@code processId}, {@code tag}, {@code messageId}, {@code appName}, {@code structuredData}, {@code deviceVendor}, {@code deviceProduct}, {@code deviceVersion}, {@code deviceEventClassId}, {@code name}, {@code severity}, {@code extension}.
     *
     * @return hashCode value
     */

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + date.hashCode();
        h += (h << 5) + remoteAddress.hashCode();
        h += (h << 5) + rawMessage.hashCode();
        h += (h << 5) + type.hashCode();
        h += (h << 5) + Objects.hashCode(level);
        h += (h << 5) + Objects.hashCode(version);
        h += (h << 5) + Objects.hashCode(facility);
        h += (h << 5) + Objects.hashCode(host);
        h += (h << 5) + Objects.hashCode(message);
        h += (h << 5) + Objects.hashCode(processId);
        h += (h << 5) + Objects.hashCode(tag);
        h += (h << 5) + Objects.hashCode(messageId);
        h += (h << 5) + Objects.hashCode(appName);
        h += (h << 5) + Objects.hashCode(structuredData);
        h += (h << 5) + Objects.hashCode(deviceVendor);
        h += (h << 5) + Objects.hashCode(deviceProduct);
        h += (h << 5) + Objects.hashCode(deviceVersion);
        h += (h << 5) + Objects.hashCode(deviceEventClassId);
        h += (h << 5) + Objects.hashCode(name);
        h += (h << 5) + Objects.hashCode(severity);
        h += (h << 5) + Objects.hashCode(extension);
        return h;
    }

    /**
     * Prints the immutable value {@code SyslogMessage} with attribute values.
     *
     * @return A string representation of the value
     */

    public String toString() {
        return "SyslogMessage{"
                + "date=" + date
                + ", remoteAddress=" + remoteAddress
                + ", rawMessage=" + rawMessage
                + ", type=" + type
                + ", level=" + level
                + ", version=" + version
                + ", facility=" + facility
                + ", host=" + host
                + ", message=" + message
                + ", processId=" + processId
                + ", tag=" + tag
                + ", messageId=" + messageId
                + ", appName=" + appName
                + ", structuredData=" + structuredData
                + ", deviceVendor=" + deviceVendor
                + ", deviceProduct=" + deviceProduct
                + ", deviceVersion=" + deviceVersion
                + ", deviceEventClassId=" + deviceEventClassId
                + ", name=" + name
                + ", severity=" + severity
                + ", extension=" + extension
                + "}";
    }



    private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
        ArrayList<T> list;
        if (iterable instanceof Collection<?>) {
            int size = ((Collection<?>) iterable).size();
            if (size == 0) return Collections.emptyList();
            list = new ArrayList<T>();
        } else {
            list = new ArrayList<T>();
        }
        for (T element : iterable) {
            if (skipNulls && element == null) continue;
            if (checkNulls) Objects.requireNonNull(element, "element");
            list.add(element);
        }
        return list;
    }

    private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
        switch (list.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(list.get(0));
            default:
                if (clone) {
                    return Collections.unmodifiableList(new ArrayList<T>(list));
                } else {
                    if (list instanceof ArrayList<?>) {
                        ((ArrayList<?>) list).trimToSize();
                    }
                    return Collections.unmodifiableList(list);
                }
        }
    }

    private static <K, V> Map<K, V> createUnmodifiableMap(boolean checkNulls, boolean skipNulls, Map<? extends K, ? extends V> map) {
        switch (map.size()) {
            case 0:
                return Collections.emptyMap();
            case 1: {
                Map.Entry<? extends K, ? extends V> e = map.entrySet().iterator().next();
                K k = e.getKey();
                V v = e.getValue();
                if (checkNulls) {
                    Objects.requireNonNull(k, "key");
                    Objects.requireNonNull(v, "value");
                }
                if (skipNulls && (k == null || v == null)) {
                    return Collections.emptyMap();
                }
                return Collections.singletonMap(k, v);
            }
            default: {
                Map<K, V> linkedMap = new LinkedHashMap<K, V>(map.size());
                if (skipNulls || checkNulls) {
                    for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
                        K k = e.getKey();
                        V v = e.getValue();
                        if (skipNulls) {
                            if (k == null || v == null) continue;
                        } else if (checkNulls) {
                            Objects.requireNonNull(k, "key");
                            Objects.requireNonNull(v, "value");
                        }
                        linkedMap.put(k, v);
                    }
                } else {
                    linkedMap.putAll(map);
                }
                return Collections.unmodifiableMap(linkedMap);
            }
        }
    }
}
