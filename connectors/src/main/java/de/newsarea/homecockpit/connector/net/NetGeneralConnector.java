package de.newsarea.homecockpit.connector.net;

import de.newsarea.homecockpit.connector.AbstractGeneralConnector;
import de.newsarea.homecockpit.connector.event.ValueEventListener;
import de.newsarea.homecockpit.connector.net.client.NetClient;
import de.newsarea.homecockpit.connector.net.client.kryonet.KryoNetClient;

import java.io.IOException;
import java.net.ConnectException;

public class NetGeneralConnector extends AbstractGeneralConnector<String> {

    private NetClient netClient;

    public NetGeneralConnector(NetClient netClient) {
        this.netClient = netClient;
        this.netClient.addEventListener(new ValueEventListener() {
            @Override
            public void valueReceived(String s) {
                fireEvent(s);
            }
        });
    }

    public NetGeneralConnector(String server, int port) {
        this(new KryoNetClient(server, port));
    }

    @Override
    public void open() throws ConnectException {
        try {
            netClient.open();
        } catch (Exception e) {
            throw new ConnectException(e.getMessage());
        }
    }

    @Override
    public void write(String data) throws IOException {
        netClient.write(data);
    }

    @Override
    public void close() {
        netClient.close();
    }

}
