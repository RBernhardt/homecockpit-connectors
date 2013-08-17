package de.newsarea.homecockpit.connector.fsuipc;

import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class FSUIPCNativeConnectorTest {

    private FSUIPCInterface fsuipcInterface;

    @BeforeMethod
    public void setUp() throws Exception {
        fsuipcInterface = mock(FSUIPCInterface.class);
    }

    @Test
    public void shouldReadValue() throws Exception {
        FSUIPCNativeConnector fsuipcNativeConnector = new FSUIPCNativeConnector(fsuipcInterface);
        OffsetIdent ident = new OffsetIdent(0x0001, 1);
        byte[] data = new byte[] { 0, 1 };
        when(fsuipcInterface.read(ident)).thenReturn(new OffsetItem(0x0001, 2, data));
        // ~
        assertEquals("0x0001 : 2 : 0x0001", fsuipcNativeConnector.read(ident).toString());
    }

    @Test
    public void shouldHandleValueChangedEvent() throws Exception {
        final List<OffsetEventListener> offsetEventListeners = new ArrayList<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                offsetEventListeners.add((OffsetEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(fsuipcInterface).addEventListener(any(OffsetEventListener.class));
        // ~
        final List<FSUIPCConnectorEvent> events = new ArrayList<>();
        FSUIPCNativeConnector fsuipcNativeConnector = new FSUIPCNativeConnector(fsuipcInterface);
        fsuipcNativeConnector.addEventListener(new ValueChangedEventListener<FSUIPCConnectorEvent>() {
            @Override
            public void valueChanged(FSUIPCConnectorEvent event) {
                events.add(event);
            }
        });
        // ~
        OffsetItem offsetItem = new OffsetItem(0x0001, 2, new byte[] {});
        offsetEventListeners.get(0).valueChanged(offsetItem);
        assertEquals(1, events.size());
    }
}
