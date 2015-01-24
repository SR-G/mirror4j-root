package org.tensin.mirror4j.plugins.mqtt.wrapper;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Class MQTTWrapper.
 */
public class MQTTWrapper implements IMQTTWrapper {

	/**
	 * Replace field.
	 *
	 * @param mask the mask
	 * @param value the value
	 */
	private String replaceField(final String s, final String mask, final String value) {
		return StringUtils.replace(s, mask, value);
	}

	/* (non-Javadoc)
	 * @see org.tensin.mirror4j.plugins.mqtt.IMQTTWrapper#wrap(java.lang.String, org.tensin.mirror4j.model.events.IEvent)
	 */
	@Override
	public String wrap(final String pattern, final IEvent event) {
		String result = pattern;
		for (final Entry<String, String> entry : event.getValues().entrySet()) {
			final String key = "${" + entry.getKey() + "}";
			final String value = entry.getValue();
			result = replaceField(result, key, value);
		}
		return result;
	}
}