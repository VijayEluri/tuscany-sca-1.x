package org.apache.tuscany.spi.extension;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;

/**
 * An extension point for component builders. When adding support for new component types, implementations may extend
 * this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {

    protected BuilderRegistry builderRegistry;
    protected ScopeRegistry scopeRegistry;
    protected WireService wireService;
    protected WorkScheduler workScheduler;
    protected WorkContext workContext;
    protected PolicyBuilderRegistry policyBuilderRegistry;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Autowire
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    @Autowire
    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    @Autowire
    public void setPolicyBuilderRegistry(PolicyBuilderRegistry registry) {
        policyBuilderRegistry = registry;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(getImplementationType(), this);
    }

    protected abstract Class<I> getImplementationType();
}
