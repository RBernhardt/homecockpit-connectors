package de.newsarea.homecockpit.connector.net.client.kryonet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.*;

public class KryoNetClientIntegrationTest {

    private Server server;
    private List<String> receivedValues = new ArrayList<String>();

    @BeforeClass
    public void beforeClass() throws IOException {
        server = new Server();
        server.start();
        server.bind(8000);
        server.addListener(new Listener() {
            @Override
            public void received (Connection connection, Object object) {
                receivedValues.add((String)object);
            }
        });
    }

    @AfterClass
    public void afterClass() {
        server.close();
    }

    @Test
    public void shouldReceiveWriteValue() throws Exception {
        KryoNetClient kryoNetClient = new KryoNetClient("localhost", 8000);
        kryoNetClient.open();
        kryoNetClient.write("testValue");
        Thread.sleep(100);
        // ~
        assertEquals(1, receivedValues.size());
        assertEquals("testValue", receivedValues.get(0));
        // ~
        kryoNetClient.close();
    }

    @Test
    public void shouldReturnFalseOnIsConnected() {
        KryoNetClient kryoNetClient = new KryoNetClient("localhost", 8000);
        assertFalse(kryoNetClient.isConnected());
    }

    @Test
    public void shouldReturnTrueOnIsConnected() throws ConnectException {
        KryoNetClient kryoNetClient = new KryoNetClient("localhost", 8000);
        kryoNetClient.open();
        assertTrue(kryoNetClient.isConnected());
        kryoNetClient.close();
    }

}
