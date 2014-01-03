package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InboundKeyboardEventHandlerTest {

    @Test
    public void shouldSendValueCTRL100() throws Exception {
        FSUIPCConnector fsuipcConnector = mock(FSUIPCConnector.class);
        // ~
        InboundKeyboardEventHandler inboundKeyboardEventHandler = new InboundKeyboardEventHandler(fsuipcConnector, FSUIPCOffset.fromString("0x0000"), 2, InboundKeyboardEventHandler.Key.CTRL, (byte)100);
        inboundKeyboardEventHandler.handleInboundEvent(null);
        // ~
        OffsetItem offsetItem = new OffsetItem(0, 2, new byte[] {0x02, (byte)100});
        verify(fsuipcConnector).write(eq(offsetItem));
    }

}
