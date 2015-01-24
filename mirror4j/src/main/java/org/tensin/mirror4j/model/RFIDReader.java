package org.tensin.mirror4j.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.mirror4j.model.events.EventDevice;
import org.tensin.mirror4j.model.events.EventZtamp;
import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Class Mirror4J.
 *
 *
 * unsigned long long tag;
 * fread(tag);
 *
 *
 * 5026388832396955656:RABBIT-SILVER
 * 2288200258955694088:RABBIT-RED
 * 2864942486235828232:ZTAMP-LIGHTGREEN
 * 3370471544408166408:ZTAMP-BLUE
 * 5388084177470148616:ZTAMP-SILVER
 * 5837882289765404680:ZTAMP-GREEN
 * 293951148472324104:ZTAMP-YELLOW
 * 4473010127695433736:ZTAMP-RED
 *
 *
 */
public class RFIDReader extends Thread {

    /**
     * Dump bytes array.
     *
     * @param bytes
     *            the bytes
     * @return the string
     */
    private static String dumpBytesArray(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (final byte theByte : bytes) {
            sb.append(Integer.toHexString(theByte));
        }
        return sb.toString();
    }

    /**
     * Expurge pattern.
     * Used on ARM.
     * On ARM, we recevied something like :
     * 100255200010025520001002550000100255000010025580001002552080001002552000100255260001002553000100255820001002551930001002553100010025539000100255000010025500001002550000
     * instead of this line (on x86) :
     * 220082082263821933139000
     *
     * So we remove the 1 0 0 -1 pattern and just keep the needed octets
     * Otherwise if pattern not found, we just copy the array byte per byte
     *
     * @param bytes
     *            the bytes
     */
    private static void expurgeArmPattern(final byte[] bytes) {
        int cursor = 0;
        int dest = 0;
        final byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = 0;
        }
        while (cursor < bytes.length) {
            if ((bytes[cursor] == 1) && (bytes[cursor + 1] == 0) && (bytes[cursor + 2] == 0) && (bytes[cursor + 3] == -1)) {
                result[dest++] = bytes[cursor + 4];
                cursor = cursor + 8;
            } else {
                result[dest++] = bytes[cursor++];
            }
        }
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = result[i];
        }
    }

    /**
     * Gets the as bits.
     *
     * @param b
     *            the b
     * @return the as bits
     */
    private static String getAsBits(final int b) {
        String asBits = "";
        for (int bit = 7; bit >= 0; bit--) {
            asBits += ((b & (1 << bit)) > 0 ? "1" : "0");
        }

        return asBits;
    }

    /**
     * Gets the byte as unsigned.
     *
     * @param b
     *            the b
     * @return the byte as unsigned
     */
    private static int getByteAsUnsigned(final byte b) {
        int valAsUnisgnedByte = b;
        if (b < 0) {
            valAsUnisgnedByte = 256 + b;
        }
        return valAsUnisgnedByte;
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(final String[] args) {
        final byte[] b = new byte[] { 1, 0, 0, -1, 8, 0, 0, 0, 1, 0, 0, -1, 1, 0, 0, 0 };
        expurgeArmPattern(b);
    }

    /**
     * Builds the tag.
     *
     * @param bytes
     *            the bytes
     * @return the string
     */
    private static String str(final byte[] bytes) {
        final StringBuilder result = new StringBuilder();
        for (final byte b : bytes) {
            if ((b & 128) > 0) {
                result.append(getByteAsUnsigned(b));
            } else {
                result.append(b);
            }
        }
        // Removes trailing zeros
        return result.toString().replaceAll("0+$", "");
    }

    /** The Constant DEVICE_RECONNECTION_TIMEOUT. */
    public static final int DEFAULT_DEVICE_RECONNECTION_TIMEOUT = 2000;

    /** The reconnection timeout. */
    private int reconnectionTimeout = DEFAULT_DEVICE_RECONNECTION_TIMEOUT;

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The Constant BUFFER_SIZE. */
    private static final int BUFFER_SIZE = 128;

    /** The device. */
    private String deviceName;

    /** The active. */
    private boolean active;

    /** The show error messages on init. */
    private boolean showErrorMessagesOnInit;

    /**
     * Instantiates a new mirror4 j.
     */
    public RFIDReader() {
        super();
    }

    /**
     * Instantiates a new mirror4 j.
     *
     * @param deviceName
     *            the device name
     */
    public RFIDReader(final String deviceName) {
        super();
        this.deviceName = deviceName;
    }

    /**
     * Builds the tag.
     *
     * @param bytes
     *            the bytes
     * @return the string
     */
    private String buildTag(final byte[] bytes) {
        String result = "";
        for (int i = 4; i < bytes.length; i++) {
            if ((i > 8) && (bytes[i] == 0)) {
                break;
            }
            result += bytes[i] & 0x80; // 0000007f;
        }
        return result;
    }

    /**
     * Detect device.
     *
     * @return the string
     * @throws Mirror4JException
     *             the mirror exception
     */
    public String detectDevice() throws Mirror4JException {
        LOGGER.info("Trying to autodect mirror4j device in [" + Constants.DEFAULT_DEVICE_PATH + "] with pattern [" + Constants.DEFAULT_DEVICE_PATTERN + "]");
        final IOFileFilter nameFilter = new RegexFileFilter(Constants.DEFAULT_DEVICE_PATTERN);
        final Collection<File> items = FileUtils.listFiles(new File(Constants.DEFAULT_DEVICE_PATH), nameFilter, TrueFileFilter.TRUE);

        if (items.size() > 0) {
            for (final File f : items) {
                final String name = f.getAbsolutePath();
                LOGGER.info("Auto-detected mirror device [" + name + "]");
                return name;
                // if (isMirror4J(name)) {
                // LOGGER.info("Device [" + name + "] is detected as a mir:ror, now connecting");
                // return name;
                // } else {
                // LOGGER.debug("Device [" + name + "] is not a mir:ror, won't connect to it");
                // }
            }
        }
        LOGGER.info("No auto-detect mirror device, will use default name [" + Constants.DEFAULT_DEVICE_NAME + "]");
        return Constants.DEFAULT_DEVICE_NAME;
    }

    /**
     * Extract ztamp id.
     *
     * @param s
     *            the s
     * @return the string
     */
    private String extractZtampId(final String s) {
        return s.substring(16, s.length());
    }

    /**
     * Gets the device name.
     *
     * @return the device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Gets the reconnection timeout.
     *
     * @return the reconnection timeout
     */
    public int getReconnectionTimeout() {
        return reconnectionTimeout;
    }

    /**
     * Halt.
     */
    public void halt() {
        active = false;
    }

    /**
     * Inits the device.
     *
     * @param currentDeviceName
     *            the current device name
     * @return the file input stream
     */
    public FileInputStream initFileInputStreamDevice(final String currentDeviceName) {
        final File device = new File(currentDeviceName);
        FileInputStream fis = null;
        if (!device.exists()) {
            if (showErrorMessagesOnInit) {
                LOGGER.error("Specified device not found [" + currentDeviceName + "]");
                showErrorMessagesOnInit = false;
            }
        } else if (!device.canRead()) {
            if (showErrorMessagesOnInit) {
                LOGGER.error("Specified device can't be read [" + currentDeviceName + "], please check permissions (e.g., 'sudo chmod a+r " + currentDeviceName
                        + "')");
                showErrorMessagesOnInit = false;
            }
        } else {
            try {
                fis = new FileInputStream(device);
                LOGGER.info("Now connected on specified device [" + currentDeviceName + "]");
            } catch (final IOException e) {
                LOGGER.error("Error while opening device [" + currentDeviceName + "]", e);
                showErrorMessagesOnInit = true;
                fis = null;
            }
        }
        return fis;
    }

    /**
     * Checks if is arm trame.
     *
     * @param bytes
     *            the bytes
     * @return true, if is arm trame
     */
    private boolean isArmTrame(final byte[] bytes) {
        return (1 == bytes[0]) && (0 == bytes[1]) && (0 == bytes[2]) && (-1 == bytes[3]);
    }

    /**
     * Checks if is empty trame.
     *
     * @param bytes
     *            the bytes
     * @return true, if is empty trame
     */
    private boolean isEmptyTrame(final byte[] bytes) {
        return (((0 == bytes[0]) && (0 == bytes[1]) && (0 == bytes[2]) && (0 == bytes[3]) && (0 == bytes[4]) && (0 == bytes[5]) && (0 == bytes[6]) && (0 == bytes[7])) || ((1 == bytes[0])
                && (0 == bytes[1]) && (0 == bytes[2]) && (-1 == bytes[3]) && (0 == bytes[4]) && (0 == bytes[5]) && (0 == bytes[6]) && (0 == bytes[7])));
    }

    /**
     * Read.
     *
     * @param fis
     *            the fis
     */
    private void read(final FileInputStream fis) {
        // final BufferedInputStream bis = new BufferedInputStream(fis);
        try {
            final byte[] bytes = new byte[BUFFER_SIZE];
            int read;
            while ((read = fis.read(bytes, 0, BUFFER_SIZE)) > -1) {
                if (!isEmptyTrame(bytes)) {
                    if (isArmTrame(bytes)) {
                        expurgeArmPattern(bytes);
                    }

                    if (!org.tensin.mirror4j.model.State.VIDE.match(bytes)) {
                        final String s = str(bytes);
                        LOGGER.info("Received mirror trame [" + s + "]");

                        IEvent event = null;
                        if (org.tensin.mirror4j.model.State.POSE.match(bytes)) {
                            event = new EventZtamp(org.tensin.mirror4j.model.State.POSE, extractZtampId(s));
                        } else if (org.tensin.mirror4j.model.State.RETIRE.match(bytes)) {
                            event = new EventZtamp(org.tensin.mirror4j.model.State.RETIRE, extractZtampId(s));
                        } else if (org.tensin.mirror4j.model.State.ENDROIT.match(bytes)) {
                            event = new EventDevice(org.tensin.mirror4j.model.State.ENDROIT);
                        } else if (org.tensin.mirror4j.model.State.ENVERS.match(bytes)) {
                            event = new EventDevice(org.tensin.mirror4j.model.State.ENVERS);
                        }
                        if (event != null) {
                            Processor.getInstance().notify(event);
                        }
                    }

                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = 0;
                    }
                }
            }
        } catch (final IOException e) {
            LOGGER.error("I/O Error while reading device [" + deviceName + "]", e);
        } catch (final Exception e) {
            LOGGER.error("Error while reading device [" + deviceName + "]", e);
        } finally {
            // IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(fis);
            showErrorMessagesOnInit = true;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        super.run();
        FileInputStream fis = null;
        showErrorMessagesOnInit = true;
        while (active) {
            if (fis != null) {
                read(fis);
                fis = null; // important (the read() method is a continuous loop, will only exit if/when there is a problem (device unplugged > I/O exception
                // internally catched into read())
            } else {
                fis = initFileInputStreamDevice(deviceName);
                if (fis == null) {
                    try {
                        Thread.sleep(getReconnectionTimeout());
                    } catch (final InterruptedException e) {
                    }
                }
            }
        }
    }

    /**
     * Sets the device name.
     *
     * @param deviceName
     *            the new device name
     */
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Sets the reconnection timeout.
     *
     * @param reconnectionTimeout
     *            the new reconnection timeout
     */
    public void setReconnectionTimeout(final int reconnectionTimeout) {
        this.reconnectionTimeout = reconnectionTimeout;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start() {
        setName(Constants.THREAD_NAME + "-MONITORING");

        final ByteOrder byteOrder = java.nio.ByteOrder.nativeOrder();
        LOGGER.info("Detected Byte Order [" + byteOrder.toString() + "]");

        LOGGER.info("Starting Mirror monitoring thread on device [" + deviceName + "]");
        active = true;
        super.start();
    }
}
