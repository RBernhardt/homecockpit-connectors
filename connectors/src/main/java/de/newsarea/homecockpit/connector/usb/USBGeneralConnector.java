package de.newsarea.homecockpit.connector.usb;

import de.newsarea.homecockpit.connector.AbstractGeneralConnector;
import de.newsarea.homecockpit.connector.event.ValueEventListener;
import de.newsarea.homecockpit.connector.usb.hardware.USBHardwareInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class USBGeneralConnector extends AbstractGeneralConnector<String> {

    private static Logger log = LoggerFactory.getLogger(USBGeneralConnector.class);

    private USBHardwareInterface usbHardwareInterface;

    public USBGeneralConnector(int vendorId, int productId) {
        this.usbHardwareInterface = new USBHardwareInterface(vendorId, productId);
        this.usbHardwareInterface.addEventListener(new ValueEventListener() {
            @Override
            public void valueReceived(String value) {
                fireEvent(value);
            }
        });
    }

    @Override
    public void open() throws ConnectException {
        try {
            USBHardwareInterface.printDeviceList();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        //
        usbHardwareInterface.open();
    }

    @Override
    public void write(String data) throws IOException {
        usbHardwareInterface.send(data);
    }

    @Override
    public void close() {
        usbHardwareInterface.close();
    }

}
