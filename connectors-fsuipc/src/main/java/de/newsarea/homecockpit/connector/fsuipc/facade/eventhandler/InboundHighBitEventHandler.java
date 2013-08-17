package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCGeneralConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeoutException;

public class InboundHighBitEventHandler extends AbstractFSUIPCEventHandler implements InboundEventHandler {

	private static final Logger log = LoggerFactory.getLogger(InboundHighBitEventHandler.class);

	private Byte bitIdx;

    public Byte getBitIdx() {
        if(bitIdx == null) {
            bitIdx = Byte.parseByte(getParameterValue("bitIdx"));
        }
        return bitIdx;
    }

    public InboundHighBitEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    @Override
    public void handleInboundEvent(Object value) {
        try {
            toggleBit(getOffset().getValue(), getBitIdx());
        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
        }
    }
	
	/* HELPER */
	
	private void toggleBit(int offset, byte bitIdx) throws TimeoutException {
		/* OffsetItem offsetItem = getConnector().read(new OffsetIdent(offset, getSize()));
        ByteArray byteArray = offsetItem.getValue();
		int bidx = bitIdx / 8;
		byte nidx = (byte)(bitIdx % 8);
		byte newByteValue = value[value.length - 1 - bidx];
		// set bit high
		newByteValue |= (byte)(1 << nidx);
		value[value.length - 1 - bidx] = newByteValue;
        getConnector().write(new OffsetItem(offset, getSize(), value));
	    // wait for set to 0 from mcp
	    boolean isLowBit = false;
	    while(!isLowBit) {
	    	OffsetItem rdOffsetItem = getConnector().read(new OffsetIdent(offset, getSize()));
	    	isLowBit = !rdOffsetItem.getValue().isHighBit(bitIdx);
	    }
	    */
        throw new UnsupportedOperationException();
	}

}
