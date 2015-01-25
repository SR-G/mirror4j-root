package org.tensin.mirror4j.model.operations;

import org.apache.commons.exec.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Class OperationShellExecution.
 */
@Root(name = "shell")
@Description(value = "Shell execution : executes any local shell script")
public class ShellExecution extends AbstractOperation implements IOperation {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The command. */
    @Attribute
    @Description(value = "The shell command to run. /bin/bash will be used as interpreter.")
    @BeanField
    private String command;

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute()
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        final Executor e = new Executor();
        final CommandLine c = new CommandLine("/bin/bash");
        c.addArgument("-c");
        c.addArgument(command, false); // false is important to prevent commons-exec from acting stupid
        LOGGER.info("Now executing [" + c.toString() + "]");
        e.execute(c);
    }

    /**
     * Gets the command.
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command.
     *
     * @param command
     *            the new command
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#start()
     */
    @Override
    public void start() throws Mirror4JException {

    }

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#stop()
     */
    @Override
    public void stop() throws Mirror4JException {

    }
}