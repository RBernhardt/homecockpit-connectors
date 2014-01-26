package de.newsarea.homecockpit.connector.fsuipc;

import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class FSUIPCHttpConnectorIntegrationTest {

    private FSUIPCHttpConnector fsuipcHttpConnector;

    @BeforeMethod
    public void setUp() throws Exception {
        fsuipcHttpConnector = new FSUIPCHttpConnector("simulator", 8080, 8081);
        fsuipcHttpConnector.open();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        fsuipcHttpConnector.close();
    }

    @Test(enabled = false)
    public void shouldReadLatitudeValue() throws Exception {
        OffsetItem offsetItem = fsuipcHttpConnector.read(new OffsetIdent(0x0568, 8));
        assertNotNull(null, offsetItem);
        assertEquals(8, offsetItem.getValue().getSize());
    }

    @Test(enabled = false)
    public void shouldMonitorOffset() throws Exception {
        fsuipcHttpConnector.monitor(new OffsetIdent(0x0568, 8));
    }

    @Test(enabled = false)
    public void shouldWriteData() throws Exception {
        fsuipcHttpConnector.write(new OffsetItem(0x04F4, 2, ByteArray.create("1", 2)), 200);
        fsuipcHttpConnector.write(new OffsetItem(0x04F4, 2, ByteArray.create("2", 2)), 200);
        fsuipcHttpConnector.write(new OffsetItem(0x04F4, 2, ByteArray.create("3", 2)), 200);
    }

    @Test(enabled = false)
    public void shouldReveiveData() throws InterruptedException, IOException {
        fsuipcHttpConnector.addEventListener(new ValueChangedEventListener<FSUIPCConnectorEvent>() {
            @Override
            public void valueChanged(FSUIPCConnectorEvent event) {
                System.out.println(event);
            }
        });
        fsuipcHttpConnector.monitor(new OffsetIdent(0x0568, 8));
        Thread.sleep(1000);
    }

}
