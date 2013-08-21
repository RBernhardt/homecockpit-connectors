package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCGeneralConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class InboundToggleBitEventHandlerTest {

    private FSUIPCConnector fsuipcConnector;

    @BeforeMethod
    public void beforeMethod() {
        fsuipcConnector = mock(FSUIPCConnector.class);
    }

    @Test
    public void shouldSetParameters() throws Exception {
        InboundToggleBitEventHandler inboundToggleBitEventHandler = new InboundToggleBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x1234), 2, (byte)0);
        assertEquals(new FSUIPCOffset(0x1234), inboundToggleBitEventHandler.getOffset());
        assertEquals(2, (int)inboundToggleBitEventHandler.getSize());
        assertEquals(0, (byte)inboundToggleBitEventHandler.getBitIdx());
    }

    @Test
    public void shouldToggleBit() throws Exception {
        InboundToggleBitEventHandler inboundToggleBitEventHandler = new InboundToggleBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x0001), 2 , (byte)1);
        when(fsuipcConnector.read(any(OffsetIdent.class))).thenReturn(new OffsetItem(0x0001, 2, new byte[] { 0, 0 }));
        // ~
        inboundToggleBitEventHandler.handleInboundEvent(null);
        // ~
        verify(fsuipcConnector).write(any(OffsetItem.class));
    }

}
