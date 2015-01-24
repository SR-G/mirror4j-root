/*
 *
 */
package org.tensin.mirror4j.model.configuration;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;

/**
 * The Class ZtampConfiguration : configuration of a single ztamp (defined by its id, with all the operations to execute).
 *
 * <mirror>
 * <ztamp id="999">
 * <shell name="stop-music" command="veille" />
 * <ssh hostname="..." user="..." command="veille" />
 * <sonos zones="all" action="pause" />
 * </ztamp>
 * <state type="up">
 * <ssh />
 * </state>
 *
 */
@Root(name = "ztamp")
@Description(value = "Configuration of a ztamp rfid")
public class ZtampConfiguration extends AbstractConfiguration {

    /** The ztamp id. */
    @Description(value = "The ztamp id on which the given operations will be executed")
    @Attribute(name = "id")
    private String ztampId;

    /** The description. */
    @Attribute(required = false)
    @Description(value = "Optionnal description to ease log reading")
    private String description;

    /** The last time seen. */
    private long lastTimeSeen;

    /** The active. */
    @Attribute(required = false)
    @Description(value = "If this ztamp is active or not", example = "true|false")
    private boolean active;

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the last time seen.
     *
     * @return the last time seen
     */
    public long getLastTimeSeen() {
        return lastTimeSeen;
    }

    /**
     * Gets the ztamp id.
     *
     * @return the ztamp id
     */
    public String getZtampId() {
        return ztampId;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active
     *            the new active
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Sets the last time seen.
     *
     * @param lastTimeSeen
     *            the new last time seen
     */
    public void setLastTimeSeen(final long lastTimeSeen) {
        this.lastTimeSeen = lastTimeSeen;
    }

    /**
     * Sets the ztamp id.
     *
     * @param ztampId
     *            the new ztamp id
     */
    public void setZtampId(final String ztampId) {
        this.ztampId = ztampId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Ztamp ID [").append(ztampId).append("]");
        if (StringUtils.isNotEmpty(description)) {
            sb.append(", description [").append(description).append("]");
        }
        sb.append(", ").append("on state(s) [").append(dumpStates()).append("], ").append(getOperations().size()).append(" operation(s) defined.");
        return sb.toString();
    }
}
