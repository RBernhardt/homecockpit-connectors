package de.newsarea.homecockpit.connector.iocard;

import de.newsarea.homecockpit.connector.GeneralConnector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

public class IOCardConnectorTest {

    private GeneralConnector generalConnector;

    @BeforeMethod
    public void setUp() throws Exception {
        generalConnector = mock(GeneralConnector.class);
    }

    @Test
    public void shouldNotRememberWrites_RawWriteShouldNotRemember() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(new byte[]{10, 2, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        ioCardConnector.write(new byte[]{11, 2, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        ioCardConnector.write(new byte[]{55, 2, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        ioCardConnector.write(new byte[]{56, 2, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertEquals(0, ioCardConnector.getRememberedOutputs().keySet().size());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotWriteValues_IndexOutOfBounds() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(10, true);
    }

    @Test
    public void shouldRememberWriteValues_OneWrite() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(11, true);
        assertEquals(1, ioCardConnector.getRememberedOutputs().keySet().size());
        assertTrue(ioCardConnector.getRememberedOutputs().get(11));
    }

    @Test
    public void shouldRememberWriteValues_TwoWrites() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(11, true);
        assertEquals(1, ioCardConnector.getRememberedOutputs().keySet().size());
        assertTrue(ioCardConnector.getRememberedOutputs().get(11));
        ioCardConnector.write(12, false);
        assertEquals(2, ioCardConnector.getRememberedOutputs().keySet().size());
        assertFalse(ioCardConnector.getRememberedOutputs().get(12));
    }

    @Test
    public void shouldRememberWriteValues_MaxValue() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(55, true);
        assertEquals(1, ioCardConnector.getRememberedOutputs().keySet().size());
        assertTrue(ioCardConnector.getRememberedOutputs().get(55));
    }

    @Test
    public void shouldWriteCompleteFirstSegByte() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(11, true);
        verify(generalConnector).write("0b01ffffffffffff");
        ioCardConnector.write(12, true);
        verify(generalConnector).write("0b03ffffffffffff");
        ioCardConnector.write(13, true);
        verify(generalConnector).write("0b07ffffffffffff");
        ioCardConnector.write(14, true);
        verify(generalConnector).write("0b0fffffffffffff");
        ioCardConnector.write(18, true);
        verify(generalConnector).write("0b8fffffffffffff");
        ioCardConnector.write(19, true);
        verify(generalConnector).write("1301ffffffffffff");
    }

    @Test
    public void shouldWriteCompleteSecondSegByte() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(19, true);
        verify(generalConnector).write("1301ffffffffffff");
    }

    @Test
    public void shouldToggleBit() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(11, true);
        verify(generalConnector).write("0b01ffffffffffff");
        ioCardConnector.write(11, false);
        verify(generalConnector).write("0b00ffffffffffff");
    }

    @Test
    public void shouldWriteEvent() throws Exception {
        IOCardConnector ioCardConnector = new IOCardConnector(generalConnector);
        ioCardConnector.write(new byte[] { 1, 2, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
        verify(generalConnector).write(eq("0102ffffffffffff"));
    }

}
