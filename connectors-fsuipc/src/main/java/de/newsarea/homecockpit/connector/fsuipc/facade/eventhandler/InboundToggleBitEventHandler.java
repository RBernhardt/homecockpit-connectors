package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCNetConnector;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class InboundToggleBitEventHandler extends AbstractFSUIPCEventHandler implements InboundEventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(InboundToggleBitEventHandler.class);

    private Byte bitIdx;

    public Byte getBitIdx() {
        if(bitIdx == null) {
            bitIdx = Byte.parseByte(getParameterValue("bitIdx"));
        }
        return bitIdx;
    }

    public InboundToggleBitEventHandler(FSUIPCNetConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    @Override
    public void handleInboundEvent(Object value) {
		log.debug("handleOutput - {}", value);
        try {
            toggleBit(getOffset().getValue(), getSize(), getBitIdx());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
	
	/* HELPER */

    private void toggleBit(int offset, int size, byte bitIdx) throws IOException, TimeoutException {
    	OffsetItem offsetItem = getConnector().read(new OffsetIdent(offset, size));
        byte[] value = offsetItem.getValue().toByteArray();
    	int bidx = (bitIdx / 8);
		byte nidx = (byte)(bitIdx % 8);
		byte newByteValue = value[value.length - 1 - bidx];
		newByteValue ^= (1 << nidx);
		value[value.length - 1 - bidx] = newByteValue;
        getConnector().write(new OffsetItem(offset, size, value));
    }

}
