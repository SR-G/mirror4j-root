/*
 * 
 */
package org.tensin.mirror4j.model;

import org.apache.commons.lang3.StringUtils;

/**
 * The Enum States.
 */
public enum State {

	/** The unknown. */
	UNKNOWN(-1, -1, "UNKNOWN"), 
    /** The VIDE. */
    VIDE(0, 0, "VIDE"), // 0x0000
    /** The ENDROIT. */
    ENDROIT(1, 4, "ENDROIT"), // 0x0401
    /** The ENVERS. */
    ENVERS(1, 5, "ENVERS"), // 0x0501
    /** The POSE. */
    POSE(2, 1, "POSE"), // 0x0102
    /** The RETIRE. */
    RETIRE(2, 2, "RETIRE"); // 0x0202

    /**
     * Builds the.
     *
     * @param label the label
     * @return the state
     */
    public static State build(final String label) {
    	if (StringUtils.isNotEmpty(label)) {
    		if (StringUtils.equalsIgnoreCase(label, "ENDROIT")) {
    			return ENDROIT;
    		} else if (StringUtils.equalsIgnoreCase(label, "ENVERS")) {
    			return ENVERS;
    		} else if (StringUtils.equalsIgnoreCase(label, "POSE")) {
    			return POSE;
    		} else if (StringUtils.equalsIgnoreCase(label, "RETIRE")) {
    			return RETIRE;
    		}  
    	} 
    	
    	return VIDE;
    }
    
    /** The high. */
    private int low, high;

    /** The label. */
    private String label;
    
    /**
     * Instantiates a new states.
     *
     * @param low            the low
     * @param high            the high
     * @param label the label
     */
    private State(final int low, final int high, final String label) {
        this.low = low;
        this.high = high;
        this.label = label;
    }

    /**
     * Label.
     *
     * @return the string
     */
    public String label() {
    	return label;
    }

    /**
     * Match.
     * 
     * @param bytes
     *            the bytes
     * @return true, if successful
     */
    public boolean match(final byte[] bytes) {
        return ((low == bytes[0]) && (high == bytes[1]));
    }
    
}
