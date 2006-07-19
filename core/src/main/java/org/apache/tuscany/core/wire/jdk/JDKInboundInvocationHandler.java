package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Receives a request from a proxy and performs an invocation on an {@link org.apache.tuscany.spi.wire.InboundWire} via
 * an {@link InboundInvocationChain}
 *
 * @version $Rev$ $Date$
 */
public class JDKInboundInvocationHandler implements WireInvocationHandler, InvocationHandler {

    /*
     * an association of an operation to chain holder. The holder contains the invocation chain
     * and a local clone of the master TargetInvoker. TargetInvokers will be cloned by the handler and placed in the
     * holder if they are cacheable. This allows optimizations such as avoiding target resolution when a source refers
     * to a target of greater scope since the target reference can be maintained by the invoker. When a target invoker
     * is not cacheable, the master associated with the wire chains will be used.
     */
    private Map<Method, ChainHolder> chains;

    public JDKInboundInvocationHandler(Map<Method, InboundInvocationChain> invocationChains) {
        this.chains = new HashMap<Method, ChainHolder>(invocationChains.size());
        for (Map.Entry<Method, InboundInvocationChain> entry : invocationChains.entrySet()) {
            this.chains.put(entry.getKey(), new ChainHolder(entry.getValue()));
        }
    }

    /**
     * Dispatches a client request made on a proxy
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ChainHolder holder = chains.get(method);
        if (holder == null) {
            TargetException e = new TargetException("Operation not configured");
            e.setIdentifier(method.getName());
            throw e;
        }
        InboundInvocationChain chain = holder.chain;
        TargetInvoker invoker;
        if (holder.cachedInvoker == null) {
            assert chain != null;
            if (chain.getTargetInvoker() == null) {
                TargetException e = new TargetException("No target invoker configured for operation");
                e.setIdentifier(chain.getMethod().getName());
                throw e;
            }
            if (chain.getTargetInvoker().isCacheable()) {
                // clone and store the invoker locally
                holder.cachedInvoker = (TargetInvoker) chain.getTargetInvoker().clone();
                invoker = holder.cachedInvoker;
            } else {
                invoker = chain.getTargetInvoker();
            }
        } else {
            assert chain != null;
            invoker = chain.getTargetInvoker();
        }
        MessageChannel requestChannel = chain.getRequestChannel();
        MessageChannel responseChannel = chain.getResponseChannel();
        Interceptor headInterceptor = chain.getHeadInterceptor();
        if (requestChannel == null && headInterceptor == null && responseChannel == null) {
            try {
                // short-circuit the dispatch and invoke the target directly
                if (chain.getTargetInvoker() == null) {
                    throw new AssertionError("No target invoker [" + method.getName() + "]");
                }
                return chain.getTargetInvoker().invokeTarget(args);
            } catch (InvocationTargetException e) {
                // the cause was thrown by the target so throw it
                throw e.getCause();
            }
        } else {
            Message msg = new MessageImpl();
            msg.setTargetInvoker(invoker);
            msg.setBody(args);
            Message resp;
            if (requestChannel != null) {
                requestChannel.send(msg);
                resp = msg.getRelatedCallbackMessage();
                if (responseChannel != null) {
                    responseChannel.send(resp);
                }
            } else {
                if (headInterceptor == null) {
                    throw new TargetException("Expected interceptor on target chain");
                }
                // dispatch the wire down the chain and get the response
                resp = headInterceptor.invoke(msg);
                if (responseChannel != null) {
                    responseChannel.send(resp);
                }
            }
            Object body = resp.getBody();
            if (body instanceof Throwable) {
                throw (Throwable) body;
            }
            return body;
        }
    }


    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }

    /**
     * A holder used to associate an wire chain with a local copy of a target invoker that was previously cloned from
     * the chain master
     */
    private class ChainHolder {

        InboundInvocationChain chain;
        TargetInvoker cachedInvoker;

        public ChainHolder(InboundInvocationChain config) {
            this.chain = config;
        }

    }

}
