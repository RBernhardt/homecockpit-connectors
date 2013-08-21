package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;

public class FSUIPCOffset {

    private int value;

    public int getValue() {
        return value;
    }

    public FSUIPCOffset(int value) {
        this.value = value;
    }

    public static FSUIPCOffset fromString(String value) {
        int offset = Integer.parseInt(value.replaceAll("0x", ""), 16);
        return new FSUIPCOffset(offset);
    }

    public String toHexString() {
        return ByteArray.create(value, 4).toHexString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FSUIPCOffset)) return false;
        FSUIPCOffset that = (FSUIPCOffset) o;
        if (value != that.value) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "FSUIPCOffset{" +
                "value=" + value +
                '}';
    }

}
