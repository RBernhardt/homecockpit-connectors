package de.newsarea.homecockpit.connector;

import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;

public abstract class AbstractGeneralConnector<E> implements GeneralConnector<E> {

    private EventListenerSupport<ValueChangedEventListener> eventListeners;

    public AbstractGeneralConnector() {
        eventListeners = EventListenerSupport.create(ValueChangedEventListener.class);
    }

    public void addEventListener(ValueChangedEventListener<E> eventListener) {
        eventListeners.addListener(eventListener);
    }

    protected void fireEvent(E data) {
        eventListeners.fire().valueChanged(data);
    }

}
