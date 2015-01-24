package org.tensin.mirror4j.model.operations;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.mirror4j.model.Constants;
import org.tensin.mirror4j.model.Mirror4JException;

/**
 * The Class Executor.
 */
public class Executor {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Execute.
     *
     * @param commandLine
     *            the command line
     * @return the int
     * @throws Mirror4JException
     *             the mirror exception
     */
    public int execute(final CommandLine commandLine) throws Mirror4JException {
        LOGGER.info("Now executing command [" + commandLine + "]");
        try {
            final DefaultExecutor executor = new DefaultExecutor();
            final ExecuteWatchdog watchdog = new ExecuteWatchdog(Constants.DEFAULT_EXECUTION_TIMEOUT_IN_MILLISECONDS);
            final PumpStreamHandler streamHandler = new PumpStreamHandler();
            executor.setExitValue(0);
            executor.setWatchdog(watchdog);
            executor.setStreamHandler(streamHandler); // Sets the stream handler
            final int exitValue = executor.execute(commandLine);
            if (exitValue != 0) {
                LOGGER.error("Can't execute command [" + commandLine + "]", exitValue);
            }
            return exitValue;
        } catch (final ExecuteException e) {
            throw new Mirror4JException(e);
        } catch (final IOException e) {
            throw new Mirror4JException(e);
        }
    }
}
