package de.newsarea.homecockpit.connector.facade.event;

abstract class AbstractEvent extends Event {

	private Object value;

    public Object getValue() {
		return value;
	}
	
	public AbstractEvent(String element, String component, String state, Object value) {
        super(element, component, state);
		this.value = value;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEvent)) return false;
        if (!super.equals(o)) return false;

        AbstractEvent that = (AbstractEvent) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

}
