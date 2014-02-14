package de.newsarea.homecockpit.connector.iocard.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.iocard.IOCardConnector;
import de.newsarea.homecockpit.connector.iocard.domain.IOCardIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class InboundIdEventHandler extends AbstractEventHandler<IOCardConnector> implements InboundEventHandler  {

    private static final Logger log = LoggerFactory.getLogger(InboundIdEventHandler.class);

    private IOCardIdMap ids;

    public IOCardIdMap getIdMap() {
        if(ids == null) {
            ids = IOCardIdMap.fromString(getParameterValue("ids"));
        }
        return ids;
    }

    public InboundIdEventHandler(IOCardConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    public InboundIdEventHandler(IOCardConnector connector, IOCardIdMap ioCardIdMap) {
        this(connector, toParameters(
            new AbstractMap.SimpleEntry<>("ids", ioCardIdMap.toString())
        ));
    }

    @Override
    public void handleInboundEvent(Object value) throws IOException {
        for(Map.Entry<Integer, Boolean> entry : getIdMap().entrySet()) {
            getConnector().write(entry.getKey(), entry.getValue());
        }
    }

}
