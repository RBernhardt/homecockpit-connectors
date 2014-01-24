package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class InboundToggleBitEventHandler extends AbstractFSUIPCEventHandler implements InboundEventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(InboundToggleBitEventHandler.class);

    private Byte bitIdx;

    public Byte getBitIdx() {
        if(bitIdx == null) {
            bitIdx = Byte.parseByte(getParameterValue("bitIdx"));
        }
        return bitIdx;
    }

    public InboundToggleBitEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    public InboundToggleBitEventHandler(FSUIPCConnector connector, FSUIPCOffset offset, int size, Byte bitIdx) {
        this(connector,
                toParameters(
                        new AbstractMap.SimpleEntry<>("offset", offset.toHexString()),
                        new AbstractMap.SimpleEntry<>("size", String.valueOf(size)),
                        new AbstractMap.SimpleEntry<>("bitIdx", String.valueOf(bitIdx))
                )
        );
    }

    @Override
    public void handleInboundEvent(Object value) throws IOException {
		log.debug("handleOutput - {}", value);
        getConnector().toggleBit(getOffset().getValue(), getSize(), getBitIdx());
    }

}
