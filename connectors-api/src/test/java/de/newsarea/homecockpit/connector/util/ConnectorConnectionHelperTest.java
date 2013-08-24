package de.newsarea.homecockpit.connector.util;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.event.ConnectorEventListener;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class ConnectorConnectionHelperTest {

    private Connector connector;

    @BeforeMethod
    public void setUp() {
        connector = mock(Connector.class);
    }

    @Test
    public void shouldConnectedBlocked() throws Exception {
        final List<ConnectorEventListener.State> stateChangedList = new ArrayList<>();
        ConnectorConnectionHelper connectorConnectionHelper = new ConnectorConnectionHelper(connector);
        connectorConnectionHelper.addEventListener(new ConnectorEventListener() {
            @Override
            public void stateChanged(Connector connector, State state) {
                stateChangedList.add(state);
            }
        });
        connectorConnectionHelper.open();
        // ~
        verify(connector).open();
        assertEquals(ConnectorEventListener.State.OPEN, stateChangedList.get(0));
        assertEquals(ConnectorEventListener.State.CONNECTED, stateChangedList.get(1));
    }

    @Test
    public void shouldConnectedAsync() throws Exception {
        final List<ConnectorEventListener.State> stateChangedList = new ArrayList<>();
        ConnectorConnectionHelper connectorConnectionHelper = new ConnectorConnectionHelper(connector);
        connectorConnectionHelper.addEventListener(new ConnectorEventListener() {
            @Override
            public void stateChanged(Connector connector, State state) {
                stateChangedList.add(state);
            }
        });
        connectorConnectionHelper.open(200);
        Thread.sleep(100);
        // ~
        verify(connector).open();
        assertEquals(2, stateChangedList.size());
        assertEquals(ConnectorEventListener.State.OPEN, stateChangedList.get(0));
        assertEquals(ConnectorEventListener.State.CONNECTED, stateChangedList.get(1));
    }

    @Test
    public void shouldNotConnectedAsync() throws Exception {
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws InterruptedException, ConnectException {
                throw new ConnectException("");
            }
        }).when(connector).open();
        // ~
        final List<ConnectorEventListener.State> stateChangedList = new ArrayList<>();
        ConnectorConnectionHelper connectorConnectionHelper = new ConnectorConnectionHelper(connector);
        connectorConnectionHelper.addEventListener(new ConnectorEventListener() {
            @Override
            public void stateChanged(Connector connector, State state) {
                stateChangedList.add(state);
            }
        });
        connectorConnectionHelper.open(200);
        Thread.sleep(100);
        // ~
        verify(connector).open();
        assertEquals(1, stateChangedList.size());
        assertEquals(ConnectorEventListener.State.OPEN, stateChangedList.get(0));
    }

    @Test
    public void shouldClosed() throws ConnectException, InterruptedException {
        final List<ConnectorEventListener.State> stateChangedList = new ArrayList<>();
        ConnectorConnectionHelper connectorConnectionHelper = new ConnectorConnectionHelper(connector);
        connectorConnectionHelper.addEventListener(new ConnectorEventListener() {
            @Override
            public void stateChanged(Connector connector, State state) {
                stateChangedList.add(state);
            }
        });
        connectorConnectionHelper.open();
        connectorConnectionHelper.close();
        // ~
        assertEquals(3, stateChangedList.size());
        assertEquals(ConnectorEventListener.State.OPEN, stateChangedList.get(0));
        assertEquals(ConnectorEventListener.State.CONNECTED, stateChangedList.get(1));
        assertEquals(ConnectorEventListener.State.CLOSED, stateChangedList.get(2));
    }

    @Test
    public void shouldClosedAsync() throws ConnectException, InterruptedException {
        final List<ConnectorEventListener.State> stateChangedList = new ArrayList<>();
        ConnectorConnectionHelper connectorConnectionHelper = new ConnectorConnectionHelper(connector);
        connectorConnectionHelper.addEventListener(new ConnectorEventListener() {
            @Override
            public void stateChanged(Connector connector, State state) {
                stateChangedList.add(state);
            }
        });
        connectorConnectionHelper.open(200);
        Thread.sleep(100);
        connectorConnectionHelper.close();
        // ~
        assertEquals(3, stateChangedList.size());
        assertEquals(ConnectorEventListener.State.OPEN, stateChangedList.get(0));
        assertEquals(ConnectorEventListener.State.CONNECTED, stateChangedList.get(1));
        assertEquals(ConnectorEventListener.State.CLOSED, stateChangedList.get(2));
    }

}
