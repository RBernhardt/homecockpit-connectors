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

    public void open() throws ConnectException {
        fsuipcInterface.open();
    }

    public void monitor(OffsetIdent offsetIdent) {
        fsuipcInterface.monitor(offsetIdent);
    }

    public void write(OffsetItem[] offsetItems) {
        fsuipcInterface.write(offsetItems);
    }

    public void write(OffsetItem offsetItem) {
        fsuipcInterface.write(offsetItem);
    }

    @Override
    public void toggleBit(int offset, int size, byte bitIdx) throws IOException {
        fsuipcInterface.toggleBit(offset, size, bitIdx);
    }

    public OffsetItem read(OffsetIdent offsetIdent) {
        return fsuipcInterface.read(offsetIdent);
    }

    public void addEventListener(ValueChangedEventListener<FSUIPCConnectorEvent> eventListener) {
        eventListeners.addListener(eventListener);
    }

    @Override
    public void close() {
        fsuipcInterface.close();
    }

}
