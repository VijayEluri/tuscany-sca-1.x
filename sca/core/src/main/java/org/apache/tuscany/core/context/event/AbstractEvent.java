package org.apache.tuscany.core.context.event;

/**
 * Represents an abstract event
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AbstractEvent implements Event{

    protected transient Object source;

    public AbstractEvent(Object source) {
        assert (source !=null): "Source id was null";
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
