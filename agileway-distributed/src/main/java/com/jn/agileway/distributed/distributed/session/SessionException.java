package com.jn.agileway.distributed.distributed.session;

/**
 * General security exception attributed to problems during interaction with the system during
 * a session.
 *
 * @since 3.7.0
 */
public class SessionException extends RuntimeException {

    /**
     * Creates a new SessionException.
     */
    public SessionException() {
        super();
    }

    /**
     * Constructs a new SessionException.
     *
     * @param message the reason for the exception
     */
    public SessionException(String message) {
        super(message);
    }

    /**
     * Constructs a new SessionException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public SessionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new SessionException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

}
