package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;

public class ConnectorHighBitEventHandler extends AbstractFSUIPCEventHandler implements ConnectorEventHandler {
	
	private static Logger log = LoggerFactory.getLogger(ConnectorHighBitEventHandler.class);

    private EventListenerSupport<ConnectorEventHandlerListener> eventListeners;

    private Byte bitIdx;
    private Boolean isHighBit;

    public Byte getBitIdx() {
        if(bitIdx == null) {
            bitIdx = Byte.parseByte(getParameterValue("bitIdx"));
        }
        return bitIdx;
    }

    public Boolean getIsHighBit() {
        if(isHighBit == null) {
            isHighBit = Boolean.parseBoolean(getParameterValue("isHighBit"));
        }
        return isHighBit;
    }

    public ConnectorHighBitEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
        // ~
        eventListeners = EventListenerSupport.create(ConnectorEventHandlerListener.class);
    }

    public ConnectorHighBitEventHandler(FSUIPCConnector connector, FSUIPCOffset offset, Integer size, Byte bitIdx, Boolean isHighBit) {
        this(connector,
                toParameters(
                        new AbstractMap.SimpleEntry<>("offset", offset.toHexString()),
                        new AbstractMap.SimpleEntry<>("size", String.valueOf(size)),
                        new AbstractMap.SimpleEntry<>("bitIdx", String.valueOf(bitIdx)),
                        new AbstractMap.SimpleEntry<>("isHighBit", String.valueOf(isHighBit))

                )
        );
    }

    private void handleConnectorEvent(FSUIPCConnectorEvent connectorEvent) {
        if(getOffset().getValue() != connectorEvent.getOffset()) { return; }
        if(getSize() != connectorEvent.getSize()) { return; }
        // ~
        try {
            boolean tObjIsHighBit = connectorEvent.getValue().isHighBit(getBitIdx());
            log.debug("check is highbit - {} : {} : {}", connectorEvent.getValue().toHexString(), getBitIdx(), getIsHighBit());
            if (tObjIsHighBit != getIsHighBit()) { return; }
            eventListeners.fire().valueChanged(null);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
	}

    @Override
    public void addConnectorEventHandlerListener(ConnectorEventHandlerListener<Object> connectorEventHandlerListener) {
        if(eventListeners.getListeners().length == 0) {
            try {
                getConnector().monitor(new OffsetIdent(getOffset().getValue(), getSize()));
                getConnector().addEventListener(new ValueChangedEventListener<FSUIPCConnectorEvent>() {
                    @Override
                    public void valueChanged(FSUIPCConnectorEvent event) {
                        handleConnectorEvent(event);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        eventListeners.addListener(connectorEventHandlerListener);
    }

}
