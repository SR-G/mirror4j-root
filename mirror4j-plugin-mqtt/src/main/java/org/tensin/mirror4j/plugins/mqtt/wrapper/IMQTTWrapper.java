package org.tensin.mirror4j.plugins.mqtt.wrapper;

import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Interface IMQTTWrapper.
 */
public interface IMQTTWrapper {

	/**
	 * Wrap.
	 * @param pattern 
	 *
	 * @param event the event
	 * @return the string
	 */
	String wrap(final String pattern, final IEvent event);
	
}
