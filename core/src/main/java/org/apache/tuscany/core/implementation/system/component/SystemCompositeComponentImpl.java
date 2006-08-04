package org.apache.tuscany.core.implementation.system.component;

import java.util.Map;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.component.CompositeComponent;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.component.AutowireResolutionException;
import org.apache.tuscany.core.implementation.composite.AbstractCompositeComponent;

/**
 * Implements an composite context for system components. In addition, it implements an autowire policy A system context
 * may contain child composite contexts but an entry point in a child context will only be outwardly accessible if there
 * is an entry point that exposes it configured in the top-level system context.
 *
 * @version $Rev$ $Date$
 */
public class SystemCompositeComponentImpl<S> extends AbstractCompositeComponent<S>
    implements SystemCompositeComponent<S> {

    public SystemCompositeComponentImpl(String name,
                                        CompositeComponent parent,
                                        AutowireComponent autowireContext,
                                        Map<String, Document> propertyValues) {
        super(name, parent, autowireContext, propertyValues);
    }

    public <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance)
        throws ObjectRegistrationException {
        register(new SystemSingletonAtomicComponent<S, I>(name, this, service, instance));
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (instanceInterface.isAssignableFrom(SystemCompositeComponent.class)) {
            return instanceInterface.cast(this);
        } else {
            return super.resolveInstance(instanceInterface);
        }
    }

}
