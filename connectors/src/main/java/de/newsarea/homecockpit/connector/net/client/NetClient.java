package de.newsarea.homecockpit.connector.net.client;

import de.newsarea.homecockpit.connector.event.ValueEventListener;

import java.io.IOException;
import java.net.ConnectException;

public interface NetClient {

    String getServer();
    int getPort();

    boolean isConnected();
    void open() throws ConnectException;
    void write(String value) throws IOException;
    void addEventListener(ValueEventListener valueEventListener);
    void close();

}
