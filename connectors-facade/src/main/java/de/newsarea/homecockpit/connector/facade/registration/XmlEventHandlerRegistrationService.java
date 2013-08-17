package de.newsarea.homecockpit.connector.facade.registration;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.connector.facade.ConnectorFacade;
import de.newsarea.homecockpit.connector.facade.DefaultConnectorFacade;
import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.ConnectorEventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.EventHandler;
import de.newsarea.homecockpit.connector.facade.eventhandler.InboundEventHandler;
import org.apache.commons.io.IOUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class XmlEventHandlerRegistrationService {

    private static Logger log = LoggerFactory.getLogger(DefaultConnectorFacade.class);

    public enum EventType {
        CONNECTOR,
        INBOUND,
        BOTH
    }

    private ConnectorFacade connectorFacade;

    public XmlEventHandlerRegistrationService(ConnectorFacade connectorFacade) {
        this.connectorFacade = connectorFacade;
    }

    public <C extends Connector<T>, T extends ConnectorEvent> void registerEventHandler(C connector, String mappingResource) {
        InputStream inputStream = null;
        try {
            inputStream = DefaultConnectorFacade.class.getResourceAsStream(mappingResource);
            if(inputStream == null) {
                throw new IllegalArgumentException("can't find resource - " + mappingResource);
            }
            // ~
            try {
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(inputStream);
                Element root = doc.getRootElement();
                String handlerPackage = root.getAttributeValue("handlerPackage");
                for (Object elementObj : root.getChildren()) {
                    Element elementElem = (Element) elementObj;
                    String element = elementElem.getAttributeValue("name");
                    //
                    for (Object stateObj : elementElem.getChildren()) {
                        String handler = null;
                        Map<String, String> params = new HashMap<>();
                        params.put("element", element);
                        //
                        Element stateElem = (Element) stateObj;
                        for (Object attrObj : stateElem.getAttributes()) {
                            Attribute attr = (Attribute) attrObj;
                            String attrName = attr.getName();
                            if ("eventhandler".equalsIgnoreCase(attrName)) {
                                handler = attr.getValue();
                            } else {
                                params.put(attrName, attr.getValue());
                            }
                        }
                        //
                        AbstractEventHandler<C> eventHandler = EventHandlerRegistrationUtils.createEventHandler(connector, handlerPackage, handler, params);
                        // ~
                        EventType eventType;
                        if(params.containsKey("handleEventType")) {
                            eventType = EventType.valueOf(params.get("handleEventType"));
                        } else {
                            eventType = determineEventType(eventHandler);
                        }
                        // ~
                        switch (eventType) {
                            case CONNECTOR:
                                connectorFacade.registerEventHandler(element, params.get("component"), params.get("state"), (ConnectorEventHandler) eventHandler);
                                break;
                            case INBOUND:
                                connectorFacade.registerEventHandler(element, params.get("component"), params.get("state"), (InboundEventHandler) eventHandler);
                                break;
                            case BOTH:
                                connectorFacade.registerEventHandler(element, params.get("component"), params.get("state"), (ConnectorEventHandler) eventHandler);
                                connectorFacade.registerEventHandler(element, params.get("component"), params.get("state"), (InboundEventHandler) eventHandler);
                                break;
                        }
                        log.info("register eventHandler - " + eventHandler);
                    }
                }
            } catch (JDOMException e) {
                log.error(e.getMessage(), e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e.getMessage() ,e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage() ,e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getMessage() ,e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage() ,e);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static EventType determineEventType(EventHandler eventHandler) {
        EventType eventType;
        if(eventHandler instanceof InboundEventHandler && eventHandler instanceof ConnectorEventHandler) {
            eventType = EventType.BOTH;
        } else if(eventHandler instanceof InboundEventHandler) {
            eventType = EventType.INBOUND;
        } else if(eventHandler instanceof ConnectorEventHandler) {
            eventType = EventType.CONNECTOR;
        } else {
            throw new IllegalArgumentException("invalid eventhandler - " + eventHandler);
        }
        return eventType;
    }

}
