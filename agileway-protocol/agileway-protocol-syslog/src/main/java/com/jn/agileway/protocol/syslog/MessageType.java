package com.jn.agileway.protocol.syslog;

public enum MessageType {
    /**
     * Message type for a message that could not be parsed.
     */
    UNKNOWN,
    /**
     * Message type for a CEF message.
     */
    CEF,
    /**
     * Message type for a rfc 3164 message.
     */
    RFC3164,
    /**
     * Message type for a rfc 5424 message.
     */
    RFC5424
}
