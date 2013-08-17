package de.newsarea.homecockpit.connector.facade.registration;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.facade.ConnectorFacade;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class XmlEventHandlerRegistrationServiceTest {

    private Connector connector;
    private ConnectorFacade connectorFacade;
    private XmlEventHandlerRegistrationService xmlEventHandlerRegistrationService;

    @BeforeMethod
    public void beforeMethod() {
        connector = mock(Connector.class);
        connectorFacade = mock(ConnectorFacade.class);
        xmlEventHandlerRegistrationService = new XmlEventHandlerRegistrationService(connectorFacade);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotFindResource() throws Exception {
        xmlEventHandlerRegistrationService.registerEventHandler(connector, "notfound.xml");
    }

    @Test
    public void shouldRegisterEventHandler() throws Exception {
        xmlEventHandlerRegistrationService.registerEventHandler(connector, "/de/newsarea/homecockpit/connector/facade/registration/min-registration.xml");
        verify(connectorFacade).registerEventHandler(eq("MAIN"), eq("TEST"), eq("INBOUND"), any(InboundEventHandler.class));
        verify(connectorFacade).registerEventHandler(eq("MAIN"), eq("TEST"), eq("CONNECTOR"), any(ConnectorEventHandler.class));
        verify(connectorFacade).registerEventHandler(eq("MAIN"), eq("TEST"), eq("INBOUND"), any(InboundEventHandler.class));
        verify(connectorFacade).registerEventHandler(eq("MAIN"), eq("TEST"), eq("CONNECTOR"), any(ConnectorEventHandler.class));
    }

    @Test
    public void shouldRegisterEventHandlerByHandleEventType() throws Exception {
        xmlEventHandlerRegistrationService.registerEventHandler(connector, "/de/newsarea/homecockpit/connector/facade/registration/handleEventType-registration.xml");
        verify(connectorFacade).registerEventHandler(eq("MAIN"), eq("TEST"), eq("INBOUND"), any(InboundEventHandler.class));
        verify(connectorFacade).registerEventHandler(eq("MAIN"), eq("TEST"), eq("CONNECTOR"), any(ConnectorEventHandler.class));
    }

}
