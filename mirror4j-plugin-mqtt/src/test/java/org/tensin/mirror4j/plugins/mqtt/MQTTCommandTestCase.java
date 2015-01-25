package org.tensin.mirror4j.plugins.mqtt;

import org.junit.Test;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.model.State;
import org.tensin.mirror4j.model.configuration.IDefinition;
import org.tensin.mirror4j.model.events.EventDevice;

import com.google.common.collect.ImmutableList;

/**
 * The Class WrapperTestCase.
 */
public class MQTTCommandTestCase {

	/**
	 * Test wrapper.
	 * @throws Mirror4JException 
	 */
	@Test
	public void testWrapper() throws Mirror4JException {
		final MQTTExecution execution = new MQTTExecution();
		final MQTTBrokerDefinition broker = new MQTTBrokerDefinition();
		broker.setBrokerUrl("tcp://192.168.8.40:1883");
		broker.setBrokerAuth(true);
		broker.setBrokerUsername("sergio");
		broker.setBrokerPassword("none");
		broker.setName("broker1");
		
		try {
			execution.setActive(true);
			execution.setBrokerTopic("topic/action");
			execution.setBrokerAlias("broker1");
			execution.setDefinitions(ImmutableList.of((IDefinition)broker));
			execution.start();
			
			final EventDevice event = new EventDevice(State.ENDROIT);
			event.setDeviceId("DEVICE1");
			execution.execute(event);
		} finally {
			execution.stop();
		}
	}
}