package de.newsarea.homecockpit.connector.fsuipc;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface FSUIPCConnector extends Connector<FSUIPCConnectorEvent> {

    boolean isConnectionEstablished();

    void monitor(OffsetIdent offsetIdent) throws IOException;

    void write(OffsetItem offsetItem) throws IOException;

    OffsetItem read(OffsetIdent offsetIdent) throws IOException;

}
