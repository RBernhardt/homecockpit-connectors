package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InboundHighBitEventHandlerTest {

    private FSUIPCConnector fsuipcConnector;

    @BeforeMethod
    public void beforeMethod() {
        fsuipcConnector = mock(FSUIPCConnector.class);
    }

    @Test
    public void shouldSetBit0High() throws Exception {
        when(fsuipcConnector.read(new OffsetIdent(0x0001, 2))).thenReturn(new OffsetItem(0x0001, 2, ByteArray.create((Integer)0x0000, 2)));
        // ~
        InboundHighBitEventHandler inboundHighBitEventHandler = new InboundHighBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x0001), 2, (byte)0);
        inboundHighBitEventHandler.handleInboundEvent(null);
        verify(fsuipcConnector).write(eq(new OffsetItem(0x0001, 2, ByteArray.create((Integer)0x0001, 2))));
    }

    @Test
    public void shouldSetBit2High() throws Exception {
        when(fsuipcConnector.read(new OffsetIdent(0x0001, 2))).thenReturn(new OffsetItem(0x0001, 2, ByteArray.create((Integer)0x0000, 2)));
        // ~
        InboundHighBitEventHandler inboundHighBitEventHandler = new InboundHighBitEventHandler(fsuipcConnector, new FSUIPCOffset(0x0001), 2, (byte)2);
        inboundHighBitEventHandler.handleInboundEvent(null);
        verify(fsuipcConnector).write(eq(new OffsetItem(0x0001, 2, ByteArray.create((Integer)0x0004, 2))));
    }
}
