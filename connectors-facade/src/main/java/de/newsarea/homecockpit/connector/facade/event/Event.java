package de.newsarea.homecockpit.connector.facade.event;

public class Event {

    private String element;
    private String component;
    private String state;

    public String getElement() {
        return element;
    }

    public String getComponent() {
        return component;
    }

    public String getState() {
        return state;
    }

    public Event(String element, String component, String state) {
        this.element = element;
        this.component = component;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (!component.equals(event.component)) return false;
        if (!element.equals(event.element)) return false;
        if (state != null ? !state.equals(event.state) : event.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = element.hashCode();
        result = 31 * result + component.hashCode();
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "element='" + element + '\'' +
                ", component='" + component + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

}
