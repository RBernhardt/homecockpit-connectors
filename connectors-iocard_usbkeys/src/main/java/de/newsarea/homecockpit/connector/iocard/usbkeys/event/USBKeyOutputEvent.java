package de.newsarea.homecockpit.connector.iocard.usbkeys.event;

import de.newsarea.homecockpit.connector.event.ConnectorEvent;

public class USBKeyOutputEvent implements ConnectorEvent {

    private int id;

    public int getId() {
        return id;
    }

    public USBKeyOutputEvent(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "USBKeyOutputEvent{" +
                "id=" + id +
                "}";
    }

}
