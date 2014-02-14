package de.newsarea.homecockpit.connector.iocard;

import de.newsarea.homecockpit.connector.AbstractConnector;
import de.newsarea.homecockpit.connector.GeneralConnector;
import de.newsarea.homecockpit.connector.iocard.domain.IOCardIdMap;
import de.newsarea.homecockpit.connector.iocard.event.IOCardConnectorEvent;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.util.TreeMap;

public class IOCardConnector extends AbstractConnector<IOCardConnectorEvent> {

    private static Logger log = LoggerFactory.getLogger(IOCardConnector.class);

    private String lastReceivedEvent;

    private IOCardIdMap rememberedOutputs;
    private IOCardIdMap rememberedInputs;

    public IOCardIdMap getRememberedOutputs() {
        return rememberedOutputs;
    }

    public IOCardIdMap getRememberedInputs() {
        return rememberedInputs;
    }

    public IOCardConnector(GeneralConnector<String> generalConnector) {
        super(generalConnector);
        rememberedOutputs  = new IOCardIdMap(new TreeMap<Integer, Boolean>());
        rememberedInputs = new IOCardIdMap(new TreeMap<Integer, Boolean>());
    }

    /**
     *
     * @throws ConnectException
     */
    @Override
    public void open() throws ConnectException {
        super.open();
        // ~
        for(int i=0; i < 5; i++) {
            /*
            * 3D003A[01]39[04]FFFF
            *         |     +- number of analog axis
            *         +- number of connected mastercards
            */
            try {
                generalConnector.write("3D003A013904FFFF");
                generalConnector.write("0A010A020A030A04");
                generalConnector.write("0A050A060A070A08");
                generalConnector.write("0A050A060A070A08");
                generalConnector.write("0A090A0A0A0B0A0C");
                generalConnector.write("0A0D0A0E0A0F0A10");
                generalConnector.write("0A110A120A130A14");
                generalConnector.write("0A150A160A170A18");
                generalConnector.write("0A190A1A0A1B0A1C");
                generalConnector.write("0A1D0A1E0A1F0A20");
                generalConnector.write("0A210A220A230A24");
                generalConnector.write("0A250A260A270A28");
                generalConnector.write("0A290A2A0A2B0A2C");
                generalConnector.write("0A2D0A2E0A2F0A30");
                generalConnector.write("0A310A320A330A34");
                generalConnector.write("0A350A360A370A38");
                generalConnector.write("0A390A3A0A3B0A3C");
                generalConnector.write("0A3D0A3E0A3FFFFF");
                return;
            } catch(IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            // ~
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) { }
        }
    }

    @Override
    protected void handleValueReceived(String value) {
        if(value.equals(lastReceivedEvent)) {
            log.warn("value already received - {}", value);
            return;
        }
        lastReceivedEvent = value;
        //
        try {
            byte[] data = Hex.decodeHex(value.toCharArray());
            IOCardConnectorEvent ioCardConnectorEvent = IOCardConnectorEvent.fromByteArray(data);
            switch(ioCardConnectorEvent.getType()) {
                case INIT:
                    initializeRememberedInputs();
                    updateRememberedInputs(ioCardConnectorEvent.getMap());
                    break;
                case INPUT:
                    log.info("input value received - {}", value);
                    updateRememberedInputs(ioCardConnectorEvent.getMap());
                    fireEvent(ioCardConnectorEvent);
                    break;
                case CONTINUOUS_INPUT:
                    /* do nothing */
                    break;
                case UNDEFINED:
                    log.warn("iocard message ignored - {}", value);
                    break;
            }
        } catch (DecoderException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Write Infos to 7 Segment Display
     *
     * @param d7Seg - SegmentIdx [0 - 15]
     * @param value - Value
     *
     * d7Seg = X & value = 59; Schaltet alle Segmente auf X
     * d7Seg = 0 & value = 56; Schaltet alle Segmente aus
     * d7Seg = 1 & value = 56; Schaltet alle Segmente auf 8
     * d7Seg = X & value = -8; '-' Zeichen auf Position X
     * d7Seg = X & value = -7; '6' Zeichen auf Position X
     * d7Seg = X & value = -6; '£' Zeichen auf Position X
     * d7Seg = X & value = -5; 'd' Zeichen auf Position X
     * d7Seg = X & value = -3; '0' Zeichen auf Position X
     * d7Seg = X & value = -2; '1' Zeichen auf Position X
     * d7Seg = X & value = -1; '2' Zeichen auf Position X
     */
    public void write(byte d7Seg, byte value) throws IOException {
        // @TODO Eingabeparameter Überprüfung verbessern
        write(new byte[]{value, d7Seg, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
    }

    /***
     * @param index - output index [11 - 55]
     * @param value - is high bit
     */
    public void write(Integer index, Boolean value) throws IOException {
        if(!(index >= 11 && index <= 55)) {
            throw new IllegalArgumentException("index is out of bounds - " + index);
        }
        rememberedOutputs.put(index, value);
        // prepare output data
        Integer offsetIndex = determineOffsetIndex(index);
        byte[] data = new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
        data[0] = Byte.parseByte(offsetIndex.toString());
        data[1] = getRememberedValues(offsetIndex);
        write(data);
    }

    public void write(byte[] data) throws IOException {
        String message = new String(Hex.encodeHex(data));
        generalConnector.write(message);
    }

    private void initializeRememberedInputs() {
        for(int i=0; i <=72; i++) {
            rememberedInputs.put(i, false);
        }
    }

    private void updateRememberedInputs(IOCardIdMap map) {
        for(Integer key : map.keySet()) {
            getRememberedInputs().put(key, map.get(key));
        }
    }

    private byte getRememberedValues(Integer offsetIndex) {
        byte data = 0;
        for(int i=offsetIndex; i < offsetIndex + 8; i++) {
            Boolean isHighBit = getRememberedOutputs().get(i);
            if(isHighBit == null) { isHighBit = false; }
            byte shiftValue = (byte) (isHighBit ? 1 : 0);
            // ~
            byte byteIdx = (byte) ((byte)i - offsetIndex);
            data = (byte) (data | (shiftValue << byteIdx));
        }
        return data;
    }

    private Integer determineOffsetIndex(Integer index) {
        Double mValue = index - ((index - 11D) % 8);
        return (int)Math.round(mValue);
    }

    @Override
    public String toString() {
        return "IOCardConnector";
    }

}
