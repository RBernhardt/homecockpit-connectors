package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain;

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

    @Override
    public String toString() {
        return "FSUIPCOffset{" +
                "value=" + value +
                '}';
    }

}
