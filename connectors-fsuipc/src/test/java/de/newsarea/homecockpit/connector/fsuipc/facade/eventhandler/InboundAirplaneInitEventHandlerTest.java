package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.AirplanePosition;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;
import org.mockito.AdditionalMatchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

public class InboundAirplaneInitEventHandlerTest {

    private FSUIPCConnector fsuipcConnector;

    @BeforeMethod
    public void beforeMethod() {
        fsuipcConnector = mock(FSUIPCConnector.class);
    }

    @Test
    public void shouldHandleAirplaneInfo() throws Exception {
        InboundAirplaneInitEventHandler inboundAirplaneInitEventHandler = new InboundAirplaneInitEventHandler(fsuipcConnector);
        inboundAirplaneInitEventHandler.handleInboundEvent(new AirplanePosition(10.0, 10.1, 10.2, 11, 12, 13, 14 ));
        // create expected result
        List<OffsetItem> offsetItemList = new ArrayList<>();
        offsetItemList.add(new OffsetItem(0x0558, 4, ByteArray.create(14, 4))); // speed
        offsetItemList.add(new OffsetItem(0x055C, 4, ByteArray.create(0, 4))); // initialize on ground
        offsetItemList.add(new OffsetItem(0x0560, 8, ByteArray.create(0x0010F5098E38E38EL, 8))); // latitude
        offsetItemList.add(new OffsetItem(0x0568, 8, ByteArray.create(0x072EA61D950C8400L, 8))); // longitude
        offsetItemList.add(new OffsetItem(0x0570, 8, ByteArray.create(0x00000003067E98FFL, 8))); // altitude
        offsetItemList.add(new OffsetItem(0x0578, 4, ByteArray.create(0x07D27D59, 4))); // pitch
        offsetItemList.add(new OffsetItem(0x057C, 4, ByteArray.create(0x088888BE, 4))); // bank
        offsetItemList.add(new OffsetItem(0x0580, 4, ByteArray.create(0x093E9423, 4))); // heading
        // ~
        OffsetItem[] offsetItems = offsetItemList.toArray(new OffsetItem[offsetItemList.size()]);
        verify(fsuipcConnector).write(AdditionalMatchers.aryEq(offsetItems));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowException_InvalidValueObject() throws Exception {
        InboundAirplaneInitEventHandler inboundAirplaneInitEventHandler = new InboundAirplaneInitEventHandler(fsuipcConnector);
        inboundAirplaneInitEventHandler.handleInboundEvent(null);
    }
}
