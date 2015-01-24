package org.tensin.mirror4j.model.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.model.State;

/**
 * The Class Mirror4JConfiguration.
 */
@Root(name = "mirror")
@Description(value = "Global mirror4j configuration")
public class Mirror4JConfiguration {

    /** The ztamp configurations. */
    @ElementList(required = false, inline = true)
    @Description(value = "Configuration of the various ztamp to define")
    public Collection<ZtampConfiguration> ztampConfigurations = new ArrayList<ZtampConfiguration>();

    /** The device configurations. */
    @ElementList(required = false, inline = true)
    @Description(value = "Configuration of the mir:ror device itself")
    public Collection<DeviceConfiguration> deviceConfigurations = new ArrayList<DeviceConfiguration>();
    
    /** The purge on start. */
    @Attribute(required = false, name = "purge-on-start")
    public boolean purgeOnStart;

	/** The definitions. */
	@ElementList(required = false, name = "definitions")
	public Collection<IDefinition> definitions = new ArrayList<IDefinition>();

    /**
     * Gets the definitions.
     *
     * @return the definitions
     */
    public Collection<IDefinition> getDefinitions() {
		return definitions;
	}

	/**
     * Gets the device configuration.
     * 
     * @param state
     *            the state
     * @return the device configuration
     */
    public DeviceConfiguration getDeviceConfiguration(final State state) {
        for (final DeviceConfiguration configuration : deviceConfigurations) {
            if (configuration.hasState(state)) {
                return configuration;
            }
        }
        return null;
    }

	/**
     * Gets the device configurations.
     * 
     * @return the device configurations
     */
    public Collection<DeviceConfiguration> getDeviceConfigurations() {
        return deviceConfigurations;
    }

	/**
     * Gets the configuration.
     * 
     * @param ztampId
     *            the ztamp id
     * @param state
     *            the state
     * @return the configuration
     */
    public ZtampConfiguration getZtampConfiguration(final String ztampId, final State state) {
        for (final ZtampConfiguration configuration : ztampConfigurations) {
            if (configuration.getZtampId().equalsIgnoreCase(ztampId)) {
                if (configuration.hasState(state)) {
                    return configuration;
                }
            }
        }
        return null;
    }

	/**
     * Gets the ztamp configurations.
     * 
     * @return the ztamp configurations
     */
    public Collection<ZtampConfiguration> getZtampConfigurations() {
        return ztampConfigurations;
    }

    /**
     * Checks for device configuration.
     * 
     * @return true, if successful
     */
    public boolean hasDeviceConfiguration() {
        return (deviceConfigurations != null) && (deviceConfigurations.size() > 0);
    }

    /**
     * Checks if is purge on start.
     *
     * @return true, if is purge on start
     */
    public boolean isPurgeOnStart() {
		return purgeOnStart;
	}

    /**
	 * Sets the definitions.
	 *
	 * @param definitions the new definitions
	 */
	public void setDefinitions(Collection<IDefinition> definitions) {
		this.definitions = definitions;
	}

    /**
     * Sets the device configurations.
     * 
     * @param deviceConfigurations
     *            the new device configurations
     */
    public void setDeviceConfigurations(final Collection<DeviceConfiguration> deviceConfigurations) {
        this.deviceConfigurations = deviceConfigurations;
    }

    /**
	 * Sets the purge on start.
	 *
	 * @param purgeOnStart the new purge on start
	 */
	public void setPurgeOnStart(boolean purgeOnStart) {
		this.purgeOnStart = purgeOnStart;
	}

    /**
     * Sets the ztamp configurations.
     * 
     * @param ztampConfigurations
     *            the new ztamp configurations
     */
    public void setZtampConfigurations(final Collection<ZtampConfiguration> ztampConfigurations) {
        this.ztampConfigurations = ztampConfigurations;
    }
}
