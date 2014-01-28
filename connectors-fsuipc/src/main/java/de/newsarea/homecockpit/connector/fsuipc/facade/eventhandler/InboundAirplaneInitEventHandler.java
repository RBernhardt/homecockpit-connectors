package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.AirplanePosition;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InboundAirplaneInitEventHandler extends AbstractEventHandler<FSUIPCConnector> implements InboundEventHandler {

    private static final Logger log = LoggerFactory.getLogger(InboundAirplaneInitEventHandler.class);

    public InboundAirplaneInitEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    public InboundAirplaneInitEventHandler(FSUIPCConnector connector) {
        this(connector, Collections.<String, String>emptyMap());
    }

    @Override
    public void handleInboundEvent(Object value) throws IOException {
        if(!(value instanceof AirplanePosition)) {
            throw new IllegalArgumentException("value with type AirplanePosition expected - " + value);
        }
        //
        AirplanePosition airplanePosition = (AirplanePosition) value;
        //
        List<OffsetItem> offsetItemList = new ArrayList<>();
        // speed
        offsetItemList.add(new OffsetItem(0x0558, 4, ByteArray.create(airplanePosition.getSpeed(), 4)));
        // initialize on ground
        offsetItemList.add(new OffsetItem(0x055C, 4, ByteArray.create(airplanePosition.getSpeed() > 0 ? 0 : 1, 4)));
        // latitude
        offsetItemList.add(new OffsetItem(0x0560, 8, ByteArray.create(FSUIPCUtil.toFSUIPCLatitude(airplanePosition.getLatitude()), 8)));
        // longitude
        offsetItemList.add(new OffsetItem(0x0568, 8, ByteArray.create(FSUIPCUtil.toFSUIPCLongitude(airplanePosition.getLongitude()), 8)));
        // altitude
        offsetItemList.add(new OffsetItem(0x0570, 8, ByteArray.create(FSUIPCUtil.toFSUIPCAlititude(airplanePosition.getAltitude()), 8)));
        // pitch
        offsetItemList.add(new OffsetItem(0x0578, 4, ByteArray.create((int)FSUIPCUtil.toFSUIPCDegree(airplanePosition.getPitch()), 4)));
        // bank
        offsetItemList.add(new OffsetItem(0x057C, 4, ByteArray.create((int)FSUIPCUtil.toFSUIPCDegree(airplanePosition.getBank()), 4)));
        // heading
        offsetItemList.add(new OffsetItem(0x0580, 4, ByteArray.create((int)FSUIPCUtil.toFSUIPCDegree(airplanePosition.getHeading()), 4)));
        //
        OffsetItem[] offsetItems = offsetItemList.toArray(new OffsetItem[offsetItemList.size()]);

        int firstOffset = offsetItems[0].getOffset();
        ByteArray byteArray = ByteArray.create(createByteArray(offsetItems));
        // ~
        OffsetItem offsetItem = new OffsetItem(firstOffset, byteArray.getSize(), byteArray);
        getConnector().write(offsetItem);
    }

	/* HELPER */



    private byte[] createByteArray(OffsetItem[] offsetItems) {
        int currentOffset = offsetItems[0].getOffset();
        OffsetItem lastOffsetItem  = offsetItems[offsetItems.length - 1];
        int lastOffset = lastOffsetItem.getOffset() + lastOffsetItem.getSize();
        //
        byte[] output = new byte[lastOffset - currentOffset];
        for(OffsetItem offsetItem : offsetItems) {
            if(offsetItem.getOffset() != currentOffset) {
                throw new IllegalArgumentException("expected offset was " + currentOffset + " but was " + offsetItem.getOffset());
            }
            //
            for(int j=0; j < offsetItem.getValue().getSize(); j++) {
                int lOffset = lastOffset - currentOffset - offsetItem.getSize();
                if(lOffset < 0) {
                    throw new IllegalArgumentException("invalid first or last offset item detected - " + offsetItems[0].getOffset() + " : " + lastOffset + " : " + output.length);
                }
                output[lOffset + j] = offsetItem.getValue().get(j);
            }
            //
            currentOffset += offsetItem.getSize();
        }
        return output;
    }

}

