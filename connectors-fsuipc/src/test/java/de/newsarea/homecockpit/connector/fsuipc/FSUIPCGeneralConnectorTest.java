package de.newsarea.homecockpit.connector.fsuipc;

import de.newsarea.homecockpit.connector.GeneralConnector;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class FSUIPCGeneralConnectorTest {

    private static Logger log = LoggerFactory.getLogger(FSUIPCGeneralConnectorTest.class);
	
	private GeneralConnector<String> generalConnector;
	private FSUIPCConnector fsuipcConnector;
	
	@BeforeMethod
	public void beforeMethod() throws IOException {
        generalConnector = mock(GeneralConnector.class);
		fsuipcConnector = new FSUIPCGeneralConnector(generalConnector);
	}
	
	@Test
	public void shouldMonitor() throws IOException {
		fsuipcConnector.monitor(new OffsetIdent(1, 2));
        verify(generalConnector).write(eq("MONITOR[[0x0001:2]]"));
        fsuipcConnector.monitor(new OffsetIdent(2, 4));
        verify(generalConnector).write(eq("MONITOR[[0x0002:4]]"));
	}
	
	@Test
	public void shouldWrite() throws IOException {
		fsuipcConnector.write(new OffsetItem(1, 2, new byte[] { 4 }));
        verify(generalConnector).write(eq("WRITE[[0x0001:2:0x04]]"));
        fsuipcConnector.write(new OffsetItem[] { new OffsetItem(1, 2, new byte[] { 4 }), new OffsetItem(2, 4, new byte[] { 5 }) });
        verify(generalConnector).write(eq("WRITE[[0x0001:2:0x04][0x0002:4:0x05]]"));
	}

    @Test
    public void shouldRead() throws Exception {
        final List<ValueChangedEventListener> valueChangedEventListeners = new ArrayList<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                valueChangedEventListeners.add((ValueChangedEventListener) invocation.getArguments()[0]);
                return null;
            }
        }).when(generalConnector).addEventListener(any(ValueChangedEventListener.class));
        // ~
        final FSUIPCGeneralConnector fsuipcConnector = new FSUIPCGeneralConnector(generalConnector);
        // ~
        final List<OffsetItem> offsetItems = Collections.synchronizedList(new ArrayList<OffsetItem>());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    offsetItems.add(fsuipcConnector.read(new OffsetIdent(0x0002, 2)));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }).start();
        // ~
        valueChangedEventListeners.get(0).valueChanged("VALUE[0x0002:2:0x0001]");
        Thread.sleep(100);
        assertEquals(1, offsetItems.size());
        assertEquals("0x0002 : 2 : 0x0001", offsetItems.get(0).toString());
    }

    @Test
    public void shouldSendToggleBitMessage() throws Exception {
        // when
        fsuipcConnector.toggleBit(0x0001, 1, (byte) 1);
        // then
        verify(generalConnector).write("TOGGLE[[0x0001:1:0x01]]");
    }

    @Test
    public void shouldSendToggleBitMessageForBitIdx10() throws Exception {
        // when
        fsuipcConnector.toggleBit(0x0001, 1, (byte) 10);
        // then
        verify(generalConnector).write("TOGGLE[[0x0001:1:0x0A]]");
    }

    @Test(expectedExceptions = TimeoutException.class)
    public void shouldThrowReadTimeout() throws Exception {
        fsuipcConnector.read(new OffsetIdent(0x0002, 2));
    }

}
