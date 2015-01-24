package org.tensin.mirror4j.model.events;

import java.util.Map;
import java.util.TreeMap;

import org.tensin.mirror4j.model.State;

/**
 * The Class Event.
 */
public class EventZtamp extends EventDevice implements IEvent {

    /** The ztamp id. */
    private String ztampId;

    /**
     * Instantiates a new event.
     * 
     * @param state
     *            the state
     * @param ztampId
     *            the ztamp id
     */
    public EventZtamp(final State state, final String ztampId) {
        super(state);
        this.ztampId = ztampId;
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.events.IEvent#getValues()
     */
    @Override
    public Map<String, String> getValues() {
        final Map<String, String> result = new TreeMap<String, String>();
        result.put("timestamp", String.valueOf(getTimestamp()));
        result.put("date", getDate());
        result.put("device-id", getDeviceId());
        result.put("ztamp-state", getState().name());
        result.put("ztamp-id", getZtampId());
        return result;
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
     * Sets the ztamp id.
     * 
     * @param ztampId
     *            the new ztamp id
     */
    public void setZtampId(final String ztampId) {
        this.ztampId = ztampId;
    }

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.events.EventDevice#toString()
     */
    @Override
    public String toString() {
        return "ZTAMP [" + getZtampId() + "], state [" + getState().name() + "]";
    }

}
