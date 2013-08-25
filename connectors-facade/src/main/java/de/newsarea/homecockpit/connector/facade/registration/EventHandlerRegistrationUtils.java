package de.newsarea.homecockpit.connector.facade.registration;

import de.newsarea.homecockpit.connector.Connector;
import de.newsarea.homecockpit.connector.event.ConnectorEvent;
import de.newsarea.homecockpit.connector.facade.eventhandler.AbstractEventHandler;
import de.newsarea.homecockpit.connector.facade.registration.util.ClassLoaderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventHandlerRegistrationUtils {

    private static final Logger log = LoggerFactory.getLogger(EventHandlerRegistrationUtils.class);

    private EventHandlerRegistrationUtils() { }

    public static <C extends Connector<T>, T extends ConnectorEvent> AbstractEventHandler<C> createEventHandler(C connector, String packageName, String handler, Map<String, String> params)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        log.debug("creating eventhandler " + packageName + " : " + handler + " : " + params);
        Class<?> clazz = Class.forName(packageName + "." + handler);
        //
        return createEventHandler(connector, clazz, params);
    }

    public static <C extends Connector<T>, T extends ConnectorEvent> AbstractEventHandler<C> createEventHandler(C connector, Class<?> clazz, Map<String, String> params)
            throws InvocationTargetException, IllegalAccessException, InstantiationException {
        log.debug("creating eventhandler " + clazz);
        Constructor<?> constructor =  ClassLoaderHelper.determineConstructorByArgumentTypes(clazz, new Class<?>[] { Connector.class, Map.class });
        if(constructor == null) {
            throw new IllegalArgumentException("can't find contstructor of class - " + clazz);
        }
        // ~
        Map<String, String> parameters = new HashMap<>(params);
        // ~
        List<Object> constructorArguments = new ArrayList<>();
        constructorArguments.add(connector);
        constructorArguments.add(parameters);
        // ~
        try {
            return (AbstractEventHandler<C>) constructor.newInstance(constructorArguments.toArray());
        } catch(IllegalArgumentException ex) {
            throw new IllegalArgumentException("can't create '" + clazz.getName() + "' - " + ex.getMessage());
        }
    }

}
