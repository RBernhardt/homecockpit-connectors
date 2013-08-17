package de.newsarea.homecockpit.connector.facade.registration.eventhandler;

import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;

import java.util.Map;

public class ConnectorTestEventHandler extends AbstractEventHandler implements ConnectorEventHandler {

    public ConnectorTestEventHandler(Object connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    @Override
    public void addValueChangedEventListener(ValueChangedEventListener<Object> valueChangedEventListener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
