package de.newsarea.homecockpit.connector;

import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractConnector<E extends ConnectorEvent> implements Connector<E> {

    private static Logger log = LoggerFactory.getLogger(AbstractConnector.class);

    private EventListenerSupport<ValueChangedEventListener> eventListeners;
    private LinkedBlockingQueue<String> queue;

    protected GeneralConnector generalConnector;

    protected AbstractConnector(GeneralConnector generalConnector) {
        this.eventListeners = EventListenerSupport.create(ValueChangedEventListener.class);
        this.queue = new LinkedBlockingQueue<>();
        // ~
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        handleValueReceived(queue.take());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });
        // ~
        this.generalConnector = generalConnector;
        this.generalConnector.addEventListener(new ValueChangedEventListener<String>() {
            @Override
            public void valueChanged(String s) {
                try {
                    queue.offer(s);
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
