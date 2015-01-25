package org.tensin.common.tools.documentation.updater;

import org.tensin.mirror4j.Mirror4JException;

/**
 * The Class SimpleXMLDocumentationException.
 */
public class SimpleXMLDocumentationException extends Mirror4JException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new simple xml documentation exception.
     * 
     * @param e
     *            the e
     */
    public SimpleXMLDocumentationException(final Exception e) {
        super(e);
    }

    /**
     * Instantiates a new simple xml documentation exception.
     * 
     * @param msg
     *            the msg
     */
    public SimpleXMLDocumentationException(final String msg) {
        super(msg);
    }

    /**
     * Instantiates a new simple xml documentation exception.
     * 
     * @param msg
     *            the msg
     * @param e
     *            the e
     */
    public SimpleXMLDocumentationException(final String msg, final Exception e) {
        super(msg, e);
    }

    /**
     * Instantiates a new simple xml documentation exception.
     * 
     * @param msg
     *            the msg
     * @param t
     *            the t
     */
    public SimpleXMLDocumentationException(final String msg, final Throwable t) {
        super(msg, t);
    }

    /**
     * Instantiates a new simple xml documentation exception.
     * 
     * @param t
     *            the t
     */
    public SimpleXMLDocumentationException(final Throwable t) {
        super(t);
    }

}
