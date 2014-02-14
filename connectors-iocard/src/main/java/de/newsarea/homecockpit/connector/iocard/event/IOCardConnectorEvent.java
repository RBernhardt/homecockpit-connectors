package de.newsarea.homecockpit.connector.iocard.event;

import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.connector.iocard.domain.IOCardIdMap;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOCardConnectorEvent implements ConnectorEvent {

    private static Logger log = LoggerFactory.getLogger(IOCardConnectorEvent.class);

    public enum Type {
        INIT,
        INPUT,
        CONTINUOUS_INPUT,
        UNDEFINED
    }

    private Type type;
    private IOCardIdMap map;

    public Type getType() {
        return type;
    }

    public IOCardIdMap getMap() {
        return map;
    }

    private IOCardConnectorEvent(Type type, IOCardIdMap map) {
        this.type = type;
        this.map = map;
    }

    public static IOCardConnectorEvent fromByteArray(byte[] data) {
        Type type;
        IOCardIdMap ioCardIdMap = null;
        switch(data[0]) {
            case 0x11:
            case 0x21:
            case 0x31:
            case 0x41:
                // ex. 31fc026000ffffff
                // ex. 21ff08c000ffffff
                if(data[5] == (byte)0xFF
                  && data[6] == (byte)0xFF
                  && data[7] == (byte)0xFF) {
                    if(data[1] != (byte)0xFF) {
                        log.warn("##### 0xFX message received - {}", new String(Hex.encodeHex(data)));
                    }
                    type = Type.INPUT;
                    ioCardIdMap = IOCardIdMap.fromInputByteArrayOfInitializedCard(data);
                } else {
                    type = Type.INIT;
                    ioCardIdMap = IOCardIdMap.fromInitialByteArray(data);
                }
                break;
            case 0x10:
            case 0x20:
            case 0x30:
            case 0x40:
                type = Type.CONTINUOUS_INPUT;
                break;
            default:
                type = Type.UNDEFINED;
                break;
        }
        // ~
        return new IOCardConnectorEvent(type, ioCardIdMap);
    }


    public static IOCardConnectorEvent fromString(Type type, String data) {
        return new IOCardConnectorEvent(type, IOCardIdMap.fromString(data));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IOCardConnectorEvent)) return false;

        IOCardConnectorEvent that = (IOCardConnectorEvent) o;

        if (map != null ? !map.equals(that.map) : that.map != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (map != null ? map.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IOCardConnectorEvent{" +
                "type=" + type +
                ", map=" + map +
                "} " + super.toString();
    }

}
