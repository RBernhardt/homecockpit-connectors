package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ValueEventHandlerTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowValueExpectedException() throws Exception {
        FSUIPCConnector fsuipcConnector = mock(FSUIPCConnector.class);
        ValueEventHandler valueEventHandler = new ValueEventHandler(fsuipcConnector, FSUIPCOffset.fromString("0x540A"), 2);
        valueEventHandler.handleInboundEvent(null);
    }

    @Test
    public void shouldWriteAltitudeValue() throws Exception {
        FSUIPCConnector fsuipcConnector = mock(FSUIPCConnector.class);
        ValueEventHandler valueEventHandler = new ValueEventHandler(fsuipcConnector, FSUIPCOffset.fromString("0x540A"), 2, "Multiplier100ValueConverter");
        // when
        valueEventHandler.handleInboundEvent(5000);
        // then
        verify(fsuipcConnector).write(eq(new OffsetItem(0x540A, 2, ByteArray.create("50", 2))));
    }
}
