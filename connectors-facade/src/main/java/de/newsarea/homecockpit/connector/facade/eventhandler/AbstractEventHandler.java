package de.newsarea.homecockpit.connector.facade.eventhandler;

import java.util.Map;

public abstract class AbstractEventHandler<C> implements EventHandler<C> {

    private C connector;
    private Map<String, String> parameters;


    public C getConnector() {
        return this.connector;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /* */

    public AbstractEventHandler(C connector, Map<String, String> parameters) {
        this.connector = connector;
        this.parameters = parameters;
    }

    public String getParameterValue(String key) {
        if(getParameters().containsKey(key)) {
            return getParameters().get(key);
        }
        throw new IllegalArgumentException("key '" + key + "' not found");
    }

    @Override
    public String toString() {
        return "AbstractEventHandler{" +
                "connector=" + connector +
                ", parameters=" + parameters +
                '}';
    }
}
