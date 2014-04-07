package de.newsarea.homecockpit.connector.fsuipc;

import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class FSUIPCNativeConnector implements FSUIPCConnector {

    private static Logger log = LoggerFactory.getLogger(FSUIPCNativeConnector.class);

    private EventListenerSupport<ValueChangedEventListener> eventListeners;
    private FSUIPCInterface fsuipcInterface;

    public FSUIPCNativeConnector(FSUIPCInterface fsuipcInterface) {
        this.eventListeners = EventListenerSupport.create(ValueChangedEventListener.class);
        // ~
        this.fsuipcInterface = fsuipcInterface;
        this.fsuipcInterface.addEventListener(new OffsetEventListener() {
            @Override
            public void valueChanged(OffsetItem offsetItem) {
                try {
                    eventListeners.fire().valueChanged(FSUIPCConnectorEvent.from(offsetItem));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void open() throws ConnectException {
        fsuipcInterface.open();
    }

    @Override
    public boolean isConnectionEstablished() {
        return fsuipcInterface.isConnectionEstablished();
    }

    @Override
    public void monitor(OffsetIdent offsetIdent) {
        fsuipcInterface.monitor(offsetIdent);
    }

    @Override
    public void write(OffsetItem offsetItem) throws IOException {
        fsuipcInterface.write(offsetItem);
    }

    @Override
    public OffsetItem read(OffsetIdent offsetIdent) throws IOException {
        return fsuipcInterface.read(offsetIdent);
    }

    public void addEventListener(ValueChangedEventListener<FSUIPCConnectorEvent> eventListener) {
        eventListeners.addListener(eventListener);
    }

    @Override
    public void close() {
        fsuipcInterface.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FSUIPCNativeConnector)) return false;

        FSUIPCNativeConnector that = (FSUIPCNativeConnector) o;

        if (!eventListeners.equals(that.eventListeners)) return false;
        if (!fsuipcInterface.equals(that.fsuipcInterface)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventListeners.hashCode();
        result = 31 * result + fsuipcInterface.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FSUIPCNativeConnector{" +
                "eventListeners=" + eventListeners +
                ", fsuipcInterface=" + fsuipcInterface +
                "}";
    }

}
