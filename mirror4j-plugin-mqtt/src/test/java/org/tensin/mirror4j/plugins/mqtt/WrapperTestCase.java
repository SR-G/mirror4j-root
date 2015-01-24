package org.tensin.mirror4j.plugins.mqtt;

import org.junit.Assert;
import org.junit.Test;
import org.tensin.mirror4j.model.State;
import org.tensin.mirror4j.model.events.EventZtamp;
import org.tensin.mirror4j.plugins.mqtt.wrapper.MQTTWrapper;

/**
 * The Class WrapperTestCase.
 */
public class WrapperTestCase {

	/**
	 * Test wrapper.
	 */
	@Test
	public void testWrapper() {
		final MQTTWrapper wrapper = new MQTTWrapper();
		final String pattern = "${ztamp-id}|${date}|${timestamp}|${device-id}|${ztamp-state}";
		final long l = System.currentTimeMillis();
		final EventZtamp z = new EventZtamp(State.POSE, "ZTAMP1");
		z.setDeviceId("DEVICE1");
		z.setTimestamp(l);
		final String wrapped = wrapper.wrap(pattern, z);
		System.out.println(wrapped);
		final String[] v = wrapped.split("\\|");
		Assert.assertEquals(5, v.length);
		Assert.assertEquals("ZTAMP1", v[0]);
		Assert.assertEquals(String.valueOf(l), v[2]);
		Assert.assertEquals("DEVICE1", v[3]);
		Assert.assertEquals("POSE", v[4]);
	}
}
