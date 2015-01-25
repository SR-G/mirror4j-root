package org.tensin.mirror4j.plugins.wakeonlan;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.IOperation;

/**
 * The Class WakeOnLanExecution.
 * 
 * To find your IP :
 * - on a windows box, cmd.exe and "ipconfig /all"
 * - on a linux box, "ifconfig" => "eth0      Link encap:Ethernet  HWaddr b4:99:ba:f0:89:85"
 */
@Root(name = "sleep-on-lan")
@Description(value = "SleepOnLanExecution : sleeps a remote host, by sending a reversed MAC address (strictly equivalent to using the 'wakeonlan' operation with a mac address manually reversed in configuration). You needs a SleepOnLan daemon (java or go implementation) to catch those packets and react upon them.")
public class SleepOnLanExecution extends AbstractWakeOnLan implements IOperation {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

	/* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute(org.tensin.mirror4j.model.events.IEvent)
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        LOGGER.info("Sending remote compute to sleep, destination mac address [" + getMacAddress() + "], broadcast IP [" + getBroadcastIp() + "], port [" + getPort() + "]");
    	final byte[] macAddressBytes = getMacBytes(getMacAddress());
    	reverseArray(macAddressBytes);
    	broadcastMagicPacket(macAddressBytes, getBroadcastIp(), getPort());
    }

	/* (non-Javadoc)
	 * @see org.tensin.mirror4j.plugins.wakeonlan.AbstractWakeOnLan#getOperation()
	 */
	@Override
	protected String getOperation() {
		return "sleep-on-lan";
	}

	/**
	 * Reverse array.
	 *
	 * @param macBytes the mac bytes
	 * @return the byte[]
	 */
	private void reverseArray(byte[] macBytes) {
		ArrayUtils.reverse(macBytes);
	}
}