package org.tensin.mirror4j.model;

/**
 * The Class Constants.
 */
public final class Constants {

    /** The Constant THREAD_NAME. */
    public static final String THREAD_NAME = "THREAD-MIRROR4J";

    /** The Constant DEFAULT_EXECUTION_TIMEOUT_IN_MILLISECONDS. */
    public static final long DEFAULT_EXECUTION_TIMEOUT_IN_MILLISECONDS = 120000;

    /** The Constant DEFAULT_DEVICE_PATH. */
    public static final String DEFAULT_DEVICE_PATH = "/dev/";

    /** The Constant DEFAULT_DEVICE_NAME. */
    public static final String DEFAULT_DEVICE_NAME = DEFAULT_DEVICE_PATH + "hidraw0";

    /** The Constant DEFAULT_DEVICE_PATTERN. */
    public static final String DEFAULT_DEVICE_PATTERN = "hidraw.*";

    /** The Constant DEFAULT_CONFIGURATION_FILENAME. */
    public static final String DEFAULT_CONFIGURATION_FILENAME = "mirror4j.xml";

    /**
     * Instantiates a new constants.
     */
    private Constants() {

    }

}
