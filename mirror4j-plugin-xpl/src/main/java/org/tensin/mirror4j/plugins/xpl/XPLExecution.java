package org.tensin.mirror4j.plugins.xpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.AbstractOperation;
import org.tensin.mirror4j.model.operations.IOperation;

/**
 * The Class OperationShellExecution.
 */
@Root(name = "xpl")
@Description(value = "Notify the event (either device or ztamp event) on the local xPL bus. Only available with the plugin mirror4j-plugin-xpl")
public class XPLExecution extends AbstractOperation implements IOperation {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The mirror4j device id. */
    @Description(value = "The mirror4j device id", example = XPLConstants.DEFAULT_MIRROR4J_DEVICE_ID)
    @Attribute(required = false)
    @BeanField
    private String mirror4jDeviceId = XPLConstants.DEFAULT_MIRROR4J_DEVICE_ID;

    /** The mirror4j instance id. */
    @Description(value = "The mirror4j instance id", example = XPLConstants.DEFAULT_MIRROR4J_INSTANCE_ID)
    @Attribute(required = false)
    @BeanField
    private String mirror4jInstanceId = XPLConstants.DEFAULT_MIRROR4J_INSTANCE_ID;

    /** The mirror4j vendor id. */
    @Description(value = "The mirror4j vendor id", example = XPLConstants.DEFAULT_MIRROR4J_VENDOR_ID)
    @Attribute(required = false)
    @BeanField
    private String mirror4jVendorId = XPLConstants.DEFAULT_MIRROR4J_VENDOR_ID;

    /** The mirror4j xpl schema type. */
    @Description(value = "The xpl schema type to use in the sent xpl messages", example = XPLConstants.DEFAULT_MIRROR4J_XPL_SCHEMA_TYPE)
    @Attribute(required = false)
    @BeanField
    private String mirror4jXPLSchemaType = XPLConstants.DEFAULT_MIRROR4J_XPL_SCHEMA_TYPE;

    /** The mirror4j xpl schema class. */
    @Description(value = "The xpl schema class to use in the sent xpl messages", example = XPLConstants.DEFAULT_MIRROR4J_XPL_SCHEMA_CLASS)
    @Attribute(required = false)
    @BeanField
    private String mirror4jXPLSchemaClass = XPLConstants.DEFAULT_MIRROR4J_XPL_SCHEMA_CLASS;



    /** The xpl hub connection. */
    private XPLHubConnection xplHubConnection;

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute()
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        LOGGER.info("Now sending event on XPL bus ");
        if (!xplHubConnection.isConnected()) {
            xplHubConnection.connect();
        }

        if (xplHubConnection.isConnected()) {
            xplHubConnection.notify(event);
        }
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
     * Gets the xpl hub connection.
     * 
     * @return the xpl hub connection
     */
    public XPLHubConnection getXplHubConnection() {
        return xplHubConnection;
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

    /**
     * Sets the mirror4j xpl schema class.
     * 
     * @param mirror4jXPLSchemaClass
     *            the new mirror4j xpl schema class
     */
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

    /**
     * Sets the xpl hub connection.
     * 
     * @param xplHubConnection
     *            the new xpl hub connection
     */
    public void setXplHubConnection(final XPLHubConnection xplHubConnection) {
        this.xplHubConnection = xplHubConnection;
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#start()
     */
    @Override
    public void start() throws Mirror4JException {
    	LOGGER.info("Now starting XPL hub");
    	
        xplHubConnection = new XPLHubConnection();
        xplHubConnection.setMirror4jDeviceId(mirror4jDeviceId);
        xplHubConnection.setMirror4jInstanceId(mirror4jInstanceId);
        xplHubConnection.setMirror4jVendorId(mirror4jVendorId);
        xplHubConnection.setMirror4jXPLSchemaClass(mirror4jXPLSchemaClass);
        xplHubConnection.setMirror4jXPLSchemaType(mirror4jXPLSchemaType);
        xplHubConnection.connect();
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#stop()
     */
    @Override
    public void stop() throws Mirror4JException {
        if (xplHubConnection != null) {
            xplHubConnection.disconnect();
        }
    }
}