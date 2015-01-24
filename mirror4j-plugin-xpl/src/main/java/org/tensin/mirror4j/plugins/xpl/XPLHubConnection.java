package org.tensin.mirror4j.plugins.xpl;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdp1802.xpl.xPL_Manager;
import org.cdp1802.xpl.xPL_MediaHandlerException;
import org.cdp1802.xpl.xPL_MessageI;
import org.cdp1802.xpl.xPL_MutableMessageI;
import org.cdp1802.xpl.device.xPL_DeviceI;
import org.tensin.mirror4j.model.Mirror4JException;
import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Class XPLHubConnection.
 */
public class XPLHubConnection {


    /** The connected. */
    private boolean connected;

    /** The mirror4 j device. */
    private xPL_DeviceI mirror4JDevice;

    /** The mirror4j device id. */
    private String mirror4jDeviceId = XPLConstants.DEFAULT_MIRROR4J_DEVICE_ID;

    /** The mirror4j instance id. */
    private String mirror4jInstanceId = XPLConstants.DEFAULT_MIRROR4J_INSTANCE_ID;

    /** The mirror4j vendor id. */
    private String mirror4jVendorId = XPLConstants.DEFAULT_MIRROR4J_VENDOR_ID;

    /** The mirror4j xpl schema type. */
    private String mirror4jXPLSchemaType = XPLConstants.DEFAULT_MIRROR4J_XPL_SCHEMA_TYPE;

    /** The mirror4j xpl schema class. */
    private String mirror4jXPLSchemaClass = XPLConstants.DEFAULT_MIRROR4J_XPL_SCHEMA_CLASS;

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Instantiates a new xPL hub connection.
     * 
     */
    public XPLHubConnection() {
    }

    /**
     * Connect.
     * 
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    public void connect() throws Mirror4JException {
        connected = true;
        try {
            if (mirror4JDevice == null) {
                final xPL_Manager xplManager = xPL_Manager.getManager();
                xplManager.createAndStartNetworkHandler();
                mirror4JDevice = xplManager.getDeviceManager().createDevice(mirror4jVendorId, mirror4jDeviceId, mirror4jInstanceId);
            }

            if ((mirror4JDevice != null) && !mirror4JDevice.isEnabled()) {
                mirror4JDevice.setEnabled(true);
            }
        } catch (final xPL_MediaHandlerException e) {
            throw new Mirror4JException(e);
        }
    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        mirror4JDevice.setEnabled(false);
        mirror4JDevice = null;
        connected = false;
    }

    /**
     * Gets the mirror4 j device.
     * 
     * @return the mirror4 j device
     */
    public xPL_DeviceI getMirror4JDevice() {
        return mirror4JDevice;
    }

    /**
     * Gets the mirror4j device id.
     * 
     * @return the mirror4j device id
     */
    public String getMirror4jDeviceId() {
        return mirror4jDeviceId;
    }

    /**
     * Gets the mirror4j instance id.
     * 
     * @return the mirror4j instance id
     */
    public String getMirror4jInstanceId() {
        return mirror4jInstanceId;
    }

    /**
     * Gets the mirror4j vendor id.
     * 
     * @return the mirror4j vendor id
     */
    public String getMirror4jVendorId() {
        return mirror4jVendorId;
    }

    /**
     * Gets the mirror4j xpl schema class.
     * 
     * @return the mirror4j xpl schema class
     */
    public String getMirror4jXPLSchemaClass() {
        return mirror4jXPLSchemaClass;
    }

    /**
     * Gets the mirror4j xpl schema type.
     * 
     * @return the mirror4j xpl schema type
     */
    public String getMirror4jXPLSchemaType() {
        return mirror4jXPLSchemaType;
    }

    /**
     * Checks if is connected.
     * 
     * @return true, if is connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Notify.
     * 
     * @param event
     *            the event
     * @throws Mirror4JException
     *             the mirror4 j exception
     */
    public void notify(final IEvent event) throws Mirror4JException {
        if (isConnected()) {
            final xPL_MutableMessageI msg = mirror4JDevice.createBroadcastMessage(xPL_MessageI.MessageType.STATUS, mirror4jXPLSchemaClass,
                    mirror4jXPLSchemaType);
            if (event.getValues() != null) {
                for (final Entry<String, String> entry : event.getValues().entrySet()) {
                    // final MutableNamedValueI eventValue = msg.addNamedValue(entry.getKey(), entry.getValue());
                    final String key = entry.getKey();
                    final String value = entry.getValue();
                    if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                        msg.addNamedValue(key, value);
                    }
                }
            }
            LOGGER.info("Sending msg [" + msg.toString() + "]");
            mirror4JDevice.sendMessage(msg);
        }
    }

    /**
     * Sets the connected.
     * 
     * @param connected
     *            the new connected
     */
    public void setConnected(final boolean connected) {
        this.connected = connected;
    }

    /**
     * Sets the mirror4 j device.
     * 
     * @param mirror4jDevice
     *            the new mirror4 j device
     */
    public void setMirror4JDevice(final xPL_DeviceI mirror4jDevice) {
        mirror4JDevice = mirror4jDevice;
    }

    /**
     * Sets the mirror4j device id.
     * 
     * @param mirror4jDeviceId
     *            the new mirror4j device id
     */
    public void setMirror4jDeviceId(final String mirror4jDeviceId) {
        this.mirror4jDeviceId = mirror4jDeviceId;
    }

    /**
     * Sets the mirror4j instance id.
     * 
     * @param mirror4jInstanceId
     *            the new mirror4j instance id
     */
    public void setMirror4jInstanceId(final String mirror4jInstanceId) {
        this.mirror4jInstanceId = mirror4jInstanceId;
    }

    /**
     * Sets the mirror4j vendor id.
     * 
     * @param mirror4jVendorId
     *            the new mirror4j vendor id
     */
    public void setMirror4jVendorId(final String mirror4jVendorId) {
        this.mirror4jVendorId = mirror4jVendorId;
    }

    public void setMirror4jXPLSchemaClass(final String mirror4jXPLSchemaClass) {
        this.mirror4jXPLSchemaClass = mirror4jXPLSchemaClass;
    }

    /**
     * Sets the mirror4j xpl schema type.
     * 
     * @param mirror4jXPLSchemaType
     *            the new mirror4j xpl schema type
     */
    public void setMirror4jXPLSchemaType(final String mirror4jXPLSchemaType) {
        this.mirror4jXPLSchemaType = mirror4jXPLSchemaType;
    }

}
