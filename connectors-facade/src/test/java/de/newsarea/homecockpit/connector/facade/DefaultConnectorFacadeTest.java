package de.newsarea.homecockpit.connector.facade;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.facade.event.InboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEvent;
import de.newsarea.homecockpit.connector.facade.event.OutboundEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class DefaultConnectorFacadeTest {

    private ConnectorFacade connectorFacade;

    @BeforeMethod
    public void beforeMethod() {
        connectorFacade = new DefaultConnectorFacade();
    }

    @Test
    public void shouldHandlePostEvent() throws Exception {
        InboundEventHandler inboundEventHandler = mock(InboundEventHandler.class);
        connectorFacade.registerEventHandler("ELEMENT", "COMPONENT", "STATE", inboundEventHandler);
        // ~
        InboundEvent inboundEvent = new InboundEvent("ELEMENT", "COMPONENT", "STATE", 123456);
        connectorFacade.postEvent(inboundEvent);
        // ~
        verify(inboundEventHandler).handleInboundEvent(inboundEvent.getValue());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotHandlePostEvent() throws Exception {
        InboundEvent inboundEvent = new InboundEvent("ELEMENT2", "COMPONENT", "STATE", 123456);
        connectorFacade.postEvent(inboundEvent);
    }

    @Test
    public void shouldReceiveOutboundEvent() throws Exception {
        final List<ValueChangedEventListener> outboundEventListeners = new ArrayList<>();
        ConnectorEventHandler connectorEventHandler = mock(ConnectorEventHandler.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                outboundEventListeners.add((ValueChangedEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(connectorEventHandler).addConnectorEventHandlerListener(any(ConnectorEventHandlerListener.class));
        connectorFacade.registerEventHandler("ELEMENT", "COMPONENT", "STATE", connectorEventHandler);
        // ~
        OutboundEventListener outboundEventListener = mock(OutboundEventListener.class);
        connectorFacade.addEventListener(outboundEventListener);
        // ~
        OutboundEvent outboundEvent = new OutboundEvent("ELEMENT", "COMPONENT", "STATE", 123456);
        outboundEventListener.outboundEvent(outboundEvent);
        // ~
        verify(outboundEventListener).outboundEvent(outboundEvent);
    }
}
