package org.tensin.mirror4j.model;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.Visitor;
import org.simpleframework.xml.strategy.VisitorStrategy;
import org.tensin.mirror4j.model.configuration.DeviceConfiguration;
import org.tensin.mirror4j.model.configuration.IDefinition;
import org.tensin.mirror4j.model.configuration.IDefinitionInjection;
import org.tensin.mirror4j.model.configuration.Mirror4JConfiguration;
import org.tensin.mirror4j.model.configuration.ZtampConfiguration;
import org.tensin.mirror4j.model.events.EventDevice;
import org.tensin.mirror4j.model.events.EventZtamp;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.IOperation;

/**
 * The Class Processor.
 */
public class Processor extends Thread {

    /**
     * Gets the available operations.
     *
     * @return the available operations
     */
    private static Set<Class<? extends IOperation>> getAvailableOperations(final Reflections reflections) {
        return reflections.getSubTypesOf(IOperation.class);
    }

    /**
     * Gets the single instance of Processor.
     *
     * @return single instance of Processor
     */
    public static Processor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Processor();
        }
        return INSTANCE;
    }

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The queue. */
    public final LinkedBlockingQueue<IEvent> queue = new LinkedBlockingQueue<IEvent>();

    /** The active. */
    private boolean active;

    /** The started. */
    private boolean started;

    /** The INSTANCE. */
    private static Processor INSTANCE;

    /** The configuration. */
    private Mirror4JConfiguration configuration;

    /** The Constant foundZtamps. */
    private static final Set<String> foundZtamps = new TreeSet<String>();

    /**
     * Instantiates a new processor.
     */
    private Processor() {

    }

    /**
     * Dump available operations.
     *
     * @param availableOperations
     *            the available operations
     * @return the string
     */
    private String dumpAvailableOperations(final Set<Class<? extends IOperation>> availableOperations) {
        final StringBuilder sb = new StringBuilder();
        String sep = "";
        for (final Class<? extends IOperation> availableOperation : availableOperations) {
            final Root rootAnnotation = availableOperation.getAnnotation(Root.class);
            if (rootAnnotation != null) {
                sb.append(sep).append(rootAnnotation.name());
                sep = ", ";
            }
        }
        return sb.toString();
    }

    /**
     * Dump configuration.
     */
    public void dumpConfiguration() {
        final StringBuilder sb = new StringBuilder();
        sb.append("What is configured :");
        if (CollectionUtils.isNotEmpty(configuration.getDefinitions())) {
            sb.append("\n    - ").append("Definitions : ");
            for (final IDefinition definition : configuration.getDefinitions()) {
                sb.append("\n      * ").append(definition.toString());
            }
        }
        if (CollectionUtils.isNotEmpty(configuration.getZtampConfigurations())) {
            for (final ZtampConfiguration c : configuration.getZtampConfigurations()) {
                sb.append("\n    - ").append(c.toString()).append(" : ").append(dumpOperations(c.getOperations())).append("");
            }
        }
        if (CollectionUtils.isNotEmpty(configuration.getDeviceConfigurations())) {
            for (final DeviceConfiguration c : configuration.getDeviceConfigurations()) {
                sb.append("\n    - Device state [").append(c.dumpStates()).append("] : ");
                if (CollectionUtils.isEmpty(c.getOperations())) {
                    sb.append("(no operations)");
                } else {
                    sb.append(dumpOperations(c.getOperations()));
                }
            }
        }
        if (configuration.isPurgeOnStart()) {
            sb.append("\nPurge on start is activated");
        } else {
            sb.append("\nPurge on start is not activated");
        }
        LOGGER.info(sb.toString());
    }

    /**
     * Dump operations.
     *
     * @param operations
     *            the operations
     * @return the string
     */
    private String dumpOperations(final Collection<IOperation> operations) {
        final StringBuilder sb = new StringBuilder();
        for (final IOperation operation : operations) {
            sb.append("\n      * ").append(operation.toString()).append(" (").append(operation.getClass().getSimpleName()).append(")");
        }
        return sb.toString();
    }

    /**
     * Execute operation.
     *
     * @param event
     *            the event
     * @throws Mirror4JException
     *             the mirror exception
     */
    private void executeOperation(final EventDevice event) throws Mirror4JException {
        final org.tensin.mirror4j.model.State state = event.getState();
        LOGGER.info("Received new device state [" + state + "]");
        if (configuration.hasDeviceConfiguration()) {
            final DeviceConfiguration deviceConfiguration = configuration.getDeviceConfiguration(state);
            if (deviceConfiguration != null) {
                for (final IOperation operation : deviceConfiguration.getOperations()) {
                    launchEvent(operation, event);
                }
            }
        }
    }

    /**
     * Execute operation.
     *
     * @param event
     *            the event
     * @throws Mirror4JException
     *             the mirror exception
     */
    private void executeOperation(final EventZtamp event) throws Mirror4JException {
        final String ztampId = event.getZtampId();
        final org.tensin.mirror4j.model.State state = event.getState();
        LOGGER.info("Received for ztamp [" + ztampId + "] event [" + state + "]");
        final ZtampConfiguration ztampConfiguration = configuration.getZtampConfiguration(ztampId, state);
        if (ztampConfiguration != null) {
            LOGGER.info("Now executing linked operations for ztampid " + ztampConfiguration.toString() + "");
            for (final IOperation operation : ztampConfiguration.getOperations()) {
                launchEvent(operation, event);
            }
        }
    }

    /**
     * Gets the available definitions.
     *
     * @return the available definitions
     */
    private Set<Class<? extends IDefinition>> getAvailableDefinitions(final Reflections reflections) {
        return reflections.getSubTypesOf(IDefinition.class);
    }

    /**
     * Halt.
     *
     * @throws Mirror4JException
     *             the mirror exception
     */
    public void halt() throws Mirror4JException {
        active = false;
        stopAllOperations();
    }

    /**
     * Checks if is started.
     *
     * @return true, if is started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Launch event.
     *
     * @param operation
     *            the operation
     * @param event
     *            the event
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    private void launchEvent(final IOperation operation, final IEvent event) throws Mirror4JException {
        final long start = System.currentTimeMillis();
        LOGGER.info("Now executing " + operation.toString() + ", for event " + event.toString() + "");
        try {
            operation.execute(event);
        } finally {
            LOGGER.info("Operation executed in [" + (System.currentTimeMillis() - start) + "ms]");
        }
    }

    /**
     * Load configuration.
     *
     * @param configurationFileName
     *            the configuration file name
     * @throws Mirror4JException
     *             the mirror exception
     */
    public void loadConfiguration(final String configurationFileName) throws Mirror4JException {
        final Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("org.tensin")).setScanners(
                new SubTypesScanner()));

        final Set<Class<? extends IOperation>> availableOperations = getAvailableOperations(reflections);
        final Set<Class<? extends IDefinition>> availableDefinitions = getAvailableDefinitions(reflections);
        LOGGER.info("Available operations are [" + dumpAvailableOperations(availableOperations) + "]");
        LOGGER.info("Loading configuration [" + configurationFileName + "]");

        final Visitor visitor = new MirrorConfigurationVisitor(availableOperations, availableDefinitions);
        final Strategy strategy = new VisitorStrategy(visitor);
        final Serializer serializer = new Persister(strategy);
        final File source = new File(configurationFileName);
        try {
            configuration = serializer.read(Mirror4JConfiguration.class, source);
        } catch (final Exception e) {
            throw new Mirror4JException("Can't load configuration from [" + configurationFileName + "]", e);
        }
    }

    /**
     * Notify.
     *
     * @param event
     *            the event
     */
    public synchronized void notify(final IEvent event) {
        queue.offer(event);
    }

    /**
     * Register new ztamp.
     *
     * @param ztamp
     *            the ztamp
     */
    private void registerNewZtamp(final EventZtamp ztamp) {
        if (!foundZtamps.contains(ztamp.getZtampId())) {
            foundZtamps.add(ztamp.getZtampId());
            LOGGER.info("New never seen before ztamp found [" + ztamp.getZtampId() + "]");
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        super.run();
        IEvent event = null;
        while (active) {
            try {
                event = queue.take();
                if (shallWeProcess()) {
                    if (event instanceof EventZtamp) {
                        registerNewZtamp((EventZtamp) event);
                        executeOperation((EventZtamp) event);
                    } else if (event instanceof EventDevice) {
                        executeOperation((EventDevice) event);
                    }
                } else {
                    LOGGER.info("Purging event [" + (event == null ? "null" : event.toString()) + "]");
                }
            } catch (final Mirror4JException e) {
                LOGGER.error("Can't process event [" + (event == null ? "null" : event.toString()) + "]", e);
            } catch (final InterruptedException e) {
            }
        }
    }

    /**
     * Sets the started.
     *
     * @param started
     *            the new started
     */
    public void setStarted(final boolean started) {
        this.started = started;
    }

    /**
     * Shall we process.
     *
     * @return true, if successful
     */
    private boolean shallWeProcess() {
        if (configuration.isPurgeOnStart() && !started) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start() {
        setName(Constants.THREAD_NAME + "-PROCESSOR");
        LOGGER.info("Starting processing thread");
        try {
            startAllOperations();
        } catch (final Mirror4JException e) {
            LOGGER.error("Error while pre-loading operations", e);
        }
        active = true;
        super.start();
    }

    /**
     * Start all operations.
     *
     * @throws Mirror4JException
     *             the mirror exception
     */
    private void startAllOperations() throws Mirror4JException {
        LOGGER.info("Pre-loading all operations");
        final long start = System.currentTimeMillis();
        try {
            if (!CollectionUtils.isEmpty(configuration.getZtampConfigurations())) {
                for (final ZtampConfiguration c : configuration.getZtampConfigurations()) {
                    for (final IOperation operation : c.getOperations()) {
                        startOperation(operation);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(configuration.getDeviceConfigurations())) {
                for (final DeviceConfiguration c : configuration.getDeviceConfigurations()) {
                    for (final IOperation operation : c.getOperations()) {
                        startOperation(operation);
                    }
                }
            }
        } finally {
            LOGGER.info("End of pre-loading operations in [" + (System.currentTimeMillis() - start) + "ms]");
        }
    }

    /**
     * Start operation.
     *
     * @param operation
     *            the operation
     * @throws Mirror4JException
     */
    private void startOperation(final IOperation operation) throws Mirror4JException {
        if (operation instanceof IDefinitionInjection) {
            ((IDefinitionInjection) operation).setDefinitions(configuration.getDefinitions());
        }
        operation.start();
    }

    /**
     * Stop all operations.
     *
     * @throws Mirror4JException
     *             the mirror exception
     */
    private void stopAllOperations() throws Mirror4JException {
        LOGGER.info("Post-handling of all operations");
        if (!CollectionUtils.isEmpty(configuration.getZtampConfigurations())) {
            for (final ZtampConfiguration c : configuration.getZtampConfigurations()) {
                for (final IOperation operation : c.getOperations()) {
                    operation.stop();
                }
            }
        }
        if (!CollectionUtils.isEmpty(configuration.getDeviceConfigurations())) {
            for (final DeviceConfiguration c : configuration.getDeviceConfigurations()) {
                for (final IOperation operation : c.getOperations()) {
                    operation.stop();
                }
            }
        }
    }

}