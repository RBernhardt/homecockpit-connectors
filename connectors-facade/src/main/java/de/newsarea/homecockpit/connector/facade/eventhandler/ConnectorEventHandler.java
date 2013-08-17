package de.newsarea.homecockpit.connector.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;

public interface ConnectorEventHandler {

    void addConnectorEventHandlerListener(ConnectorEventHandlerListener<Object> connectorEventHandlerListener);

}
