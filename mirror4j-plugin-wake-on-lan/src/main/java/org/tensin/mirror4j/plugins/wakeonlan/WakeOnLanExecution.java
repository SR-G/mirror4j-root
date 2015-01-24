package org.tensin.mirror4j.plugins.wakeonlan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.model.Mirror4JException;
import org.tensin.mirror4j.model.events.IEvent;
import org.tensin.mirror4j.model.operations.IOperation;

/**
 * The Class WakeOnLanExecution.
 * 
 * To find your IP :
 * - on a windows box, cmd.exe and "ipconfig /all"
 * - on a linux box, "ifconfig" => "eth0      Link encap:Ethernet  HWaddr b4:99:ba:f0:89:85"
 */
@Root(name = "wake-on-lan")
@Description(value = "WakeOnLanExecution : wake a remote host")
public class WakeOnLanExecution extends AbstractWakeOnLan implements IOperation {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

	/* (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute(org.tensin.mirror4j.model.events.IEvent)
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        LOGGER.info("Awaking remote compute with wake on lan, destination mac address [" + getMacAddress() + "], broadcast IP [" + getBroadcastIp() + "], port [" + getPort() + "]");
    	final byte[] macAddressBytes = getMacBytes(getMacAddress());
    	broadcastMagicPacket(macAddressBytes, getBroadcastIp(), getPort());
    }

	/* (non-Javadoc)
	 * @see org.tensin.mirror4j.plugins.wakeonlan.AbstractWakeOnLan#getOperation()
	 */
	@Override
	protected String getOperation() {
		return "wake-on-lan";
	}
}
