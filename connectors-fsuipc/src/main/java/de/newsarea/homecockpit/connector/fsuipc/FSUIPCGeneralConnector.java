package de.newsarea.homecockpit.connector.fsuipc;

import de.newsarea.homecockpit.connector.AbstractConnector;
import de.newsarea.homecockpit.connector.GeneralConnector;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FSUIPCGeneralConnector extends AbstractConnector<FSUIPCConnectorEvent> implements FSUIPCConnector {

    public enum MessageType {
        MONITOR,
        WRITE,
        READ,
        CHANGED,
        TOGGLE,
        VALUE
    }

	private static final String REGEX_MAIN = "([A-Z]+)\\[(.*)\\]";
    private static final String REGEX_ITEM = "0x([A-F0-9]{1,4}):([0-9]+)(?::0x((?:[A-F0-9][A-F0-9])+))?";
	
	private static Logger log = LoggerFactory.getLogger(FSUIPCGeneralConnector.class);

    private Map<String, OffsetItem> receivedValues;
	
	private final Semaphore mutex = new Semaphore(0);

    public FSUIPCGeneralConnector(GeneralConnector generalConnector) {
        super(generalConnector);
        receivedValues = new HashMap<>();
    }

    @Override
    protected void handleValueReceived(String message) throws Exception {
        Entry<MessageType, Collection<OffsetItem>> messagePair = toMessagePair(message);
        if(messagePair == null) {
            throw new IllegalArgumentException("invalid message - " + message);
        }
        Collection<OffsetItem> offsetItems = messagePair.getValue();
        switch (messagePair.getKey()) {
            case CHANGED:
                for(OffsetItem offsetItem : offsetItems) {
                    receivedValues.put(offsetItem.getIdentifier(), offsetItem);
                    fireEvent(FSUIPCConnectorEvent.from(offsetItem));
                }
                break;
            case VALUE:
                for(OffsetItem offsetItem : offsetItems) {
                    receivedValues.put(offsetItem.getIdentifier(), offsetItem);
                }
                mutex.release();
                break;
            default:
                throw new IllegalArgumentException("invalid message - " + message);
        }
    }

    public void monitor(OffsetIdent offsetIdent) throws IOException {
        StringBuilder monitorMessage = new StringBuilder();
        monitorMessage.append(MessageType.MONITOR.toString());
        monitorMessage.append(toString(new OffsetIdent[]{offsetIdent}));
        generalConnector.write(monitorMessage.toString());
    }

    public OffsetItem read(OffsetIdent offsetIdent) throws TimeoutException {
        try {
            StringBuilder monitorMessage = new StringBuilder();
            monitorMessage.append(MessageType.READ.toString());
            monitorMessage.append(toString(new OffsetIdent[] { offsetIdent }));
            generalConnector.write(monitorMessage.toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        // ~
        for(int i=0; i < 5; i++) {
            try {
                if(mutex.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                    if(receivedValues.containsKey(offsetIdent.getIdentifier())) {
                        return receivedValues.get(offsetIdent.getIdentifier());
                    }
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        throw new TimeoutException();
    }

    public void write(OffsetItem[] offsetItems) throws IOException {
        StringBuilder writeMessage = new StringBuilder();
        writeMessage.append(MessageType.WRITE.toString());
        writeMessage.append(toString(offsetItems));
        generalConnector.write(writeMessage.toString());
    }

    public void write(OffsetItem offsetItem) throws IOException {
        write(new OffsetItem[]{offsetItem});
    }

    @Override
    public void toggleBit(int offset, int size, byte bitIdx) throws IOException {
        StringBuilder writeMessage = new StringBuilder();
        writeMessage.append(MessageType.TOGGLE.toString());
        writeMessage.append(toString(new OffsetItem[] { new OffsetItem(offset, size, new byte[] { bitIdx } )}));
        generalConnector.write(writeMessage.toString());
    }

    @Override
    public String toString() {
        return "FSUIPCGeneralConnector{" +
                "receivedValues=" + receivedValues +
                '}';
    }

    /* HELPER */

	private Entry<MessageType, Collection<OffsetItem>> toMessagePair(String value) {
		Pattern p = Pattern.compile(REGEX_MAIN);
		Matcher m = p.matcher(value);
		while(m.find()) {
            MessageType messageType = MessageType.valueOf(m.group(1));
            Collection<OffsetItem> offsetItems = toOffsetItems(m.group(2));
			return new AbstractMap.SimpleEntry<>(messageType, offsetItems);
		}
		return null;
	}

    private static String toString(OffsetIdent[] offsetIdents) {
        StringBuilder strBld = new StringBuilder();
        strBld.append("[");
        for(OffsetIdent offsetIdent : offsetIdents) {
            strBld.append("[");
            strBld.append(ByteArray.create(String.valueOf(offsetIdent.getOffset()), 2).toHexString());
            strBld.append(":");
            strBld.append(offsetIdent.getSize());
            strBld.append("]");
        }
        strBld.append("]");
        return strBld.toString();
    }

    private static String toString(OffsetItem[] offsetItems) {
        StringBuilder strBld = new StringBuilder();
        strBld.append("[");
        for(OffsetItem offsetItem : offsetItems) {
            strBld.append("[");
            strBld.append(ByteArray.create(String.valueOf(offsetItem.getOffset()), 2).toHexString());
            strBld.append(":");
            strBld.append(offsetItem.getSize());
            strBld.append(":");
            strBld.append(offsetItem.getValue().toHexString());
            strBld.append("]");
        }
        strBld.append("]");
        return strBld.toString();
    }

    private static Collection<OffsetItem> toOffsetItems(String message) {
        List<OffsetItem> items = new ArrayList<>();
        Pattern p = Pattern.compile(REGEX_ITEM);
        Matcher m = p.matcher(message);
        while(m.find()) {
            int offset = Integer.parseInt(m.group(1), 16);
            int size = Integer.parseInt(m.group(2));
            // ~
            String byteArrayHex = m.group(3);
            ByteArray byteArray = ByteArray.create(new BigInteger(byteArrayHex, 16), byteArrayHex.length() / 2);
            items.add(new OffsetItem(offset, size, byteArray));
        }
        return items;
    }

}
