package de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler;

import de.newsarea.homecockpit.connector.event.ConnectorEventHandlerListener;
import de.newsarea.homecockpit.connector.event.ValueChangedEventListener;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import de.newsarea.homecockpit.connector.fsuipc.FSUIPCConnector;
import de.newsarea.homecockpit.connector.fsuipc.event.FSUIPCConnectorEvent;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.converter.ValueConverter;
import de.newsarea.homecockpit.connector.fsuipc.facade.eventhandler.domain.FSUIPCOffset;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ValueEventHandler extends AbstractFSUIPCEventHandler implements InboundEventHandler, ConnectorEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ValueEventHandler.class);

    private EventListenerSupport<ConnectorEventHandlerListener> eventListeners;

    private ByteArray value;
    private ValueConverter<Long, Number> valueConverter;

    private ByteArray getValue() {
        if(value == null && getParameters().containsKey("value")) {
            this.value = ByteArray.create(getParameterValue("value"), getSize());
        }
        return value;
    }

    public ValueConverter<Long, Number> getValueConverter() {
        if (valueConverter == null && getParameters().containsKey("valueConverter")) {
            log.debug("loading value converter - " + valueConverter);
            try {
                this.valueConverter = ValueEventHandler.createValueConverterInstance(getParameterValue("valueConverter"));
                log.debug("value converter loaded ... " + valueConverter);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return valueConverter;
    }

    public ValueEventHandler(FSUIPCConnector connector, Map<String, String> parameters) {
        super(connector, parameters);
    }

    public ValueEventHandler(FSUIPCConnector connector, FSUIPCOffset offset, int size) {
        this(connector,
                toParameters(
                    new AbstractMap.SimpleEntry<>("offset", offset.toHexString()),
                    new AbstractMap.SimpleEntry<>("size", String.valueOf(size))
                )
        );
    }

    public ValueEventHandler(FSUIPCConnector connector, FSUIPCOffset offset, int size, String valueConverter) {
        this(connector,
                toParameters(
                    new AbstractMap.SimpleEntry<>("offset", offset.toHexString()),
                    new AbstractMap.SimpleEntry<>("size", String.valueOf(size)),
                    new AbstractMap.SimpleEntry<>("valueConverter", valueConverter)
                )
        );
    }

	public void handleConnectorEvent(FSUIPCConnectorEvent connectorEvent) {
        if(getOffset().getValue() != connectorEvent.getOffset()) { return; }
        if(getSize() != connectorEvent.getSize()) { return; }
        // ~
        Number value;
        if(getValueConverter() != null) {
            log.debug("convert value - " + connectorEvent + " - with " + getValueConverter());
            try {
                value = getValueConverter().toInput(connectorEvent.getValue().toLong());
            } catch(Exception ex) {
                log.error("converter error with event - {}", connectorEvent);
                throw ex;
            }
        } else {
            value = connectorEvent.getValue().toNumber(connectorEvent.getSize());
        }
        // ~
        if(getValue() != null) {
            ByteArray tObjValue = connectorEvent.getValue();
            if(!tObjValue.equals(getValue())) {
                return;
            }
        }
        //
        eventListeners.fire().valueChanged(getValue() == null ? value : null);
	}

    public void handleInboundEvent(Object value) {
        ByteArray outputValue = getValue();
        if(outputValue == null && value != null) {
            if(getValueConverter() == null) {
                outputValue = ByteArray.create(value.toString(), getSize());
            } else {
                outputValue = ByteArray.create(value.toString(), 8);
                Long convertedValue = getValueConverter().toOutput(outputValue.toLong());
                outputValue = ByteArray.create(convertedValue.toString(), getSize());
            }
        }
        // validate output value
        if(outputValue == null) {
            throw new IllegalArgumentException("defined value or event value expected");
        }
        //
        try {
            OffsetItem offsetItem = new OffsetItem(getOffset().getValue(), getSize(), outputValue);
            log.info("SEND {}", offsetItem);
            getConnector().write(offsetItem);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void addConnectorEventHandlerListener(ConnectorEventHandlerListener<Object> connectorEventHandlerListener) {
        if(eventListeners == null) {
            eventListeners = EventListenerSupport.create(ConnectorEventHandlerListener.class);
            try {
                getConnector().monitor(new OffsetIdent(getOffset().getValue(), getSize()));
                getConnector().addEventListener(new ValueChangedEventListener<FSUIPCConnectorEvent>() {
                    @Override
                    public void valueChanged(FSUIPCConnectorEvent event) {
                        handleConnectorEvent(event);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        eventListeners.addListener(connectorEventHandlerListener);
    }

    @Override
    public void queueLastEvent() {
        try {
            OffsetItem offsetItem = getConnector().read(new OffsetIdent(getOffset().getValue(), getSize()));
            FSUIPCConnectorEvent fsuipcConnectorEvent = FSUIPCConnectorEvent.from(offsetItem);
            handleConnectorEvent(fsuipcConnectorEvent);
        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static <I,O> ValueConverter<I, O> createValueConverterInstance(String valueConverter)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String packageName = ValueConverter.class.getPackage().getName();
        Class<?> clazz = Class.forName(packageName + "." + valueConverter);
        return (ValueConverter<I, O>) clazz.newInstance();
    }

}
