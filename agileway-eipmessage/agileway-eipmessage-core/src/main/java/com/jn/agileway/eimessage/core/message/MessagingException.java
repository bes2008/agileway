package com.jn.agileway.eimessage.core.message;

/**
 * The base exception for any failures related to messaging.
 *
 */
public class MessagingException extends RuntimeException {

    private volatile Message<?> failed;

    public MessagingException(Message<?> message) {
        super();
        this.failed = message;
    }

    public MessagingException(String description) {
        super(description);
        this.failed = null;
    }

    public MessagingException(String description, Throwable cause) {
        super(description, cause);
        this.failed = null;
    }

    public MessagingException(Message<?> message, String description) {
        super(description);
        this.failed = message;
    }

    public MessagingException(Message<?> message, Throwable cause) {
        super(cause);
        this.failed = message;
    }

    public MessagingException(Message<?> message, String description, Throwable cause) {
        super(description, cause);
        this.failed = message;
    }

    public Message<?> getFailed() {
        return this.failed;
    }

    public void setFailed(Message<?> message) {
        this.failed = message;
    }

}
