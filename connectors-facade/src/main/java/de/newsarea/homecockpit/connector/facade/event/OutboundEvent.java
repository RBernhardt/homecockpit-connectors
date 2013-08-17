package de.newsarea.homecockpit.connector.facade.event;

public class OutboundEvent extends AbstractEvent {

    public OutboundEvent(String element, String component, String state, Object value) {
        super(element, component, state, value);
    }

    @Override
    public String toString() {
        return "OutboundEvent{" +
                "element='" + getElement() + '\'' +
                ", component='" + getComponent() + '\'' +
                ", state='" + getState() + '\'' +
                ", value=" + getValue() +
                '}';
    }

}
