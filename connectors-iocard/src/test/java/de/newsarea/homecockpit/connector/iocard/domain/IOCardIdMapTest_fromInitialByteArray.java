package de.newsarea.homecockpit.connector.iocard.domain;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class IOCardIdMapTest_fromInitialByteArray {


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldDetectInvalidMessage_InvalidLength() throws Exception {
        //IOCardIdMap.fromInitialByteArray(hexToByteArray("41FE010200040A00"));
        IOCardIdMap.fromInitialByteArray(hexToByteArray("41FE010200040A"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldDetectInvalidMessage_InvalidMessageIdent() throws Exception {
        IOCardIdMap.fromInitialByteArray(hexToByteArray("40FE010200040A00"));
    }

    @Test
    public void shouldReturnMapWithSectionZero() throws Exception {
        IOCardIdMap map = IOCardIdMap.fromInitialByteArray(hexToByteArray("41FE010200040A00"));
        assertEquals("!0, 1, !2, !3, !4, !5, !6, !7", map.toString());
    }

    @Test
    public void shouldReturnMapWithSectionZeroAndTwo() throws Exception {
        IOCardIdMap map = IOCardIdMap.fromInitialByteArray(hexToByteArray("41FE030202040A00")); // 1 10
        assertEquals("!0, 1, !2, !3, !4, !5, !6, !7, !9, 10, !11, !12, !13, !14, !15, !16", map.toString());
    }

    private byte[] hexToByteArray(String hexString) throws DecoderException {
        return Hex.decodeHex(hexString.toCharArray());
    }

}
