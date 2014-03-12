package de.newsarea.homecockpit.connector.facade.registration;

import de.newsarea.homecockpit.connector.facade.ConnectorFacade;
import de.newsarea.homecockpit.connector.facade.DefaultConnectorFacade;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class FSUIPCEventHandlerRegistrationTest {

    @Test
    public void shouldRegisterEventHandler() throws Exception {
        FSUIPCConnector fsuipcConnector = mock(FSUIPCConnector.class);
        ConnectorFacade connectorFacade = new DefaultConnectorFacade();
        //
        XmlEventHandlerRegistrationService xmlEventHandlerRegistrationService = new XmlEventHandlerRegistrationService(connectorFacade);
        xmlEventHandlerRegistrationService.registerEventHandler(fsuipcConnector, "/de/newsarea/homecockpit/connector/fsuipc/general.xml");
    }
}
