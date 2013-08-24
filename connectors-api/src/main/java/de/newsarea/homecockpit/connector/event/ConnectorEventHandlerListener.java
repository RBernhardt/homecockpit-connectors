package de.newsarea.homecockpit.connector.event;

public interface ConnectorEventHandlerListener<E> {

    void valueChanged(E event);
    void stateChanged(String state);

}
