package org.apache.tuscany.core.injection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Injects a value created by an {@link ObjectFactory} using a given method
 * @version $Rev$ $Date$
 */
public class MethodInjector<T> implements Injector<T> {
    private final Method method;
    private final ObjectFactory<?> objectFactory;

    public MethodInjector(Method method, ObjectFactory<?> objectFactory) {
        this.method = method;
        this.objectFactory = objectFactory;
    }

    public void inject(T instance) throws ObjectCreationException {
        try {
            method.invoke(instance, new Object[]{objectFactory.getInstance()});
        } catch (IllegalAccessException e) {
            throw new AssertionError("Method is not accessible [" + method + "]");
        } catch (InvocationTargetException e) {
            throw new ObjectCreationException("Exception thrown by setter [" + method + "]", e);
        }
    }
}
