package org.tensin.mirror4j.plugins.sonos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.model.Mirror4JException;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.AbstractOperation;
import org.tensin.mirror4j.model.operations.IOperation;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commander.DaemonController;

/**
 * The Class SonosOperation.
 */
@Root(name = "sonos")
@Description(value = "Sonos operation : executes a command (like play, stop, mute, ...) on one or several or all sonos boxes. Only available with the plugin mirror4j-plugin-sonos")
public class SonosOperation extends AbstractOperation implements IOperation {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The zones. */
    @Attribute
    @Description(value = "The zones to act on. You can put one value like 'kitchen' or several ones like 'kitchen,room1' or the 'ALL' keyword", example = "kitchen,room1")
    @BeanField
    private String zones;

    /** The action. */
    @Attribute
    @Description(value = "The action to use. Check Sonos documentation http://github.com/SR-G/sonos-java", example = "Something like 'play', 'mute', 'removeall', 'next', 'prev' or 'stop' for example")
    @BeanField
    private String action;

    /** The java controller. */
    private static DaemonController javaController;

    /**
     * The Constant controller.
     * 
     * @throws Mirror4JException
     *             the mirror exception
     */

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute()
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        LOGGER.info("About to execute sonos operation on zones [" + zones + "] with command [" + action + "]");
        try {
            if (javaController != null) {
                javaController.execute(zones, action);
            }
        } catch (final SonosException e) {
            throw new Mirror4JException("Can't execute sonos operation on zones [" + zones + "] with command [" + action + "]", e);
        }
    }

    /**
     * Gets the action.
     * 
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Gets the zones.
     * 
     * @return the zones
     */
    public String getZones() {
        return zones;
    }

    /**
     * Sets the action.
     * 
     * @param action
     *            the new action
     */
    public void setAction(final String action) {
        this.action = action;
    }

    /**
     * Sets the zones.
     * 
     * @param zones
     *            the new zones
     */
    public void setZones(final String zones) {
        this.zones = zones;
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#start()
     */
    @Override
    public void start() throws Mirror4JException {
        if (javaController == null) {
        	LOGGER.info("Now starting SONOS controller");
        	 javaController = DaemonController.createController();
            try {
                javaController.start();
            } catch (final SonosException e) {
                throw new Mirror4JException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#stop()
     */
    @Override
    public void stop() throws Mirror4JException {
        if (javaController != null) {
            try {
                javaController.stop();
            } catch (final SonosException e) {
                throw new Mirror4JException(e);
            }
        }
    }
}