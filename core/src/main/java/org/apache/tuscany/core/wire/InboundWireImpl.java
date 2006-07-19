package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.util.MethodHashMap;

/**
 * Default implementation of an inbound wire
 *
 * @version $Rev$ $Date$
 */
public class InboundWireImpl<T> implements InboundWire<T> {

    private String serviceName;
    private Class[] businessInterfaces;
    private Map<Method, InboundInvocationChain> invocationChains = new MethodHashMap<InboundInvocationChain>();
    private OutboundWire<T> targetWire;

    @SuppressWarnings("unchecked")
    public T getTargetService() throws TargetException {
        if (targetWire != null) {
            // optimized, no interceptors or handlers on either end
            return targetWire.getTargetService();
        }
        throw new TargetException("Target wire not optimized");
    }

    public void setBusinessInterface(Class interfaze) {
        businessInterfaces = new Class[]{interfaze};
    }

    @SuppressWarnings("unchecked")
    public Class<T> getBusinessInterface() {
        return businessInterfaces[0];
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public Class[] getImplementedInterfaces() {
        return businessInterfaces;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<Method, InboundInvocationChain> getInvocationChains() {
        return invocationChains;
    }

    public void addInvocationChains(Map<Method, InboundInvocationChain> chains) {
        invocationChains.putAll(chains);
    }

    public void addInvocationChain(Method method, InboundInvocationChain chain) {
        invocationChains.put(method, chain);
    }

    public void setTargetWire(OutboundWire<T> wire) {
        targetWire = wire;
    }

    public boolean isOptimizable() {
        for (InboundInvocationChain chain : invocationChains.values()) {
            if (chain.getTargetInvoker() != null && !chain.getTargetInvoker().isOptimizable()) {
                return false;
            }
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
            if (chain.getRequestHandlers() != null && !chain.getRequestHandlers().isEmpty()) {
                for (MessageHandler handler : chain.getRequestHandlers()) {
                    if (!handler.isOptimizable()) {
                        return false;
                    }
                }
            }
            if (chain.getResponseHandlers() != null && !chain.getResponseHandlers().isEmpty()) {
                for (MessageHandler handler : chain.getResponseHandlers()) {
                    if (!handler.isOptimizable()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
