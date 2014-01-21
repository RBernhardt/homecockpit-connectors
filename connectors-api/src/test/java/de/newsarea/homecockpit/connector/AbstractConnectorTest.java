package de.newsarea.homecockpit.connector;

import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.LinkedList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class AbstractConnectorTest {

    @Test
    public void shouldHandleNonBlocking() throws Exception {
        final ValueChangedEventListener[] valueChangedEventListener = new ValueChangedEventListener[1];
        GeneralConnector<String> generalConnector = mock(GeneralConnector.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                valueChangedEventListener[0] = (ValueChangedEventListener<String>) invocation.getArguments()[0];
                return null;
            }
        }).when(generalConnector).addEventListener(any(ValueChangedEventListener.class));
        // ~
        final LinkedList<String> messages = new LinkedList<>();
        new AbstractConnector<ConnectorEvent>(generalConnector) {
            @Override
            protected void handleValueReceived(String s) throws Exception {
                messages.add(s);
                Thread.sleep(100);
            }
        };
        // ~
        Date startDate = new Date();
        valueChangedEventListener[0].valueChanged("0000");
        valueChangedEventListener[0].valueChanged("0001");
        valueChangedEventListener[0].valueChanged("0002");
        valueChangedEventListener[0].valueChanged("0003");
        valueChangedEventListener[0].valueChanged("0004");
        valueChangedEventListener[0].valueChanged("0005");
        valueChangedEventListener[0].valueChanged("0006");
        valueChangedEventListener[0].valueChanged("0007");
        valueChangedEventListener[0].valueChanged("0008");
        valueChangedEventListener[0].valueChanged("0009");
        Date endDate = new Date();
        // ~
        Thread.sleep(2000);
        //
        assertTrue((endDate.getTime() - startDate.getTime()) < 10);
        assertEquals(10, messages.size());
        assertEquals("0000", messages.getFirst());
        assertEquals("0009", messages.getLast());
    }

}
