package de.newsarea.homecockpit.connector.iocard.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.iocard.IOCardConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class InboundD7SegEventHandler extends AbstractEventHandler<IOCardConnector> implements InboundEventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(InboundD7SegEventHandler.class);

    private Byte dSegId;
    private Byte digits;

    public Byte getDSegId() {
        if(dSegId == null) {
            dSegId = Byte.parseByte(getParameterValue("dSegId"));
        }
        return dSegId;
    }

    public Byte getDigits() {
        if(digits == null) {
            digits = Byte.parseByte(getParameterValue("digits"));
        }
        return digits;
    }

    /* */

    public InboundD7SegEventHandler(IOCardConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    public InboundD7SegEventHandler(IOCardConnector connector, Byte dSegId, Byte digits) {
        this(connector, toParameters(
            new AbstractMap.SimpleEntry<>("dSegId", String.valueOf(dSegId)),
            new AbstractMap.SimpleEntry<>("digits", String.valueOf(digits))
        ));
    }

    @Override
    public void handleInboundEvent(Object value) throws IOException {
        int valueLength = value.toString().length();
        if(valueLength > getDigits()) {
            throw new IllegalArgumentException("less or equal of " +  getDigits() + " expected but was " + valueLength);
        }
        // ~
        Integer intValue;
        if(value instanceof Integer) {
            intValue = (Integer) value;
        } else {
            intValue = Integer.parseInt(value.toString());
        }
        // ~
        String digitValues = String.format("%0" + getDigits() + "d", intValue);
        byte dSegIdCounter = getDSegId();
        for(char d7SegValue : digitValues.toCharArray()) {
            byte d7SegByteValue;
            switch(d7SegValue) {
                case '-':
                    d7SegByteValue = -8;
                    break;
                case '0':
                    d7SegByteValue = -3;
                    break;
                default:
                    d7SegByteValue =  Byte.parseByte(String.valueOf(d7SegValue));
                    break;
            }
            getConnector().write(dSegIdCounter++, d7SegByteValue);
        }

    }
}
