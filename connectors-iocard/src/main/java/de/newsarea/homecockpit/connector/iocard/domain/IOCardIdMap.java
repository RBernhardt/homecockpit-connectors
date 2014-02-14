package de.newsarea.homecockpit.connector.iocard.domain;

import org.apache.commons.codec.binary.Hex;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IOCardIdMap  {

    private Map<Integer, Boolean> map;

    public IOCardIdMap(Map<Integer, Boolean> map) {
        this.map = map;
    }

    public Boolean get(Integer key) {
        return map.get(key);
    }

    public void put(Integer key, Boolean value) {
        map.put(key, value);
    }

    public boolean containsKey(Integer key) {
        return map.containsKey(key);
    }

    public Set<Map.Entry<Integer, Boolean>> entrySet() {
        return map.entrySet();
    }

    public Set<Integer> keySet() {
        return map.keySet();
    }

    /***
     * The method extracts the id map of an input iocard message
     *
     * @param data The data array is the the representation of an message from the iocard
     *
     * ex.  0108040000000000
     *      0101000100000000
     *
     *      [01][20][60][00]102001ff
     *        |   |   |   +- ID States Bit 9 (8)
     *        |   |   +- ID States Bit 8 (0-7)
     *        |   +- SectionId
     *        +- DeviceId
     *
     * The following list is the description of the bytes.
     * Byte 0 = DeviceId
     * Byte 1 = SectionId [0-7] (8 sections by mastercard connector (JP3 und JP4))
     *          every bit is a representation of a section
     * Byte 2 = Bit 8 (0-7)
     * Byte 3 = Bit 9 (8)
     *
     * @return
     */
    public static IOCardIdMap fromInputByteArray(byte[] data) {
        Map<Integer, Boolean> map = new TreeMap<>();
        byte sectionId = (byte) (Math.log(Math.abs(data[1])) / Math.log(2));
        for(int i=0; i < 9; i++) {
            byte bitIdx = (byte) (i % 9);
            Integer id = sectionId * 9 + bitIdx;
            if(i < 8) {
                // add the bit 0-8 to the id map
                Boolean hasHighBit = isHighBit(data[2], bitIdx);
                map.put(id, hasHighBit);
            } else {
                // add the bit 9 to the id map
                Boolean hasHighBit = isHighBit(data[3], (byte) 0);
                map.put(id, hasHighBit);
            }
        }
        return new IOCardIdMap(map);
    }

    /***
     * The method extracts the id map of an iocard message
     *
     * @param data The data array is the the representation of an message from the iocard
     *
     * ex.  41 FE 01 02 00 04 0A 00 = 01
     *      41 FE 01 04 00 04 0A 00 = 02
     *      41 FE 01 08 00 04 0A 00 = 03
     *      41 FE 03 02 02 00 0A 00 = 01 10
     *      41 FE 03 04 04 00 0A 00 = 02 11
     *      41 FE 06 02 02 00 0A 00 = 10 19
     *      41 FF 0F 02 02 02 02 00 = 01 10 19 28
     *      41 FE 0A 01 80 00 0A 00 = 09 34
     *      41 FE 0A 20 80 00 0A 00 = 14 34
     *      41 FE 0A 20 C0 00 0A 00 = 14 33 34
     *      41 FE 2A 20 C0 01 00 00 = 14 33 34 45
     *      41 FE 2A 01 80 40 00 00 = 09 34 51
     *      41 FE 2A 01 80 20 00 00 = 09 34 50
     *      41 FE 80 3C 00 04 0A 00 = 44 65 66 67 68
     *
     *      [41][FE][06][02][02] 00 0A 00 = 10 19
     *        |   |   |   |   +- ID States (02 = 0000 0010 = 2 * 9 + 1 = 19)
     *        |   |   |   +- ID States (02 = 0000 0010 = 1 * 9 + 1 = 10)
     *        |   |   +- available sections (06 = 0000 0110 = Section 1 and 2)
     *        |   +- ???
     *        +- init message identifier
     *
     * @return
     */
    public static IOCardIdMap fromInitialByteArray(byte[] data) {
        if(data.length != 8) { throw new IllegalArgumentException("invalid message length"); }
        if(data[0] != 0x41) {
            String dataAsHexString = new String(Hex.encodeHex(data));
            throw new IllegalArgumentException("invalid message ident at bit 0 - excepted 0x41 but was - " + dataAsHexString);
        }
        // ~
        int sectionCursor = 3;
        Map<Integer, Boolean> map = new TreeMap<>();
        for(byte sectionId=0; sectionId < 7; sectionId++) {
            // determine sections
            byte sectionIdentifier = data[2];
            boolean isAvaiableSection = isHighBit(sectionIdentifier, sectionId);
            if(isAvaiableSection) {
                byte sectionData = data[sectionCursor];
                for(byte bitIdx=0; bitIdx < 8; bitIdx++) {
                    Integer id = sectionId * 9 + bitIdx;
                    // add the bit 0-8 to the id map
                    Boolean hasHighBit = isHighBit(sectionData, bitIdx);
                    map.put(id, hasHighBit);
                }
                // ~
                sectionCursor++;
            }
        }
        return new IOCardIdMap(map);
    }

    public static IOCardIdMap fromInputByteArrayOfInitializedCard(byte[] data) {
        if(data.length != 8) { throw new IllegalArgumentException("invalid message length"); }
        // ~
        Map<Integer, Boolean> map = new TreeMap<>();
        for(byte sectionId=0; sectionId < 8; sectionId++) {
            // determine sections
            byte sectionIdentifier = data[2];
            boolean isAvaiableSection = isHighBit(sectionIdentifier, sectionId);
            if(isAvaiableSection) {
                byte sectionDataByteThree = data[3];
                for(byte bitIdx=0; bitIdx < 8; bitIdx++) {
                    Integer id = sectionId * 9 + bitIdx;
                    // add the bit 0-8 to the id map
                    Boolean hasHighBit = isHighBit(sectionDataByteThree, bitIdx);
                    map.put(id, hasHighBit);
                }
                // ~
                byte sectionDataByteFour = data[4];
                Boolean hasHighBit = isHighBit(sectionDataByteFour, (byte)0);
                map.put(sectionId * 9 + 8, hasHighBit);
            }
        }
        return new IOCardIdMap(map);
    }

    public static IOCardIdMap fromString(String idsString) {
        Map<Integer, Boolean> map = new TreeMap<>();
        for(String idString : idsString.split(",")) {
            idString = idString.trim();
            //
            boolean isPositive = idString.indexOf("!") != 0;
            Integer id = Integer.parseInt(idString.replace("!", ""));
            map.put(id, isPositive);
        }
        return new IOCardIdMap(map);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IOCardIdMap)) return false;

        IOCardIdMap that = (IOCardIdMap) o;

        if (map != null ? !map.equals(that.map) : that.map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }

    @Override
    public String toString() {
        if(map.size() == 0) { return "IOCardIdMap is empty"; }
        StringBuilder out = new StringBuilder();
        for(Map.Entry<Integer, Boolean> entry : map.entrySet()) {
            out.append(entry.getValue() ? "" : "!");
            out.append(entry.getKey());
            out.append(", ");
        }
        out.delete(out.length() - 2, out.length());
        return out.toString();
    }

    /* */

    private static boolean isHighBit(byte[] data, int idx) {
        int bidx = (idx / 8);
        byte nidx = (byte)(idx % 8);
        return isHighBit(data[data.length - 1 - bidx], nidx);
    }

    public static boolean isHighBit(byte data, byte idx) {
        return (data & (1 << idx)) > 0;
    }

}
