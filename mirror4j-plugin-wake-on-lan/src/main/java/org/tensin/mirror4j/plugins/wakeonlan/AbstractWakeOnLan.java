package org.tensin.mirror4j.plugins.wakeonlan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.model.operations.AbstractOperation;

/**
 * The Class AbstractWakeOnLan.
 */
public abstract class AbstractWakeOnLan extends AbstractOperation {

    /**
	 * Broadcast magic packet.
	 *
	 * @param macBytes the mac bytes
	 * @param broadcastIp the broadcast ip
	 * @param port the port
	 * @throws Mirror4JException the mirror4 j exception
	 */
	protected static void broadcastMagicPacket(byte[] macBytes, String broadcastIp, final int port) throws Mirror4JException {
		DatagramSocket socket = null;
        try {
            final byte[] bytes = new byte[6 + (16 * macBytes.length)];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            final InetAddress address = InetAddress.getByName(broadcastIp);
            final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
            socket = new DatagramSocket();
            socket.send(packet);

            LOGGER.debug("Magic packet sent.");
        } catch (final IOException e) {
            throw new Mirror4JException("Can't send magic packet with broadcast ip [" + broadcastIp + "] and port [" + port + "]", e);
        } finally {
        	IOUtils.closeQuietly(socket);
        }
	}

	/**
     * Gets the mac bytes.
     * 
     * @param mac
     *            the mac
     * @return the mac bytes
     * @throws IllegalArgumentException
     *             the illegal argument exception
     */
    protected static byte[] getMacBytes(final String mac) throws IllegalArgumentException {
        final byte[] bytes = new byte[6];
        final String[] hex = mac.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address [" + mac + "]");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address [" + mac + "]");
        }
        return bytes;
    }

	/** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

	/** The Constant DEFAULT_BROADCAST_IP. */
	private static final String DEFAULT_BROADCAST_IP = "192.168.255.255";

    /** The hostname. */
    @Attribute(required = false, name = "broadcast")
    @Description(value = "The broadcast IP to use. Default to 192.168.255.255")
    @BeanField
    private String broadcastIp = DEFAULT_BROADCAST_IP;

    /** The mac address. */
    @Attribute(name = "mac")
    @Description(value = "The mac address to awake. Valid examples are : 00:0D:61:08:22:4A, 00-0D-61-08-22-4A. One mac address per operation.")
    @BeanField
    private String macAddress;

    /** The Constant PORT. */
    private static final int DEFAULT_PORT = 9;
    
    /** The port. */
    @Attribute(name = "port", required = false)
    @BeanField
    private int port = DEFAULT_PORT;

    /**
     * Gets the broadcast ip.
     * 
     * @return the broadcast ip
     */
    public String getBroadcastIp() {
        return broadcastIp;
    }

	/**
     * Gets the mac address.
     * 
     * @return the mac address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
	 * Gets the operation.
	 *
	 * @return the operation
	 */
	protected abstract String getOperation();

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
		return port;
	}

    /**
     * Sets the broadcast ip.
     * 
     * @param broadcastIp
     *            the new broadcast ip
     */
    public void setBroadcastIp(final String broadcastIp) {
        this.broadcastIp = broadcastIp;
    }

    /**
     * Sets the mac address.
     * 
     * @param macAddress
     *            the new mac address
     */
    public void setMacAddress(final String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(int port) {
		this.port = port;
	}

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#start()
     */
    /**
     * Start.
     *
     * @throws Mirror4JException the mirror4 j exception
     */
    public void start() throws Mirror4JException {
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#stop()
     */
    /**
     * Stop.
     *
     * @throws Mirror4JException the mirror4 j exception
     */
    public void stop() throws Mirror4JException {
    }
}
