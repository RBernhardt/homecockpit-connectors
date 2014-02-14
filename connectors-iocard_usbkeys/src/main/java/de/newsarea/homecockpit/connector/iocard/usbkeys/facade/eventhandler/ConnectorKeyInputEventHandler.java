package de.newsarea.homecockpit.connector.iocard.usbkeys.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.iocard.usbkeys.USBKeysConnector;
import de.newsarea.homecockpit.connector.iocard.usbkeys.event.USBKeyOutputEvent;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;

public class ConnectorKeyInputEventHandler extends AbstractEventHandler<USBKeysConnector> implements ConnectorEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ConnectorKeyInputEventHandler.class);

    private EventListenerSupport<ConnectorEventHandlerListener> eventListeners;

    private Integer key;

    public Integer getKey() {
        if(key == null) {
            key = Integer.valueOf(getParameterValue("key"));
        }
        return key;
    }

    public ConnectorKeyInputEventHandler(USBKeysConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
        // ~
        this.eventListeners = EventListenerSupport.create(ConnectorEventHandlerListener.class);
    }

    public ConnectorKeyInputEventHandler(USBKeysConnector connector, Integer key) {
        this(connector, toParameters(
            new AbstractMap.SimpleEntry<>("key", String.valueOf(key))
        ));
    }

    public void handleConnectorEvent(USBKeyOutputEvent usbKeyOutputEvent) {
        if(getKey() != usbKeyOutputEvent.getId()) {
            return;
        }
        eventListeners.fire().valueChanged(null);
    }

    @Override
    public void addConnectorEventHandlerListener(ConnectorEventHandlerListener<Object> connectorEventHandlerListener) {
        if(eventListeners.getListeners().length == 0) {
            getConnector().addEventListener(new ValueChangedEventListener<USBKeyOutputEvent>() {
                @Override
                public void valueChanged(USBKeyOutputEvent event) {
                    handleConnectorEvent(event);
                }
            });
        }
        eventListeners.addListener(connectorEventHandlerListener);
    }

    @Override
    public void queueLastEvent() {
        /* do nothing */
    }
}
