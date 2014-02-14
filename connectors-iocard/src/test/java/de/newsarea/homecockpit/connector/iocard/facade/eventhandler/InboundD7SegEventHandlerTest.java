package de.newsarea.homecockpit.connector.iocard.facade.eventhandler;

import de.newsarea.homecockpit.connector.iocard.IOCardConnector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InboundD7SegEventHandlerTest {

    private IOCardConnector ioCardConnector;

    @BeforeMethod
    public void beforeMethod() {
        ioCardConnector = mock(IOCardConnector.class);
    }

    @Test
    public void shouldHandleOneDigitPositiveValue() throws Exception {
        InboundD7SegEventHandler inboundD7SegEventHandler = new InboundD7SegEventHandler(ioCardConnector, (byte)6, (byte)3);
        inboundD7SegEventHandler.handleInboundEvent(0);
        verify(ioCardConnector).write(eq((byte) 6), eq((byte)-3));
    }

    @Test
    public void shouldHandleThreeDigitPositiveValue() throws Exception {
        InboundD7SegEventHandler inboundD7SegEventHandler = new InboundD7SegEventHandler(ioCardConnector, (byte)6, (byte)3);
        inboundD7SegEventHandler.handleInboundEvent(123);
        verify(ioCardConnector).write(eq((byte)6), eq((byte) 1));
        verify(ioCardConnector).write(eq((byte)7), eq((byte) 2));
        verify(ioCardConnector).write(eq((byte)8), eq((byte) 3));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowException_ValueLengthToLong() throws Exception {
        InboundD7SegEventHandler inboundD7SegEventHandler = new InboundD7SegEventHandler(ioCardConnector, (byte)6, (byte)3);
        inboundD7SegEventHandler.handleInboundEvent(1234);
    }

    @Test
    public void shouldHandleOneNegativeValue() throws Exception {
        InboundD7SegEventHandler inboundD7SegEventHandler = new InboundD7SegEventHandler(ioCardConnector, (byte)6, (byte)2);
        inboundD7SegEventHandler.handleInboundEvent(-1);
        verify(ioCardConnector).write(eq((byte)6), eq((byte) -8));
        verify(ioCardConnector).write(eq((byte)7), eq((byte) 1));
    }

    @Test
    public void shouldHandleIntegerValue() throws Exception {
        InboundD7SegEventHandler inboundD7SegEventHandler = new InboundD7SegEventHandler(ioCardConnector, (byte)6, (byte)3);
        inboundD7SegEventHandler.handleInboundEvent(1);
        verify(ioCardConnector).write(eq((byte)6), eq((byte)-3));
        verify(ioCardConnector).write(eq((byte)7), eq((byte)-3));
        verify(ioCardConnector).write(eq((byte)8), eq((byte)1));
    }

    @Test
    public void shouldHandleStringValue() throws Exception {
        InboundD7SegEventHandler inboundD7SegEventHandler = new InboundD7SegEventHandler(ioCardConnector, (byte)6, (byte)3);
        inboundD7SegEventHandler.handleInboundEvent("1");
        verify(ioCardConnector).write(eq((byte)6), eq((byte) -3));
    }

}
