package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain;

public class FSUIPCOffset {

    private int value;

    public int getValue() {
        return value;
    }

    public FSUIPCOffset(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FSUIPCOffset{" +
                "value=" + value +
                '}';
    }

}
