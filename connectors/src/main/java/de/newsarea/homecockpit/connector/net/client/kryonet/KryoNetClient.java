package de.newsarea.homecockpit.connector.net.client.kryonet;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.newsarea.homecockpit.connector.net.client.AbstractNetClient;
import de.newsarea.homecockpit.connector.net.client.NetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class KryoNetClient extends AbstractNetClient implements NetClient {

	private static Logger log = LoggerFactory.getLogger(KryoNetClient.class);

    private Client client;

	public KryoNetClient(String server, int port) {
		super(server, port);
        // ~
        client = new Client();
        client.start();
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if(object instanceof String) {
                    fireValueReceivedEvent((String)object);
                }
            }
        });
	}

    @Override
	public boolean isConnected() {
		return client.isConnected();
	}

    @Override
	public void open() throws ConnectException {
        log.info("open client " + server + ":" + port);
        try {
            client.connect(5000, server, port);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    @Override
	public void write(String value) throws IOException {
        client.sendTCP(value);
	}

    @Override
	public void close() {
        client.close();
	}


}
