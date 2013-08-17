package de.newsarea.homecockpit.connector.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;

public interface ConnectorEventHandler {

    void addValueChangedEventListener(ValueChangedEventListener<Object> valueChangedEventListener);

}
