package de.newsarea.homecockpit.connector.iocard.domain;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class IOCardIdMapTest_fromInputByteArrayOfInitializedCard {


    @Test
    public void shouldReturnMapWithSectionFour() throws Exception {
        IOCardIdMap map = IOCardIdMap.fromInputByteArrayOfInitializedCard(hexToByteArray("41ff088001ffffff")); // 1 10
        // !27, !28, !29, !30, !31, !32, !33, 34
        // !27, !28, !29, !30, !31, !32, !33, 34
        assertEquals("!27, !28, !29, !30, !31, !32, !33, 34, 35", map.toString());
    }

    @Test
    public void shouldReturnMapWithSectionFive() throws Exception {
        IOCardIdMap map = IOCardIdMap.fromInputByteArrayOfInitializedCard(hexToByteArray("41ff100200ffffff"));
        assertEquals("!36, 37, !38, !39, !40, !41, !42, !43, !44", map.toString());
    }

    @Test
    public void shouldReturnMapWithSectionSix() throws Exception {
        IOCardIdMap map = IOCardIdMap.fromInputByteArrayOfInitializedCard(hexToByteArray("41ff202000ffffff"));
        assertEquals("!45, !46, !47, !48, !49, 50, !51, !52, !53", map.toString());
    }

    @Test
    public void shouldReturnMapWithSectionSeven() throws Exception {
        IOCardIdMap map = IOCardIdMap.fromInputByteArrayOfInitializedCard(hexToByteArray("41ff402000ffffff"));
        assertEquals("!54, !55, !56, !57, !58, 59, !60, !61, !62", map.toString());
    }

    @Test
    public void shouldReturnMapWithSectionEight() throws Exception {
        IOCardIdMap map = IOCardIdMap.fromInputByteArrayOfInitializedCard(hexToByteArray("41ff802400ffffff"));
        assertEquals("!63, !64, 65, !66, !67, 68, !69, !70, !71", map.toString());
    }

    private byte[] hexToByteArray(String hexString) throws DecoderException {
        return Hex.decodeHex(hexString.toCharArray());
    }

}
