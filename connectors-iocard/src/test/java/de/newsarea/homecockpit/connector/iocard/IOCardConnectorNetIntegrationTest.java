package de.newsarea.homecockpit.connector.iocard;

import de.newsarea.homecockpit.connector.GeneralConnector;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.iocard.event.IOCardConnectorEvent;
import de.newsarea.homecockpit.connector.net.NetGeneralConnector;
import de.newsarea.homecockpit.connector.usb.USBGeneralConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class IOCardConnectorNetIntegrationTest {

    private static Logger log = LoggerFactory.getLogger(IOCardConnectorNetIntegrationTest.class);

    private GeneralConnector<String> generalConnector;

    @BeforeMethod
    public void setUp() throws Exception {
        generalConnector = new NetGeneralConnector("thinkcentre02", 4010);
    }

    @Test(enabled = false)
    public void shouldOpenAndClose() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.addEventListener(new ValueChangedEventListener<IOCardConnectorEvent>() {
            @Override
            public void valueChanged(IOCardConnectorEvent ioCardConnectorEvent) {
                log.info(ioCardConnectorEvent.toString());
            }
        });
        ioCardConnector.open();
        Thread.sleep(1000);
        ioCardConnector.close();
    }

}
