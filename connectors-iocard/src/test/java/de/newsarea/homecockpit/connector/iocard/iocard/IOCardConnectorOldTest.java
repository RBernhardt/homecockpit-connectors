package de.newsarea.homecockpit.connector.iocard.iocard;

import de.newsarea.homecockpit.connector.iocard.domain.IOCardIdMap;
import de.newsarea.homecockpit.connector.iocard.event.IOCardConnectorEvent;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IOCardConnectorOldTest {

    @Test
    public void testDetermineIdMap_Section0_AllOff() throws Exception {
        assertIdMap("0101000000000000", "0:0 1:0 2:0 3:0 4:0 5:0 6:0 7:0 8:0");
    }

    @Test
    public void testDetermineIdMap_Section1_AllOff() throws Exception {
        assertIdMap("0102000000000000", "9:0 10:0 11:0 12:0 13:0 14:0 15:0 16:0 17:0");
    }

    @Test
    public void testDetermineIdMap_Section2_AllOff() throws Exception {
        assertIdMap("0104000000000000", "18:0 19:0 20:0 21:0 22:0 23:0 24:0 25:0 26:0");
    }

    @Test
    public void testDetermineIdMap_Section3_AllOff() throws Exception {
        assertIdMap("0108000000000000", "27:0 28:0 29:0 30:0 31:0 32:0 33:0 34:0 35:0");
    }

    @Test
    public void testDetermineIdMap_Section4_AllOff() throws Exception {
        assertIdMap("0110000000000000", "36:0 37:0 38:0 39:0 40:0 41:0 42:0 43:0 44:0");
    }

    @Test
    public void testDetermineIdMap_Section5_AllOff() throws Exception {
        assertIdMap("0120000000000000", "45:0 46:0 47:0 48:0 49:0 50:0 51:0 52:0 53:0");
    }

    @Test
    public void testDetermineIdMap_Section6_AllOff() throws Exception {
        assertIdMap("0140000000000000", "54:0 55:0 56:0 57:0 58:0 59:0 60:0 61:0 62:0");
    }

    @Test
    public void testDetermineIdMap_Section7_AllOff() throws Exception {
        assertIdMap("0180000000000000", "63:0 64:0 65:0 66:0 67:0 68:0 69:0 70:0 71:0");
    }

    @Test
    public void testDetermineIdMap_Section0_AllOn() throws Exception {
        assertIdMap("0101FF0100000000", "0:1 1:1 2:1 3:1 4:1 5:1 6:1 7:1 8:1");
    }

    @Test
    public void testDetermineIdMap_Section3_AllOn() throws Exception {
        assertIdMap("0108FF0100000000", "27:1 28:1 29:1 30:1 31:1 32:1 33:1 34:1 35:1");
    }

    @Test
    public void testDetermineIdMap_Section0() throws Exception {
        assertIdMap("0101010000000000", "0:1 1:0 2:0 3:0 4:0 5:0 6:0 7:0 8:0");
        assertIdMap("0101020000000000", "0:0 1:1 2:0 3:0 4:0 5:0 6:0 7:0 8:0");
        assertIdMap("0101030000000000", "0:1 1:1 2:0 3:0 4:0 5:0 6:0 7:0 8:0");
    }

    private IOCardIdMap callDetermineIdMap(String value) throws DecoderException {
        return IOCardIdMap.fromInputByteArray(Hex.decodeHex(value.toCharArray()));
    }

    /* HELPER */

    private void assertIdMap(String value, String compareValue) throws DecoderException {
        IOCardIdMap ioCardIdMap = callDetermineIdMap(value);
        assertEquals(compareValue, idMapToString(ioCardIdMap));
    }

    private String idMapToString(IOCardIdMap ioCardIdMap) {
        StringBuilder out = new StringBuilder();
        for(Map.Entry<Integer, Boolean> entry : ioCardIdMap.entrySet()) {
            out.append(entry.getKey());
            out.append(":");
            out.append(entry.getValue() ? "1" : "0");
            out.append(" ");
        }
        out.deleteCharAt(out.length() - 1);
        return out.toString();
    }


}
