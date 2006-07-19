package org.apache.tuscany.core.implementation.system.wire;

import org.apache.tuscany.spi.wire.InboundWire;

/**
 * Specified by a {@link org.apache.tuscany.core.implementation.system.model.SystemBinding}, a specialized inbound wire
 * that returns a direct reference to the target.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SystemInboundWire<T> extends InboundWire<T> {


}
