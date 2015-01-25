package org.tensin.mirror4j.plugins.mqtt;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.model.State;
import org.tensin.mirror4j.model.configuration.IDefinition;
import org.tensin.mirror4j.model.configuration.IDefinitionInjection;
import org.tensin.mirror4j.model.events.EventDevice;
import org.tensin.mirror4j.model.events.EventZtamp;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.AbstractOperation;
import org.tensin.mirror4j.model.operations.IOperation;
import org.tensin.mirror4j.plugins.mqtt.wrapper.IMQTTWrapper;
import org.tensin.mirror4j.plugins.mqtt.wrapper.MQTTWrapper;

/**
 * The Class MQTTExecution.
 */
@Root(name = "mqtt")
@Description(value = "MQTT Execution : sends MQTT packets")
public class MQTTExecution extends AbstractOperation implements IOperation, IDefinitionInjection {

	/** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

	/** The Constant DEFAULT_PATTERN. */
	private static final String DEFAULT_PATTERN = "${device-id}|${device-state}";

	/** The broker pattern. */
	@Attribute(name = "pattern", required = false)
	@Description("The pattern of the string that will be sent to the topic. Ex. ${device-id}|${device-state}")
    @BeanField
	private String brokerPattern = DEFAULT_PATTERN;

	/** The broker topic. */
	@Attribute(name = "broker-topic", required = true)
    @BeanField
	private String brokerTopic;
	
	/** The broker alias. */
	@Attribute(name = "destination", required = false)
	@Description("Optionnal destination, if several brokers are defined. If alias is not set, the first defined broker is taken.")
    @BeanField
	private String brokerAlias;
	
	/** The client. */
	private MqttClient client;

	/** The wrapper. */
	private IMQTTWrapper wrapper = new MQTTWrapper();

	/** The definitions. */
	private Collection<IDefinition> definitions;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tensin.mirror4j.model.operations.IOperation#execute(org.tensin.mirror4j
	 * .model.events.IEvent)
	 */
	@Override
	public void execute(final IEvent event) throws Mirror4JException {
		LOGGER.info("Now publishing message on topic [" + getBrokerTopic() + "]");
		final MqttMessage message = new MqttMessage();
		message.setPayload(wrapper.wrap(getBrokerPattern(), event).getBytes());
		try {
			client.publish(getBrokerTopic(), message);
		} catch (MqttPersistenceException e) {
			throw new Mirror4JException("Can't publish message [" + message.toString() + "] on topic [" + getBrokerTopic() + "]", e);
		} catch (MqttException e) {
			throw new Mirror4JException("Can't publish message [" + message.toString() + "] on topic [" + getBrokerTopic() + "]", e);
		}
	}

	public String getBrokerAlias() {
		return brokerAlias;
	}
	
	/**
	 * Gets the broker pattern.
	 *
	 * @return the broker pattern
	 */
	public String getBrokerPattern() {
		return brokerPattern;
	}

	/**
	 * Gets the broker topic.
	 *
	 * @return the broker topic
	 */
	public String getBrokerTopic() {
		return brokerTopic;
	}
	
	/**
	 * Gets the MQTT broker.
	 *
	 * @return the MQTT broker
	 * @throws Mirror4JException the mirror4 j exception
	 */
	public MQTTBrokerDefinition getMQTTBroker() throws Mirror4JException {
		for (final IDefinition definition : definitions) {
			if (definition instanceof MQTTBrokerDefinition) {
				final MQTTBrokerDefinition mqttBrokerDefinition = (MQTTBrokerDefinition)definition;
				if (StringUtils.equalsIgnoreCase(brokerAlias, mqttBrokerDefinition.getName())) {
					return mqttBrokerDefinition;
				}
			}
		}
		throw new Mirror4JException("Can't find any broker definition with name [" + brokerAlias + "] (is the corresponding <mqtt /> definition defined under <definitions /> ?)");
	}


	/**
	 * Inits the client mqtt.
	 *
	 * @throws Mirror4JException
	 *             the mirror4 j exception
	 */
	private void initClientMQTT() throws Mirror4JException {
		try {
			final String clientId = MqttClient.generateClientId();
			final MqttConnectOptions options = new MqttConnectOptions();
			final StringBuilder sb = new StringBuilder();
			if (getMQTTBroker().isBrokerAuth()) {
			    final String hiddenPassword = StringUtils.repeat("*", getMQTTBroker().getBrokerPassword() == null ? 0 : getMQTTBroker().getBrokerPassword().length());
				sb.append(", connection will be authentificated with username [").append(getMQTTBroker().getBrokerUsername()).append("], password [").append(hiddenPassword).append("]");
				options.setUserName(getMQTTBroker().getBrokerUsername());
				options.setPassword(getMQTTBroker().getBrokerPassword().toCharArray());
			}  else {
				sb.append(", without authentification");
			}
			LOGGER.info("Now starting MQTT client on broker url [" + getMQTTBroker().getBrokerUrl() + "], client ID is [" + clientId + "]" + sb.toString());
			
			client = new MqttClient(getMQTTBroker().getBrokerUrl(), clientId);
			client.connect(options);
		} catch (MqttException e) {
			throw new Mirror4JException("Can't start MQTT client on broker url [" + getMQTTBroker().getBrokerUrl() + "]", e);
		}
	}

	/**
	 * Log available pattern for events.
	 */
	protected void logAvailablePatternForEvents() {
		final StringBuilder sb = new StringBuilder("For MQTT output patterns");
		final EventDevice e = new EventDevice(State.UNKNOWN);
		final EventZtamp z = new EventZtamp(State.UNKNOWN, "");
		String sep = "";
		sb.append("\n - keys allowed for devices : ");
		for (final String s: e.getValues().keySet()) {
			sb.append(sep).append("${").append(s).append("}");
			sep = ", ";
		}
		sep = "";
		sb.append("\n - keys allowed for ztamps : ");
		for (final String s: z.getValues().keySet()) {
			sb.append(sep).append("${").append(s).append("}");
			sep = ", ";
		}
		sb.append("\nExample : \n  <pattern>ID=${ztamp-id};TIMESTAMP=${timestamp};STATE=${ztamp-state}</pattern>");
		LOGGER.info(sb.toString());
	}
	
	public void setBrokerAlias(String brokerAlias) {
		this.brokerAlias = brokerAlias;
	}

	/**
	 * Sets the broker pattern.
	 *
	 * @param brokerPattern the new broker pattern
	 */
	public void setBrokerPattern(String brokerPattern) {
		this.brokerPattern = brokerPattern;
	}

	/**
	 * Sets the broker topic.
	 *
	 * @param brokerTopic the new broker topic
	 */
	public void setBrokerTopic(String brokerTopic) {
		this.brokerTopic = brokerTopic;
	}

	/* (non-Javadoc)
	 * @see org.tensin.mirror4j.model.configuration.IDefinitionInjection#setDefinitions(java.util.Collection)
	 */
	@Override
	public void setDefinitions(Collection<IDefinition> definitions) {
		this.definitions = definitions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tensin.mirror4j.model.operations.IOperation#start()
	 */
	@Override
	public void start() throws Mirror4JException {
		logAvailablePatternForEvents();
		initClientMQTT();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tensin.mirror4j.model.operations.IOperation#stop()
	 */
	@Override
	public void stop() throws Mirror4JException {
		stopClientMQTT();
	}

	/**
	 * Stop client mqtt.
	 *
	 * @throws Mirror4JException
	 *             the mirror4 j exception
	 */
	private void stopClientMQTT() throws Mirror4JException {
		if (client != null) {
			LOGGER.info("Now stoping MQTT client");
			try {
				client.disconnect();
			} catch (MqttException e) {
				throw new Mirror4JException("Can't stop MQTT client", e);
			}
		}
	}
}