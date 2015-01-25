package org.tensin.mirror4j.plugins.ssh;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.bean.Method;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.AbstractOperation;
import org.tensin.mirror4j.model.operations.IOperation;


/**
 * The Class OperationShellExecution.
 */
@Root(name = "ssh")
@Description(value = "SSH Execution : execute a remote shell command through ssh. Only available with the plugin mirror4j-plugin-ssh")
public class SSHExecution extends AbstractOperation implements IOperation {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The hostname. */
    @Attribute
    @Description(value = "The remote hostname to use")
    @BeanField
    private String hostname;

    /** The username. */
    @Attribute
    @Description(value = "The remote username to connect with")
    @BeanField
    private String username;

    /** The command. */
    @Attribute
    @Description(value = "The remote command to run")
    @BeanField
    private String command;

    /** The password. */
    @Attribute(required = false)
    @Description(value = "The remote password to use (optional - only in case you don't have put ssh keys)")
    @BeanField(exclude = { Method.TO_STRING })
    private String password;

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute()
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        LOGGER.info("Now executing command ["
                + command
                + "] on hostname ["
                + hostname
                + "] with user ["
                + username
                + "]"
                + (StringUtils.isEmpty(password) ? ", using ssh keys as no password provided" : ", using provided password ["
                        + StringUtils.repeat("*", password.length()) + "]"));
        final SSHClient ssh = new SSHClient();
        try {
            // ssh.loadKnownHosts();
            ssh.connect(hostname);
            ssh.authPassword(username, password);

            final Session session = ssh.startSession();
            try {
                final Command cmd = session.exec(command);
                LOGGER.info(IOUtils.readFully(cmd.getInputStream()).toString());
                cmd.join(5, TimeUnit.SECONDS);
                LOGGER.info("execution exit status: " + cmd.getExitStatus());
            } finally {
                session.close();
            }
        } catch (final IOException e) {
            throw new Mirror4JException("Error while executing command [" + command + "] on host [" + hostname + "]", e);
        } finally {
            try {
                ssh.disconnect();
            } catch (final IOException e) {
            }
        }
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
     * Gets the hostname.
     * 
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
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

    /**
     * Sets the hostname.
     * 
     * @param hostname
     *            the new hostname
     */
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     * Sets the username.
     * 
     * @param username
     *            the new username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#start()
     */
    @Override
    public void start() throws Mirror4JException {
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#stop()
     */
    @Override
    public void stop() throws Mirror4JException {
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	final StringBuilder sb = new StringBuilder();
    	sb.append(super.toString());
        sb.append(", password [").append(StringUtils.repeat("*", password == null ? 0 : password.length())).append("]");
        return  sb.toString();
    }
}
