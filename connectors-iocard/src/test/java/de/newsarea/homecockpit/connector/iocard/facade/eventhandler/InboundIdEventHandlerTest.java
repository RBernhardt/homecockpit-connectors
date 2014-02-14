package de.newsarea.homecockpit.connector.iocard.facade.eventhandler;

import de.newsarea.homecockpit.connector.iocard.IOCardConnector;
import de.newsarea.homecockpit.connector.iocard.domain.IOCardIdMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InboundIdEventHandlerTest {

    private IOCardConnector ioCardConnector;

    @BeforeMethod
    public void setUp() throws Exception {
        ioCardConnector = mock(IOCardConnector.class);
    }

    @Test
    public void shouldHandleInboundEvent() throws Exception {
        InboundIdEventHandler inboundIdEventHandler = new InboundIdEventHandler(ioCardConnector, IOCardIdMap.fromString("11"));
        inboundIdEventHandler.handleInboundEvent(null);
        verify(ioCardConnector).write(11, true);
        // ~
        inboundIdEventHandler = new InboundIdEventHandler(ioCardConnector, IOCardIdMap.fromString("!12"));
        inboundIdEventHandler.handleInboundEvent(null);
        verify(ioCardConnector).write(12, false);
    }

}
