package org.tensin.mirror4j;

import java.io.File;
import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.mirror4j.model.Constants;
import org.tensin.mirror4j.model.Processor;
import org.tensin.mirror4j.model.RFIDReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * The Class Start.
 */
public class Starter {

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws Exception
     *             the exception
     */
    public static void main(final String[] args) throws Exception {
        final Starter starter = new Starter();
        starter.parseArguments(args);
        starter.initPid();
        starter.start();
    }

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String DEFAULT_PID_FILENAME = "mirror4j.pid";

    /** The debug. */
    @Parameter(names = "--debug", description = "Debug mode", required = false)
    private boolean debug;

    /** The usage. */
    @Parameter(names = { "-h", "--usage", "--help" }, description = "Shows available commands", required = false)
    private boolean usage;

    /** The configuration file name. */
    @Parameter(names = { "--configuration", "-c" }, description = "Configuration file name to load. If not provided, a file named \"mirror4j.xml\" on the current path will be used", required = false)
    private String configurationFileName = Constants.DEFAULT_CONFIGURATION_FILENAME;

    /** The device name. */
    @Parameter(names = { "--device", "-d" }, description = "Device name to use, e.g., /dev/hidraw0. If not provided, the first /dev/hidraw will be used", required = false)
    private String deviceName;

    /** The pid file name. */
    @Parameter(names = { "--pid" }, description = "The PID filename. Default is current directory, file mirror4j.pid")
    private String pidFileName = DEFAULT_PID_FILENAME;

    /** The m. */
    private RFIDReader m;

    /** The processor. */
    private Processor processor;

    /** The reconnection timeout. */
    @Parameter(names = { "--reconnection-timeout" }, description = "When expected device is not found (or was found previously but not anymore), we'll wait this timeout before trying to reconnect. In milliseconds. Default 2000.", required = false)
    private final int reconnectionTimeout = RFIDReader.DEFAULT_DEVICE_RECONNECTION_TIMEOUT;

    /**
     * Activate processor.
     */
    private void activateProcessor() {
        processor.setStarted(true);
    }

    /**
     * Gets the configuration file name.
     *
     * @return the configuration file name
     */
    public String getConfigurationFileName() {
        return configurationFileName;
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
     * Gets the m.
     *
     * @return the m
     */
    public RFIDReader getM() {
        return m;
    }

    /**
     * Gets the pid file name.
     *
     * @return the pid file name
     */
    public String getPidFileName() {
        return pidFileName;
    }

    /**
     * Gets the processor.
     *
     * @return the processor
     */
    public Processor getProcessor() {
        return processor;
    }

    /**
     * Halt.
     *
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void halt() throws Mirror4JException {
        m.halt();
        processor.halt();
    }

    /**
     * Inits the pid.
     *
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void initPid() throws Mirror4JException {
        if (StringUtils.isNotEmpty(pidFileName)) {
            int pid = 0;
            try {
                final RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
                final Field jvm = runtime.getClass().getDeclaredField("jvm");
                jvm.setAccessible(true);
                final sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
                final Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
                pid_method.setAccessible(true);

                pid = (Integer) pid_method.invoke(mgmt);
                LOGGER.info("Writing retrieved PID [" + pid + "] in PID file [" + pidFileName + "]");
                FileUtils.writeStringToFile(new File(pidFileName), String.valueOf(pid));
            } catch (NoSuchFieldException e) {
                throw new Mirror4JException("Can't retrieve PID", e);
            } catch (SecurityException e) {
                throw new Mirror4JException("Can't retrieve PID", e);
            } catch (IllegalAccessException e) {
                throw new Mirror4JException("Can't retrieve PID", e);
            } catch (IllegalArgumentException e) {
                throw new Mirror4JException("Can't retrieve PID", e);
            } catch (InvocationTargetException e) {
                throw new Mirror4JException("Can't retrieve PID", e);
            } catch (NoSuchMethodException e) {
                throw new Mirror4JException("Can't retrieve PID", e);
            } catch (IOException e) {
                throw new Mirror4JException("Can't store PID [" + pid + "] in file [" + pidFileName + "]", e);
            }
        } else {
            LOGGER.debug("PID file name is empty, won't store PID");
        }
    }

    /**
     * Inits the processor.
     *
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void initProcessor() throws Mirror4JException {
        processor = Processor.getInstance();
        processor.loadConfiguration(configurationFileName);
        processor.dumpConfiguration();
    }

    /**
     * Parses the arguments.
     *
     * @param args
     *            the args
     * @return the j commander
     */
    public JCommander parseArguments(final String[] args) {
        JCommander jCommander = null;
        try {
            jCommander = new JCommander(this, args);
        } catch (final ParameterException e) {
            LOGGER.error("Unrecognized options");
            jCommander = new JCommander(this);
            usage(jCommander);
        }
        if (usage) {
            usage(jCommander);
        }
        if (debug) {
            LOGGER.info("Debug activated");
        }
        return jCommander;
    }

    /**
     * Sets the configuration file name.
     *
     * @param configurationFileName
     *            the new configuration file name
     */
    public void setConfigurationFileName(final String configurationFileName) {
        this.configurationFileName = configurationFileName;
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
     * Sets the m.
     *
     * @param m
     *            the new m
     */
    public void setM(final RFIDReader m) {
        this.m = m;
    }

    /**
     * Sets the pid file name.
     *
     * @param pidFileName
     *            the new pid file name
     */
    public void setPidFileName(final String pidFileName) {
        this.pidFileName = pidFileName;
    }

    /**
     * Sets the processor.
     *
     * @param processor
     *            the new processor
     */
    public void setProcessor(final Processor processor) {
        this.processor = processor;
    }

    /**
     * Start.
     *
     * @throws Mirror4JException
     *             the mirror exception
     */
    private void start() throws Mirror4JException {
        LogInitializer.setDebug(debug);
        final long start = System.currentTimeMillis();
        LOGGER.info("Now starting Mirror4J");
        initProcessor();
        startRFIDReader();
        startProcessor();
        activateProcessor();
        LOGGER.info("Mirror4J started in [" + (System.currentTimeMillis() - start) + "ms]");
    }

    /**
     * Start processor.
     *
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void startProcessor() throws Mirror4JException {
        processor.start();
    }

    /**
     * Start rfid reader.
     *
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void startRFIDReader() throws Mirror4JException {
        m = new RFIDReader();
        if (StringUtils.isEmpty(deviceName)) {
            deviceName = m.detectDevice();
        } else {
            LOGGER.info("Using provided device name [" + deviceName + "]");
        }
        m.setDeviceName(deviceName);
        m.setReconnectionTimeout(reconnectionTimeout);
        m.start();
    }

    /**
     * Usage.
     *
     * @param jCommander
     *            the j commander
     */
    private void usage(final JCommander jCommander) {
        final StringBuilder sb = new StringBuilder();
        jCommander.usage(sb);
        System.out.println(sb.toString());
        System.exit(0);
    }
}
