package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCNetConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.AirplanePosition;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.util.FSUIPCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InboundAirplaneInitEventHandler extends AbstractFSUIPCEventHandler implements InboundEventHandler {

    private static final Logger log = LoggerFactory.getLogger(InboundAirplaneInitEventHandler.class);

    public InboundAirplaneInitEventHandler(FSUIPCNetConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    @Override
    public void handleInboundEvent(Object value) {
        if(!(value instanceof AirplanePosition)) {
            throw new IllegalArgumentException("airplane position object expected - " + value);
        }
        //
        AirplanePosition airplanePosition = (AirplanePosition) value;
        //
        List<OffsetItem> offsetItems = new ArrayList<>();
        // speed
        offsetItems.add(new OffsetItem(0x0558, 4, ByteArray.create(airplanePosition.getSpeed(), 4)));
        // initialize on ground
        offsetItems.add(new OffsetItem(0x055C, 4, ByteArray.create(airplanePosition.getSpeed() > 0 ? 0 : 1, 4)));
        // latitude
        offsetItems.add(new OffsetItem(0x0560, 8, ByteArray.create(FSUIPCUtil.toFSUIPCLatitude(airplanePosition.getLatitude()), 8)));
        // longitude
        offsetItems.add(new OffsetItem(0x0568, 8, ByteArray.create(FSUIPCUtil.toFSUIPCLongitude(airplanePosition.getLongitude()), 8)));
        // altitude
        offsetItems.add(new OffsetItem(0x0570, 8, ByteArray.create(FSUIPCUtil.toFSUIPCAlititude(airplanePosition.getAltitude()), 8)));
        // pitch
        offsetItems.add(new OffsetItem(0x0578, 4, ByteArray.create(FSUIPCUtil.toFSUIPCDegree(airplanePosition.getPitch()), 4)));
        // bank
        offsetItems.add(new OffsetItem(0x057C, 4, ByteArray.create(FSUIPCUtil.toFSUIPCDegree(airplanePosition.getBank()), 4)));
        // heading
        offsetItems.add(new OffsetItem(0x0580, 4, ByteArray.create(FSUIPCUtil.toFSUIPCDegree(airplanePosition.getHeading()), 4)));
        //
        try {
            getConnector().write(offsetItems.toArray(new OffsetItem[offsetItems.size()]));
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}

