package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class ConnectorHighBitEventHandlerTest {

    private static Logger log = LoggerFactory.getLogger(ConnectorHighBitEventHandlerTest.class);

    final EventListenerSupport<ValueChangedEventListener> valueChangedEventListeners = EventListenerSupport.create(ValueChangedEventListener.class);

    private FSUIPCConnector fsuipcConnector;
    private ConnectorHighBitEventHandler connectorHighBitEventHandler;

    @BeforeMethod
    public void setUp() throws Exception {
        fsuipcConnector = mock(FSUIPCConnector.class);
        // ~
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueChangedEventListener eventListener = ((ValueChangedEventListener)args[0]);
                valueChangedEventListeners.addListener(eventListener);
                return null;
            }
        }).when(fsuipcConnector).addEventListener(any(ValueChangedEventListener.class));
    }

    @Test
    public void shouldSetParameters() throws Exception {
        ConnectorHighBitEventHandler connectorHighBitEventHandler = new ConnectorHighBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x1234), 2, (byte)0, true);
        assertEquals(new FSUIPCOffset(0x1234), connectorHighBitEventHandler.getOffset());
        assertEquals(2, (int)connectorHighBitEventHandler.getSize());
        assertEquals(0, (byte)connectorHighBitEventHandler.getBitIdx());
        assertEquals(true, (boolean)connectorHighBitEventHandler.getIsHighBit());
    }

    @Test
    public void shouldMonitorAndAddEventListener() throws Exception {
        connectorHighBitEventHandler = new ConnectorHighBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x1234), 2, (byte)0, true);
        connectorHighBitEventHandler.addConnectorEventHandlerListener(new ConnectorEventHandlerListener<Object>() {
            @Override
            public void valueChanged(Object event) { }
            @Override
            public void stateChanged(String state) { }
        });
        // ~
        verify(fsuipcConnector).monitor(eq(new OffsetIdent(0x1234, 2)));
        verify(fsuipcConnector).addEventListener(any(ValueChangedEventListener.class));
    }

    @Test
    public void shouldFireValueChangedEvent() throws Exception {
        final List<Object> valuesChanged = new ArrayList<>();
        connectorHighBitEventHandler = new ConnectorHighBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x0001), 2, (byte)0, true);
        connectorHighBitEventHandler.addConnectorEventHandlerListener(new ConnectorEventHandlerListener<Object>() {
            @Override
            public void valueChanged(Object event) {
                valuesChanged.add(event);
            }

            @Override
            public void stateChanged(String state) {

            }
        });
        // ~
        valueChangedEventListeners.fire().valueChanged(new FSUIPCConnectorEvent(0x0001, 2, ByteArray.create((Integer)0xFFFF, 2)));
        // ~
        assertEquals(1, valuesChanged.size());
        assertNull(valuesChanged.get(0));
    }

    @Test
    public void shouldNotFireValueChangedEvent_NoHighBit() throws Exception {
        final List<Object> valuesChanged = new ArrayList<>();
        connectorHighBitEventHandler = new ConnectorHighBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x0001), 2, (byte)0, true);
        connectorHighBitEventHandler.addConnectorEventHandlerListener(new ConnectorEventHandlerListener<Object>() {
            @Override
            public void valueChanged(Object event) {
                valuesChanged.add(event);
            }

            @Override
            public void stateChanged(String state) {

            }
        });
        // ~
        valueChangedEventListeners.fire().valueChanged(new FSUIPCConnectorEvent(0x0001, 2, ByteArray.create((Integer)0x0000, 2)));
        valueChangedEventListeners.fire().valueChanged(new FSUIPCConnectorEvent(0x0001, 2, ByteArray.create((Integer)0x0002, 2)));
        // ~
        assertEquals(0, valuesChanged.size());
    }
}
