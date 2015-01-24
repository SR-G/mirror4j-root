package org.tensin.mirror4j.model.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.tensin.mirror4j.model.State;

/**
 * The Class Event.
 */
public class EventDevice implements IEvent {

    /** The state. */
    private State state;

    /** The device id. */
    private String deviceId;

    /** The date. */
    private long timestamp;

    /**
     * Instantiates a new event.
     * 
     * @param state
     *            the state
     */
    public EventDevice(final State state) {
        super();
        this.state = state;
        timestamp = System.currentTimeMillis();
    }

	/**
     * Gets the date.
     * 
     * @return the date
     */
    public String getDate() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp)).toString();
    }

	/**
     * Gets the device id.
     * 
     * @return the device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
		return timestamp;
	}

    /* (non-Javadoc)
     * @see org.tensin.mirror4j.model.events.IEvent#getValues()
     */
    /**
     * Gets the values.
     *
     * @return the values
     */
    @Override
    public Map<String, String> getValues() {
        final Map<String, String> result = new TreeMap<String, String>();
        result.put("timestamp", String.valueOf(getTimestamp()));
        result.put("date", getDate());
        result.put("device-state", getState().name());
        result.put("device-id", getDeviceId());
        return result;
    }

    /**
     * Sets the device id.
     * 
     * @param deviceId
     *            the new device id
     */
    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Sets the state.
     * 
     * @param state
     *            the new state
     */
    public void setState(final State state) {
        this.state = state;
    }

    
    /**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "DEVICE [" + state.name() + "]";
    }

}
