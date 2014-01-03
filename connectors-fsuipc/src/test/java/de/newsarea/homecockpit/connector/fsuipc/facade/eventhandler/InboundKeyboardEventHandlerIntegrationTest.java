package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.GeneralConnector;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCGeneralConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.connector.net.NetGeneralConnector;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InboundKeyboardEventHandlerIntegrationTest {

    private GeneralConnector generalConnector;
    private FSUIPCConnector fsuipcConnector;

    @BeforeMethod
    public void setUp() throws Exception {
        generalConnector = new NetGeneralConnector("drevil", 4020);
        // ~
        fsuipcConnector = new FSUIPCGeneralConnector(generalConnector);
        fsuipcConnector.open();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        fsuipcConnector.close();
    }

    @Test(enabled = false)
    public void shouldWriteThreeSameValue() throws Exception {
        InboundKeyboardEventHandler inboundKeyboardEventHandler = new InboundKeyboardEventHandler(fsuipcConnector, FSUIPCOffset.fromString("5428"), 2, InboundKeyboardEventHandler.Key.NOTSET, (byte)65);
        inboundKeyboardEventHandler.handleInboundEvent(null);
        Thread.sleep(2000);
        inboundKeyboardEventHandler = new InboundKeyboardEventHandler(fsuipcConnector, FSUIPCOffset.fromString("5428"), 2, InboundKeyboardEventHandler.Key.NOTSET, (byte)65);
        inboundKeyboardEventHandler.handleInboundEvent(null);
        Thread.sleep(2000);
        inboundKeyboardEventHandler = new InboundKeyboardEventHandler(fsuipcConnector, FSUIPCOffset.fromString("5428"), 2, InboundKeyboardEventHandler.Key.NOTSET, (byte)65);
        inboundKeyboardEventHandler.handleInboundEvent(null);
    }

}
