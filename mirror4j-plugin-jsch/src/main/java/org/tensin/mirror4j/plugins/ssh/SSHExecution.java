/*
 * 
 */
package org.tensin.mirror4j.plugins.ssh;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.bean.Method;
import org.tensin.mirror4j.model.Mirror4JException;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.AbstractOperation;
import org.tensin.mirror4j.model.operations.IOperation;
import org.vngx.jsch.Channel;
import org.vngx.jsch.ChannelExec;
import org.vngx.jsch.JSch;
import org.vngx.jsch.Session;
import org.vngx.jsch.UserInfo;
import org.vngx.jsch.exception.JSchException;

/**
 * The Class OperationShellExecution.
 */
@Root(name = "ssh")
@Description(value = "SSH Execution : execute a remote shell command through ssh. Only available with the plugin mirror4j-plugin-jsch")
public class SSHExecution extends AbstractOperation implements IOperation {

    /**
     * The Class MyUserInfo.
     */
    private static final class SSHUserInfo implements UserInfo {

        /** The password. */
        private String password;

        /* (non-Javadoc)
         * @see com.jcraft.jsch.UserInfo#getPassphrase()
         */
        @Override
        public String getPassphrase() {
            return null;
        }

        /* (non-Javadoc)
         * @see com.jcraft.jsch.UserInfo#getPassword()
         */
        @Override
        public String getPassword() {
            return password;
        }

        /* (non-Javadoc)
         * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
         */
        @Override
        public boolean promptPassphrase(final String arg0) {
            return true;
        }

        /* (non-Javadoc)
         * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
         */
        @Override
        public boolean promptPassword(final String arg0) {
            return false;
        }

        /* (non-Javadoc)
         * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
         */
        @Override
        public boolean promptYesNo(final String arg0) {
            return true;
        }

        /**
         * Sets the password.
         * 
         * @param password
         *            the new password
         */
        public void setPassword(final String password) {
            this.password = password;
        }

        /* (non-Javadoc)
         * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
         */
        @Override
        public void showMessage(final String arg0) {
            LOGGER.info(arg0);
        }
    }

    /** The Constant BUFFER_SIZE. */
    private static final int BUFFER_SIZE = 16384;

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

    /** The password. */
    @Attribute(required = false)
    @Description(value = "The remote password to use (optional - only in case you don't have put ssh keys)")
    @BeanField(exclude = { Method.TO_STRING })
    private String password;

    /** The command. */
    @Attribute
    @Description(value = "The remote command to run")
    @BeanField
    private String command;

    /**
     * Do exec.
     * 
     * @param machine
     *            the machine
     * @param user
     *            the user
     * @param passwd
     *            the passwd
     * @param commande
     *            the commande
     * @return the string
     * @throws Mirror4JException
     *             the mirror exception
     */
    public String doExec(final String machine, final String user, final String passwd, final String commande) throws Mirror4JException {
        /* Creation JSch + ajout clés */
        final JSch jsch = getJsch(user, passwd);

        final StringBuffer result = new StringBuffer();
        Channel channel = null;
        InputStream in = null;
        Session session = null;
        try {
            /* Ouverture de session */
            session = getConnectedSession(jsch, machine, user, passwd);
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(commande);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            ((ChannelExec) channel).setExtOutputStream(System.err);
            ((ChannelExec) channel).setOutputStream(System.err);
            in = channel.getInputStream();
            channel.connect();

            final byte[] tmp = new byte[BUFFER_SIZE];
            while (true) {
                while (in.available() > 0) {
                    final int i = in.read(tmp, 0, BUFFER_SIZE);
                    if (i < 0) {
                        break;
                    }
                    result.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (final Exception ee) {
                }
            }
        } catch (final JSchException e) {
            throw new Mirror4JException(e);
        } catch (final IOException e) {
            throw new Mirror4JException("I/O error while executing command [" + command + "]", e);
        } finally {
            IOUtils.closeQuietly(in);
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        return result.toString();
    }

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
        LOGGER.info(doExec(hostname, username, password, command));
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
     * Method.
     * 
     * @param jsch
     *            the jsch
     * @param machine
     *            the machine
     * @param user
     *            the user
     * @param passwd
     *            the passwd
     * @return the connected session
     * @throws JSchException
     *             the j sch exception
     */
    private Session getConnectedSession(final JSch jsch, final String machine, final String user, final String passwd) throws JSchException {
        final Session session = jsch.createSession(user, machine, 22);
        final SSHUserInfo ui = new SSHUserInfo();
        ui.setPassword(passwd);
        session.setUserInfo(ui);

        // final java.util.Properties config=new java.util.Properties();
        // config.put("compression.s2c", "zlib,none");
        // config.put("compression.c2s", "zlib,none");
        // session.setConfig(config);

        if (StringUtils.isNotEmpty(passwd)) {
            session.connect(passwd.getBytes());
        } else {
            session.connect();
        }
        return session;
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
     * Method.
     * 
     * @param user
     *            the user
     * @param passwd
     *            the passwd
     * @return the jsch
     * @throws Mirror4JException
     *             the mirror exception
     */
    private JSch getJsch(final String user, final String passwd) throws Mirror4JException {
        final JSch jsch = JSch.getInstance();
        try {
            jsch.setKnownHosts(System.getProperty("user.home") + "/.ssh/known_hosts");
        } catch (final JSchException e) {
            throw new Mirror4JException(e);
        }

        // if (StringUtils.isEmpty(passwd)) {
        // try {
        // jsch.addIdentity(user, prvKey.clone(), pubKey.clone(), new byte[0]);
        // } catch (final JSchException jschex) {
        // throw new MirrorException("Erreur lors de l'enregistrement des cles privees/public", jschex);
        // }
        // }
        return jsch;
    }


    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
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
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(final String password) {
        this.password = password;
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