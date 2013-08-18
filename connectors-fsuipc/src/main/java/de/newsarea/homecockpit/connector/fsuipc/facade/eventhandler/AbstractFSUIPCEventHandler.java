package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;

import java.util.Map;

public abstract class AbstractFSUIPCEventHandler extends AbstractEventHandler<FSUIPCConnector> {

    private FSUIPCOffset offset;
    private Integer size;

    public FSUIPCOffset getOffset() {
        if(offset == null) {
            offset = FSUIPCOffset.fromString(getParameterValue("offset"));
        }
        return offset;
    }

    public Integer getSize() {
        if(size == null) {
            size = Integer.parseInt(getParameterValue("size"));
        }
        return size;
    }

    public AbstractFSUIPCEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

}
