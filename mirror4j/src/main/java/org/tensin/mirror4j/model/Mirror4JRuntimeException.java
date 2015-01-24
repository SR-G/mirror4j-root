package org.tensin.mirror4j.model;

/**
 * The Class MirrorException.
 */
public class Mirror4JRuntimeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5425682063963568465L;

    /**
     * Instantiates a new mirror exception.
     */
    public Mirror4JRuntimeException() {
        super();
    }

    /**
     * Instantiates a new mirror exception.
     * 
     * @param message
     *            the message
     */
    public Mirror4JRuntimeException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new mirror exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public Mirror4JRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new mirror exception.
     * 
     * @param cause
     *            the cause
     */
    public Mirror4JRuntimeException(final Throwable cause) {
        super(cause);
    }

}
