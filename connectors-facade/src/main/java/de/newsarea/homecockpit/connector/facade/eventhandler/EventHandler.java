package de.newsarea.homecockpit.connector.facade.eventhandler;

import java.util.Map;

public interface EventHandler<C> {

    C getConnector();
    Map<String, String> getParameters();

}
