package de.newsarea.homecockpit.connector.facade;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.facade.event.Event;
import de.newsarea.homecockpit.connector.facade.event.InboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class DefaultConnectorFacade implements ConnectorFacade {

    private static final Logger log = LoggerFactory.getLogger(DefaultConnectorFacade.class);

    private Map<Event, InboundEventHandler> inboundEventHandlers = new HashMap<>();
    private List<ConnectorEventHandler> connectorEventHandlers = new ArrayList<>();
    private List<Event> outboundEvents = new ArrayList<>();

    private List<OutboundEventListener> outboundEventListeners = new ArrayList<>();

    @Override
    public void registerEventHandler(String element, String component, String state, InboundEventHandler eventHandler) {
        Event event = new Event(element, component, state);
        inboundEventHandlers.put(event, eventHandler);
    }

    @Override
    public void registerEventHandler(final String element, final String component, final String state, ConnectorEventHandler eventHandler) {
        outboundEvents.add(new Event(element, component, state));
        // ~
        connectorEventHandlers.add(eventHandler);
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
    public boolean postEvent(InboundEvent inboundEvent) throws IOException {
        Event event = new Event(inboundEvent.getElement(), inboundEvent.getComponent(), inboundEvent.getState());
        if(!inboundEventHandlers.containsKey(event)) {
            return false;
        }
        inboundEventHandlers.get(event).handleInboundEvent(inboundEvent.getValue());
        return true;
    }

    @Override
    public void queueLastEvents() {
        for(ConnectorEventHandler connectorEventHandler : connectorEventHandlers) {
            connectorEventHandler.queueLastEvent();
        }
    }

    @Override
    public Collection<Event> listRegisteredInboundEvents() {
        return inboundEventHandlers.keySet();
    }

    @Override
    public Collection<Event> listRegisteredOutboundEvents() {
        return outboundEvents;
    }

}
