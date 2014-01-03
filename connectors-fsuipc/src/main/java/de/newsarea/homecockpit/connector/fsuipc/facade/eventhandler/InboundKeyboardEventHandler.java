package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;

public class InboundKeyboardEventHandler extends AbstractFSUIPCEventHandler implements InboundEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ValueWriterEventHandler.class);

    public enum Key {
        NOTSET,
        SHIFT,
        CTRL,
        ALT
    }

    private Key key;
    private Byte value;

    private Key getKey() {
        if(key == null) {
            if(getParameters().containsKey(key)) {
                this.key = Key.valueOf(getParameterValue("key"));
            } else {
                this.key = Key.NOTSET;
            }
        }
        return key;
    }

    private Byte getValue() {
        if(value == null) {
            this.value = Byte.parseByte(getParameterValue("value"));
        }
        return value;
    }

    public InboundKeyboardEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    public InboundKeyboardEventHandler(FSUIPCConnector connector, FSUIPCOffset offset, int size, Key key, Byte value) {
        this(connector,
                toParameters(
                        new AbstractMap.SimpleEntry<>("offset", offset.toHexString()),
                        new AbstractMap.SimpleEntry<>("size", String.valueOf(size)),
                        new AbstractMap.SimpleEntry<>("key", String.valueOf(key)),
                        new AbstractMap.SimpleEntry<>("value", String.valueOf(value))
                )
        );
    }

    public void handleInboundEvent(Object value) {
        if(value != null) {
            log.warn("ignore inbound value - {}", value);
        }
        // determine key value
        byte keyValue = 0x00;
        switch(getKey()) {
            case ALT:
                keyValue = 0b00000100;
                break;
            case CTRL:
                keyValue = 0b00000010;
                break;
            case SHIFT:
                keyValue = 0b00000001;
                break;
        }
        // ~
        byte[] keyboardByteArray = new byte[] { keyValue, getValue() };
        try {
            getConnector().write(new OffsetItem(getOffset().getValue(), getSize(), keyboardByteArray));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
