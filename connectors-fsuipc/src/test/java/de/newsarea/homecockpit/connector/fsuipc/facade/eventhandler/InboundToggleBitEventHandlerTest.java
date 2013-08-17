package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCGeneralConnector;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class InboundToggleBitEventHandlerTest {

    private FSUIPCConnector fsuipcConnector;
    private InboundToggleBitEventHandler inboundToggleBitEventHandler;
    private Map<String, String> parameters;

    @BeforeMethod
    public void beforeMethod() {
        fsuipcConnector = mock(FSUIPCConnector.class);
        parameters = new HashMap<>();
        parameters.put("offset", String.valueOf("0x0001"));
        parameters.put("size", String.valueOf("2"));
        inboundToggleBitEventHandler = new InboundToggleBitEventHandler(fsuipcConnector, parameters);
    }

    @Test
    public void shouldToggleBit() throws Exception {
        parameters.put("bitIdx", String.valueOf(1));
        when(fsuipcConnector.read(any(OffsetIdent.class))).thenReturn(new OffsetItem(0x0001, 2, new byte[] { 0, 0 }));
        // ~
        inboundToggleBitEventHandler.handleInboundEvent(null);
        // ~
        verify(fsuipcConnector).write(any(OffsetItem.class));
    }
}
