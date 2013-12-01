package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class LeverValueConverterTest {

    private LeverValueConverter leverValueConverter = new LeverValueConverter();

    @Test
    public void shouldReturnCorrectByteArray() throws Exception {
        assertEquals("0x4000", leverValueConverter.toOutput(100).toHexString());
        assertEquals("0x0000", leverValueConverter.toOutput(0).toHexString());

    }
}
