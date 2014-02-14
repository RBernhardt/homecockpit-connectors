package de.newsarea.homecockpit.connector.iocard;

import de.newsarea.homecockpit.connector.GeneralConnector;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.iocard.usbkeys.event.USBKeyOutputEvent;
import de.newsarea.homecockpit.connector.iocard.usbkeys.USBKeysConnector;
import de.newsarea.homecockpit.connector.usb.USBGeneralConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class USBKeysConnectorManualIntegrationTest {

    private static Logger log = LoggerFactory.getLogger(USBKeysConnectorManualIntegrationTest.class);

    private GeneralConnector<String> generalConnector;
    private USBKeysConnector usbKeysConnector;

    @BeforeMethod
    public void setUp() throws Exception {
        generalConnector = new USBGeneralConnector(0, 2);
        usbKeysConnector = new USBKeysConnector(generalConnector);
        usbKeysConnector.open();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        usbKeysConnector.close();
    }

    @Test(enabled = false)
    public void shouldHandleValueChanged() throws Exception {
        usbKeysConnector.addEventListener(new ValueChangedEventListener<USBKeyOutputEvent>() {
            @Override
            public void valueChanged(USBKeyOutputEvent usbKeyOutputEvent) {
                log.info("{}", usbKeyOutputEvent);
            }
        });
        Thread.sleep(60000);
    }
}
