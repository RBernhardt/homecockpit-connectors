package de.newsarea.homecockpit.connector.net.client;

import de.newsarea.homecockpit.connector.event.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNetClient {

    protected String server;
    protected int port;

    private List<ValueEventListener> valueEventListeners = new ArrayList<>();

    public String getServer() {
        return this.server;
    }

    public int getPort() {
        return this.port;
    }

    public AbstractNetClient(String server, int port) {
        this.server = server;
        this.port = port;
    }

    /* */

    public void addEventListener(ValueEventListener valueEventListener) {
        valueEventListeners.add(valueEventListener);
    }

    protected void fireValueReceivedEvent(String value) {
        if(valueEventListeners.size() == 0) { return; }
        for(ValueEventListener valueEventListener : valueEventListeners) {
            valueEventListener.valueReceived(value);
        }
    }

}
