package de.newsarea.homecockpit.connector.iocard.usbkeys.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.iocard.usbkeys.USBKeysConnector;
import de.newsarea.homecockpit.connector.iocard.usbkeys.event.USBKeyOutputEvent;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConnectorKeyInputEventHandlerTest {

    @Test
    public void shouldRegisterListener() throws Exception {
        USBKeysConnector usbKeysConnector = mock(USBKeysConnector.class);
        ConnectorKeyInputEventHandler connectorKeyInputEventHandler = new ConnectorKeyInputEventHandler(usbKeysConnector, 60);
        // when
        connectorKeyInputEventHandler.addConnectorEventHandlerListener(new ConnectorEventHandlerListener<Object>() {
            @Override
            public void valueChanged(Object o) { /* do nothing */ }
            @Override
            public void stateChanged(String s) { /* do nothing */ }
        });
        // then
        verify(usbKeysConnector).addEventListener(any(ValueChangedEventListener.class));
    }

    @Test
    public void shouldFireEvent() throws Exception {
        ConnectorEventHandlerListener connectorEventHandlerListener = mock(ConnectorEventHandlerListener.class);
        ConnectorKeyInputEventHandler connectorKeyInputEventHandler = new ConnectorKeyInputEventHandler(mock(USBKeysConnector.class), 60);
        connectorKeyInputEventHandler.addConnectorEventHandlerListener(connectorEventHandlerListener);
        // when
        connectorKeyInputEventHandler.handleConnectorEvent(new USBKeyOutputEvent(60));
        // then
        verify(connectorEventHandlerListener).valueChanged(null);
    }
}
