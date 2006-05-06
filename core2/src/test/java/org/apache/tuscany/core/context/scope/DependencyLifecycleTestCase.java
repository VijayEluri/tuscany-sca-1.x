package org.apache.tuscany.core.context.scope;

import java.util.List;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.HttpSessionEvent;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.model.Scope;

/**
 * Tests that dependencies are initalized and destroyed in the proper order (i.e. LIFO)
 *
 * @version $Rev: 393992 $ $Date: 2006-04-13 18:01:05 -0700 (Thu, 13 Apr 2006) $
 */
public class DependencyLifecycleTestCase extends TestCase {

    public void testInitDestroyOrderModuleScope() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scopeCtx = new ModuleScopeContext(ctx);
        scopeCtx.start();
        //scopeCtx.onEvent(new ModuleStart(this, null));
        List<AtomicContext> contexts = MockFactory.createWiredContexts("source", SourceImpl.class,"target", TargetImpl.class,scopeCtx,scopeCtx);
        Source source = (Source)contexts.get(0).getInstance(null);
        assertNotNull(source.getTarget());
        //scopeCtx.onEvent(new ModuleStop(this, null));


        //scope.registerFactories(MockContextFactory.createWiredContexts(Scope.MODULE,scope));
//        scope.start();
//        scope.onEvent(new ModuleStart(this));
//        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
//        assertNotNull(source.getPojo());
//        // expire module
//        Assert.assertEquals(2,source.getNumberInstantiated());
//        scope.onEvent(new ModuleStop(this));
//        Assert.assertEquals(0,source.getNumberInstantiated());
//        scope.stop();
    }

//    public void testInitDestroyOrderSessionScope() throws Exception {
//        WorkContext ctx = new WorkContextImpl();
//        SessionScopeContext scope = new SessionScopeContext(ctx);
//        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.SESSION,scope));
//        scope.start();
//        Object session =  new Object();
//        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
//        scope.onEvent(new HttpSessionBound(this,session));
//        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
//        assertNotNull(source.getPojo());
//        // expire module
//        Assert.assertEquals(2,source.getNumberInstantiated());
//        scope.onEvent(new HttpSessionEnd(this,session));
//        Assert.assertEquals(0,source.getNumberInstantiated());
//        scope.stop();
//    }
//
//
//    public void testInitDestroyOrderRequestScope() throws Exception {
//        WorkContext ctx = new WorkContextImpl();
//        RequestScopeContext scope = new RequestScopeContext(ctx);
//        scope.registerFactories(MockContextFactory.createWiredContexts(Scope.REQUEST,scope));
//        scope.start();
//        Object request =  new Object();
//        scope.onEvent(new RequestStart(this,request));
//        OrderedDependentPojo source = (OrderedDependentPojo) scope.getContext("source").getInstance(null);
//        assertNotNull(source.getPojo());
//        // expire module
//        Assert.assertEquals(2,source.getNumberInstantiated());
//        scope.onEvent(new RequestEnd(this,request));
//        Assert.assertEquals(0,source.getNumberInstantiated());
//        scope.stop();
//    }

}
