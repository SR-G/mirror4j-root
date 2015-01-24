package org.tensin.mirror4j.plugins.mqtt;

import org.junit.Test;

/**
 * The Class LogTestCase.
 */
public class LogTestCase {

	/**
	 * Test log.
	 */
	@Test
	public void testLog() {
		final MQTTExecution e = new MQTTExecution();
		e.logAvailablePatternForEvents();
	}
	
}
