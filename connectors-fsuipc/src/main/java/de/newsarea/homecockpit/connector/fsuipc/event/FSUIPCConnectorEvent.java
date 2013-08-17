package de.newsarea.homecockpit.connector.fsuipc.event;

import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

public class FSUIPCConnectorEvent implements ConnectorEvent {

    private int offset;
    private int size;
    private ByteArray value;

    public int getOffset() {
        return this.offset;
    }

    public int getSize() {
        return this.size;
    }

    public ByteArray getValue() {
        return this.value;
    }

    public FSUIPCConnectorEvent(int offset, int size, ByteArray value) {
        this.offset = offset;
        this.size = size;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append("FSUIPCConnectorEvent {");
        strBld.append(ByteArray.create(String.valueOf(offset), 2).toHexString());
        strBld.append(" : ");
        strBld.append(getSize());
        strBld.append(" : ");
        strBld.append(getValue());
        strBld.append("}");
        return strBld.toString();
    }

    public static FSUIPCConnectorEvent from(OffsetItem offsetItem) {
        return new FSUIPCConnectorEvent(offsetItem.getOffset(), offsetItem.getSize(), offsetItem.getValue());
    }

}
