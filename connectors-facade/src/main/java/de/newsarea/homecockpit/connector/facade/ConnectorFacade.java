package de.newsarea.homecockpit.connector.facade;

import de.newsarea.homecockpit.connector.facade.event.InboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;

public interface ConnectorFacade {

    void registerEventHandler(String element, String component, String state, InboundEventHandler eventHandler);

    void registerEventHandler(String element, String component, String state, ConnectorEventHandler eventHandler);

    void addEventListener(OutboundEventListener outboundEventListener);

    boolean postEvent(InboundEvent inboundEvent);

}
