package de.newsarea.homecockpit.connector.facade.event;

public class InboundEvent extends AbstractEvent {

    public InboundEvent(String element, String component, String state, Object value) {
        super(element, component, state, value);
    }

    @Override
    public String toString() {
        return "InboundEvent{" +
                "element='" + getElement() + '\'' +
                ", component='" + getComponent() + '\'' +
                ", state='" + getState() + '\'' +
                ", value=" + getValue() +
                '}';
    }

}
