package org.tensin.mirror4j.plugins.mqtt;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.bean.BeanHelper;
import org.tensin.mirror4j.bean.Method;
import org.tensin.mirror4j.model.configuration.IDefinition;

/**
 * The Class MQTTBroker.
 */
@Root(name = "mqtt")
public class MQTTBrokerDefinition implements IDefinition {

	/**
	 * The Class Builder.
	 */
	public static final class Builder {

		/**
		 * Builds the.
		 *
		 * @return the MQTT broker
		 */
		public static MQTTBrokerDefinition build() {
			return new MQTTBrokerDefinition();
		}
		
	}
	
	/** The command. */
	@Attribute(name = "broker-url")
	@Description(value = "The broker url")
	@BeanField
	private String brokerUrl;
	
	/** The broker auth. */
	@Attribute(name = "broker-auth", required = false)
	@BeanField
	private boolean brokerAuth;

	/** The broker username. */
	@Attribute(name = "broker-username", required = false)
	@BeanField
	private String brokerUsername;
	
	/** The broker password. */
	@Attribute(name = "broker-password", required = false)
	@BeanField(exclude = { Method.TO_STRING })
	private String brokerPassword;
	
	/** The name. */
	@Attribute
	private String name;

	/**
	 * Gets the broker password.
	 *
	 * @return the broker password
	 */
	public String getBrokerPassword() {
		return brokerPassword;
	}
	
	/**
	 * Gets the broker url.
	 *
	 * @return the broker url
	 */
	public String getBrokerUrl() {
		return brokerUrl;
	}

	/**
	 * Gets the broker username.
	 *
	 * @return the broker username
	 */
	public String getBrokerUsername() {
		return brokerUsername;
	}

	/* (non-Javadoc)
	 * @see org.tensin.mirror4j.model.configuration.IDefinition#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Checks if is broker auth.
	 *
	 * @return true, if is broker auth
	 */
	public boolean isBrokerAuth() {
		return brokerAuth;
	}

	/**
	 * Sets the broker auth.
	 *
	 * @param brokerAuth the new broker auth
	 */
	public void setBrokerAuth(boolean brokerAuth) {
		this.brokerAuth = brokerAuth;
	}

	/**
	 * Sets the broker password.
	 *
	 * @param brokerPassword the new broker password
	 */
	public void setBrokerPassword(String brokerPassword) {
		this.brokerPassword = brokerPassword;
	}

	/**
	 * Sets the broker url.
	 *
	 * @param brokerUrl
	 *            the new broker url
	 */
	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	/**
	 * Sets the broker username.
	 *
	 * @param brokerUsername the new broker username
	 */
	public void setBrokerUsername(String brokerUsername) {
		this.brokerUsername = brokerUsername;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return BeanHelper.equals(this, obj);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return BeanHelper.hashCode(this);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	final StringBuilder sb = new StringBuilder();
    	sb.append(BeanHelper.toString(this));
        sb.append(", broker-password [").append(StringUtils.repeat("*", brokerPassword == null ? 0 : brokerPassword.length())).append("]");
        return  sb.toString();
    }
}
