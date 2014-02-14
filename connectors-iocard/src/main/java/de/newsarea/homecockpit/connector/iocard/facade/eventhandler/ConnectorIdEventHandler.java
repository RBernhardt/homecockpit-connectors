package de.newsarea.homecockpit.connector.iocard.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.iocard.IOCardConnector;
import de.newsarea.homecockpit.connector.iocard.domain.IOCardIdMap;
import de.newsarea.homecockpit.connector.iocard.event.IOCardConnectorEvent;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.AbstractMap;
import java.util.Map;

public class ConnectorIdEventHandler extends AbstractEventHandler<IOCardConnector> implements ConnectorEventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ConnectorIdEventHandler.class);

    private EventListenerSupport<ConnectorEventHandlerListener> eventListeners;

    private IOCardIdMap ids;
    private String fallbackState;

    public IOCardIdMap getIds() {
        if(ids == null) {
            ids = IOCardIdMap.fromString(getParameterValue("ids"));
        }
        return ids;
    }

    public String getFallbackState() {
        if(getParameters().containsKey("fallbackState") && fallbackState == null) {
            fallbackState = getParameterValue("fallbackState");
        }
        return fallbackState;
    }

    /* */

    public ConnectorIdEventHandler(IOCardConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
        // ~
        this.eventListeners = EventListenerSupport.create(ConnectorEventHandlerListener.class);
    }

    public ConnectorIdEventHandler(IOCardConnector connector, IOCardIdMap ioCardIdMap, String fallbackState) {
        this(connector, toParameters(
            new AbstractMap.SimpleEntry<>("ids", ioCardIdMap.toString()),
            new AbstractMap.SimpleEntry<>("fallbackState", fallbackState)
        ));
    }

    @Override
    public void addConnectorEventHandlerListener(ConnectorEventHandlerListener<Object> connectorEventHandlerListener) {
        if(eventListeners.getListeners().length == 0) {
            getConnector().addEventListener(new ValueChangedEventListener<IOCardConnectorEvent>() {
                @Override
                public void valueChanged(IOCardConnectorEvent event) {
                    handleConnectorEvent(event.getMap());
                }
            });
        }
        eventListeners.addListener(connectorEventHandlerListener);
    }

    private void handleConnectorEvent(IOCardIdMap ioCardIdMap) {
        if(!couldHandleConnectorEvent(ioCardIdMap)) {
            return;
        }
        // ~
        for(Map.Entry<Integer, Boolean> idEntry : getIds().entrySet()) {
            if(!contains(ioCardIdMap, idEntry)) {
                if(getFallbackState() != null) {
                    eventListeners.fire().stateChanged(getFallbackState());
                }
                return;
            }
        }
        eventListeners.fire().valueChanged(null);
    }

    /* HELPER */

    private boolean couldHandleConnectorEvent(IOCardIdMap ioCardIdMap) {
        for(int id : getIds().keySet()) {
            if(!ioCardIdMap.containsKey(id)) {
                return false;
            }
        }
        return true;
    }

    private boolean contains(IOCardIdMap ioCardIdMap, Map.Entry<Integer, Boolean> idItem) {
        for(Map.Entry<Integer, Boolean> entry : ioCardIdMap.entrySet()) {
            if(entry.getKey().equals(idItem.getKey()) && entry.getValue().equals(idItem.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void queueLastEvent() {
        handleConnectorEvent(getConnector().getRememberedInputs());
    }

}
