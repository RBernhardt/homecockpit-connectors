package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class LeverValueConverterTest {

    private LeverValueConverter leverValueConverter = new LeverValueConverter();

    @Test
    public void shouldReturnResult() throws Exception {
        assertEquals(16384L, (long)leverValueConverter.toOutput(100L));
        assertEquals(0L, (long)leverValueConverter.toOutput(0L));

    }
}
