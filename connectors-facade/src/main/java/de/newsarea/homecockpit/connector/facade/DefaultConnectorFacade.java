package de.newsarea.homecockpit.connector.facade;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.facade.event.InboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultConnectorFacade implements ConnectorFacade {

    private static Logger log = LoggerFactory.getLogger(DefaultConnectorFacade.class);

    private Map<String, InboundEventHandler> inboundEventHandlers = new HashMap<>();
    private List<OutboundEventListener> outboundEventListeners = new ArrayList<>();

    @Override
    public void registerEventHandler(String element, String component, String state, InboundEventHandler eventHandler) {
        String eventSignature = element + "_" + component + "_" + state;
        inboundEventHandlers.put(eventSignature, eventHandler);
    }

    @Override
    public void registerEventHandler(final String element, final String component, final String state, ConnectorEventHandler eventHandler) {
        eventHandler.addConnectorEventHandlerListener(new ConnectorEventHandlerListener<Object>() {
            @Override
            public void valueChanged(Object value) {
                fireEvent(new OutboundEvent(element, component, state, value));
            }
            @Override
            public void stateChanged(String state) {
                fireEvent(new OutboundEvent(element, component, state, null));
            }
        });
    }

    @Override
    public void addEventListener(OutboundEventListener outboundEventListener) {
        outboundEventListeners.add(outboundEventListener);
    }

    private void fireEvent(OutboundEvent outboundEvent) {
        for(OutboundEventListener outboundEventListener : outboundEventListeners) {
            outboundEventListener.outboundEvent(outboundEvent);
        }
    }

    @Override
    public boolean postEvent(InboundEvent inboundEvent) {
        String eventSignature = inboundEvent.getEventSignature();
        if(!inboundEventHandlers.containsKey(eventSignature)) {
            return false;
        }
        inboundEventHandlers.get(eventSignature).handleInboundEvent(inboundEvent.getValue());
        return true;
    }

}
