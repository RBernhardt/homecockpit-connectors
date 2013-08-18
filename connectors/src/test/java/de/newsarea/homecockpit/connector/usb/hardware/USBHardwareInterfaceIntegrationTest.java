package de.newsarea.homecockpit.connector.usb.hardware;

import org.testng.annotations.Test;

public class USBHardwareInterfaceIntegrationTest {

    @Test(enabled = false)
    public void shouldOpenAndClose() throws Exception {
        USBHardwareInterface usbHardwareInterface = new USBHardwareInterface(0, 0);
        usbHardwareInterface.open();
        Thread.sleep(5000);
        usbHardwareInterface.close();
    }
}
