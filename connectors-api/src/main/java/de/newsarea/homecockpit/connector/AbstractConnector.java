package de.newsarea.homecockpit.connector;

import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

public abstract class AbstractConnector<E extends ConnectorEvent> implements Connector<E> {

    private static Logger log = LoggerFactory.getLogger(AbstractConnector.class);

    private EventListenerSupport<ValueChangedEventListener> eventListeners;
    protected GeneralConnector generalConnector;

    protected AbstractConnector(GeneralConnector generalConnector) {
        this.eventListeners = EventListenerSupport.create(ValueChangedEventListener.class);
        // ~
        this.generalConnector = generalConnector;
        this.generalConnector.addEventListener(new ValueChangedEventListener<String>() {
            @Override
            public void valueChanged(String s) {
                try {
                    handleValueReceived(s);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    protected abstract void handleValueReceived(String s) throws Exception;

    @Override
    public void open() throws ConnectException {
        generalConnector.open();
    }

    public void addEventListener(ValueChangedEventListener<E> eventListener) {
        eventListeners.addListener(eventListener);
    }

    protected void fireEvent(E data) {
        eventListeners.fire().valueChanged(data);
    }


    @Override
    public void close() {
        generalConnector.close();
    }

}
