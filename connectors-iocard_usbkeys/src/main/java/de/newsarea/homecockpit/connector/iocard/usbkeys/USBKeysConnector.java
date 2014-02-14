package de.newsarea.homecockpit.connector.iocard.usbkeys;

import de.newsarea.homecockpit.connector.AbstractConnector;
import de.newsarea.homecockpit.connector.GeneralConnector;
import de.newsarea.homecockpit.connector.iocard.usbkeys.event.USBKeyOutputEvent;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class USBKeysConnector  extends AbstractConnector<USBKeyOutputEvent> {

    private static Logger log = LoggerFactory.getLogger(USBKeysConnector.class);

    public USBKeysConnector(GeneralConnector<String> generalConnector) {
        super(generalConnector);
    }

    @Override
    public void open() throws ConnectException {
        super.open();
        // send initial message
        try {
            generalConnector.write("0400000014000000");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void handleValueReceived(String s) throws Exception {
        byte[] data = Hex.decodeHex(s.toCharArray());
        fireEvent(new USBKeyOutputEvent(data[0]));
    }

}
