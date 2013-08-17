package de.newsarea.homecockpit.connector;

import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;

import java.net.ConnectException;

public interface Connector<E extends ConnectorEvent> {

    void open() throws ConnectException;

	void addEventListener(ValueChangedEventListener<E> eventListener);

    void close();
	
}
