package de.newsarea.homecockpit.connector.facade.event;

abstract class AbstractEvent implements Event {

    private String element;
    private String component;
	private String state;
	private Object value;

    public String getElement() {
        return element;
    }

    public String getComponent() {
        return component;
    }

    public String getState() {
		return state;
	}

    public String getEventSignature() {
        return element + "_" + component + "_" + state;
    }

    public Object getValue() {
		return value;
	}
	
	public AbstractEvent(String element, String component, String state, Object value) {
		this.element = element;
        this.component = component;
		this.state = state;
		this.value = value;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEvent abstractEvent = (AbstractEvent) o;

        if (component != null ? !component.equals(abstractEvent.component) : abstractEvent.component != null) return false;
        if (element != null ? !element.equals(abstractEvent.element) : abstractEvent.element != null) return false;
        if (state != null ? !state.equals(abstractEvent.state) : abstractEvent.state != null) return false;
        if (value != null ? !value.equals(abstractEvent.value) : abstractEvent.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = element != null ? element.hashCode() : 0;
        result = 31 * result + (component != null ? component.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AbstractEvent{" +
                "element='" + element + '\'' +
                ", component='" + component + '\'' +
                ", state='" + state + '\'' +
                ", value=" + value +
                '}';
    }

}
