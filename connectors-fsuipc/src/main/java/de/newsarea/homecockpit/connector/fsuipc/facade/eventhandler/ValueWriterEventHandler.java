package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;

public class ValueWriterEventHandler extends AbstractFSUIPCEventHandler implements InboundEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ValueWriterEventHandler.class);

    private ByteArray value;
    private Boolean waitForResetToZero;

    private ByteArray getValue() {
        if(value == null) {
            this.value = ByteArray.create(getParameterValue("value"), getSize());
        }
        return value;
    }

    private boolean isWaitForResetToZero() {
        if(waitForResetToZero == null) {
            if(getParameters().containsKey("waitForResetToZero")) {
                this.waitForResetToZero = Boolean.valueOf(getParameterValue("waitForResetToZero"));
            } else {
                this.waitForResetToZero = false;
            }
        }
        return waitForResetToZero;
    }

    public ValueWriterEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    public ValueWriterEventHandler(FSUIPCConnector connector, FSUIPCOffset offset, int size, boolean waitForResetToZero) {
        this(connector,
            toParameters(
                new AbstractMap.SimpleEntry<>("offset", offset.toHexString()),
                new AbstractMap.SimpleEntry<>("size", String.valueOf(size)),
                new AbstractMap.SimpleEntry<>("waitForResetToZero", String.valueOf(waitForResetToZero))
            )
        );
    }

    public void handleInboundEvent(Object value) {
        if(value != null) {
            log.warn("ignore inbound value - {}", value);
        }
        //
        OffsetItem item = new OffsetItem(getOffset().getValue(), getSize(), getValue());
        try {
            if(isWaitForResetToZero()) {
                getConnector().writeAndWait(item);
            } else {
                getConnector().write(item);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
