package org.tensin.mirror4j.model;

/**
 * The Class MirrorException.
 */
public class Mirror4JException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5425682063963568465L;

    /**
     * Instantiates a new mirror exception.
     */
    public Mirror4JException() {
        super();
    }

    /**
     * Instantiates a new mirror exception.
     * 
     * @param message
     *            the message
     */
    public Mirror4JException(final String message) {
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
    public Mirror4JException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new mirror exception.
     * 
     * @param cause
     *            the cause
     */
    public Mirror4JException(final Throwable cause) {
        super(cause);
    }

}
