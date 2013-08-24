package de.newsarea.homecockpit.connector;

import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;

import java.io.IOException;
import java.net.ConnectException;

public interface GeneralConnector<E> {

    void open() throws ConnectException;

    void addEventListener(ValueChangedEventListener<E> eventListener);

    void write(E data) throws IOException;

    void close();

}
