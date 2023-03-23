package com.jn.agileway.syslog.protocol;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;

import java.net.InetAddress;
import java.util.*;

public class SyslogMessage {
/*****************************************************
 *  附加部分， 这部分不属于 syslog message 中的
 *****************************************************/
    /**
     * IP Address for the sender of the message.
     */
    private InetAddress remoteAddress;
    /**
     * Unprocessed copy of the message.
     */
    private String rawMessage;
    private MessageType type;


    /***************************************************************
     * header 部分
     ***************************************************************/

    /**
     * the PRI header
     */
    private int priority;

    /**
     * Facility of the message.
     * 从priority 中解析出
     */
    @Nullable
    private Integer facility;

    /**
     * Level for the message. Parsed from the message.
     * 从priority 中解析出
     */
    private Severity severity;
    /**
     * the VERSION header
     */
    @Nullable
    private Integer version;
    /**
     * Date of the message. This is the parsed date from the client.
     * UTC 时间戳
     * <p>
     * the TIMESTAMP header
     */
    private long timestamp;
    /**
     * the HOSTNAME header
     */
    @Nullable
    private String hostname;

    /**
     * the APP-NAME header
     */
    @Nullable
    private String appName;
    /**
     * the PROCID header
     */
    @Nullable
    private String procId;

    /**
     * the MSGID header
     */
    @Nullable
    private String msgId;


    /***************************************************************
     * MSG 部分,主要包括 tag, content
     ***************************************************************/

    /**
     * the message tag, 在 message 部分开头，可有可无
     */
    @Nullable
    private String tag;
    /**
     * content of message
     */
    @Nullable
    private String content;


    /***************************************************************
     * structured data 部分
     ***************************************************************/
    @Nullable
    private List<StructuredElement> structuredData;


    /***************************************************************
     * 接下来这部分，是CEF中的，暂时先不做处理
     ***************************************************************/
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
     * the {@code extension} attribute
     */
    @Nullable
    private Map<String, String> extension;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProcId() {
        return procId;
    }

    public void setProcId(String procId) {
        this.procId = procId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<StructuredElement> getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(List<StructuredElement> structuredData) {
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


    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * This instance is equal to all instances of {@code ImmutableSyslogMessage} that have equal attribute values.
     *
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */

    public boolean equals(Object another) {
        if (this == another) return true;
        if (!(another instanceof SyslogMessage)) {
            return false;
        }
        SyslogMessage o = (SyslogMessage) another;
        if (!Objs.equals(this.priority, o.priority)) {
            return false;
        }
        if (!Objs.equals(this.timestamp, o.timestamp)) {
            return false;
        }
        if (!Objs.equals(this.remoteAddress, o.remoteAddress)) {
            return false;
        }
        if (!Objs.equals(this.rawMessage, o.rawMessage)) {
            return false;
        }
        if (!Objs.equals(this.type, o.type)) {
            return false;
        }
        if (!Objs.equals(this.severity, o.severity)) {
            return false;
        }
        if (!Objs.equals(this.facility, o.facility)) {
            return false;
        }
        if (!Objs.equals(this.hostname, o.hostname)) {
            return false;
        }
        if (!Objs.equals(this.version, o.version)) {
            return false;
        }


        if (!Objs.equals(this.content, o.content)) {
            return false;
        }
        if (!Objs.equals(this.procId, o.procId)) {
            return false;
        }
        if (!Objs.equals(this.tag, o.tag)) {
            return false;
        }
        if (!Objs.equals(this.msgId, o.msgId)) {
            return false;
        }
        if (!Objs.equals(this.appName, o.appName)) {
            return false;
        }
        if (!Objs.equals(this.structuredData, o.structuredData)) {
            return false;
        }
        if (!Objs.equals(this.deviceVendor, o.deviceVendor)) {
            return false;
        }
        if (!Objs.equals(this.deviceProduct, o.deviceProduct)) {
            return false;
        }
        if (!Objs.equals(this.deviceVersion, o.deviceVersion)) {
            return false;
        }
        if (!Objs.equals(this.deviceEventClassId, o.deviceEventClassId)) {
            return false;
        }
        if (!Objs.equals(this.name, o.name)) {
            return false;
        }
        if (!Objs.equals(this.deviceEventClassId, o.deviceEventClassId)) {
            return false;
        }
        if (!Objs.equals(this.name, o.name)) {
            return false;
        }
        if (!Objs.equals(this.extension, o.extension)) {
            return false;
        }
        return true;
    }

    /**
     * Computes a hash code from attributes: {@code date}, {@code remoteAddress}, {@code rawMessage}, {@code type}, {@code level}, {@code version}, {@code facility}, {@code host}, {@code message}, {@code processId}, {@code tag}, {@code messageId}, {@code appName}, {@code structuredData}, {@code deviceVendor}, {@code deviceProduct}, {@code deviceVersion}, {@code deviceEventClassId}, {@code name}, {@code severity}, {@code extension}.
     *
     * @return hashCode value
     */

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objs.hash(this.timestamp);
        h += (h << 5) + Objs.hash(remoteAddress);
        h += (h << 5) + Objs.hash(rawMessage);
        h += (h << 5) + Objs.hash(type);
        h += (h << 5) + Objs.hashCode(severity);
        h += (h << 5) + Objs.hashCode(version);
        h += (h << 5) + Objs.hashCode(facility);
        h += (h << 5) + Objs.hashCode(hostname);
        h += (h << 5) + Objs.hashCode(content);
        h += (h << 5) + Objs.hashCode(procId);
        h += (h << 5) + Objs.hashCode(tag);
        h += (h << 5) + Objs.hashCode(msgId);
        h += (h << 5) + Objs.hashCode(appName);
        h += (h << 5) + Objs.hashCode(structuredData);
        h += (h << 5) + Objs.hashCode(deviceVendor);
        h += (h << 5) + Objs.hashCode(deviceProduct);
        h += (h << 5) + Objs.hashCode(deviceVersion);
        h += (h << 5) + Objs.hashCode(deviceEventClassId);
        h += (h << 5) + Objs.hashCode(name);
        h += (h << 5) + Objs.hashCode(extension);
        return h;
    }

    /**
     * Prints the immutable value {@code SyslogMessage} with attribute values.
     *
     * @return A string representation of the value
     */
    @Override
    public String toString() {
        if (type == MessageType.RFC3164) {
            return StringTemplates.formatWithPlaceholder("PRI: {}  --  FACILITY: {}, SEVERITY: {}\nHEADER: \n\tTIMESTAMP: {}\n\tHOSTNAME: {}\nMSG:\n\tTAG: {}\n\tPROC-ID: {}\n\tCONTENT: {}\n\nATTACHMENTS\n\tTYPE: {}\n\tTYPE:RAW-MESSAGE: {}", this.priority, this.facility, this.severity, this.timestamp, this.hostname, this.tag, this.procId, this.content, this.type, this.rawMessage);
        } else if (type == MessageType.RFC5424) {
            return StringTemplates.formatWithPlaceholder("HEADER: \n\tPRI: {}  --  FACILITY: {}, SEVERITY: {}\n\tVERSION: {}\n\tTIMESTAMP: {}\n\tHOSTNAME: {}\n\tAPP-NAME: {}\n\tPROCID: {}\n\tMSGID: {}\nMSG:\n\tTAG: {}\n\tCONTENT: {}\n\nATTACHMENTS\n\tTYPE: {}\n\tTYPE:RAW-MESSAGE: {}", this.priority, this.facility, this.severity, this.version, this.timestamp, this.hostname, this.appName, this.procId, this.msgId, this.tag, this.content, this.type, this.rawMessage);
        }
        return defaultToString();
    }

    private String defaultToString() {
        return "SyslogMessage{" +
                "remoteAddress=" + remoteAddress +
                ", rawMessage='" + rawMessage + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", priority=" + priority +
                ", facility=" + facility +
                ", severity=" + severity +
                ", hostname='" + hostname + '\'' +
                ", version=" + version +
                ", msgId='" + msgId + '\'' +
                ", appName='" + appName + '\'' +
                ", procId='" + procId + '\'' +
                ", tag='" + tag + '\'' +
                ", content='" + content + '\'' +
                ", structuredData=" + structuredData +
                ", deviceVendor='" + deviceVendor + '\'' +
                ", deviceProduct='" + deviceProduct + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", deviceEventClassId='" + deviceEventClassId + '\'' +
                ", name='" + name + '\'' +
                ", extension=" + extension +
                '}';
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
