package de.newsarea.homecockpit.connector.facade.eventhandler;

import java.io.IOException;

public interface InboundEventHandler {

    void handleInboundEvent(Object value) throws IOException;

}
