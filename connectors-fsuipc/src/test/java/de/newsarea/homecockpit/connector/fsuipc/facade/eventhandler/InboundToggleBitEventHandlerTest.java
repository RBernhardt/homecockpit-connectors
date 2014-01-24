package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        // given
        InboundToggleBitEventHandler inboundToggleBitEventHandler = new InboundToggleBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x0001), 2 , (byte)1);
        // when
        inboundToggleBitEventHandler.handleInboundEvent(null);
        // then
        verify(fsuipcConnector).toggleBit(0x0001, 2, (byte) 1);
    }

}
