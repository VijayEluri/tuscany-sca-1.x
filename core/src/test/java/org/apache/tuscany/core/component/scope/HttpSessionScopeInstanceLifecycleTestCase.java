package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.mock.component.OrderedEagerInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.apache.tuscany.core.mock.component.RequestScopeDestroyOnlyComponent;
import org.apache.tuscany.core.mock.component.RequestScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.RequestScopeInitOnlyComponent;
import org.apache.tuscany.core.mock.factories.MockFactory;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class HttpSessionScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent initDestroyContext = MockFactory.createAtomicComponent("InitDestroy",
            scope,
            RequestScopeInitDestroyComponent.class);
        initDestroyContext.start();

        SystemAtomicComponent initOnlyContext = MockFactory.createAtomicComponent("InitOnly",
            scope,
            RequestScopeInitOnlyComponent.class);
        initOnlyContext.start();

        SystemAtomicComponent destroyOnlyContext = MockFactory.createAtomicComponent("DestroyOnly",
            scope,
            RequestScopeDestroyOnlyComponent.class);
        destroyOnlyContext.start();

        Object session = new Object();
        ctx.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session);
        scope.onEvent(new HttpSessionStart(this, session));
        RequestScopeInitDestroyComponent initDestroy =
            (RequestScopeInitDestroyComponent) scope.getInstance(initDestroyContext);
        Assert.assertNotNull(initDestroy);

        RequestScopeInitOnlyComponent initOnly =
            (RequestScopeInitOnlyComponent) scope.getInstance(initOnlyContext);
        Assert.assertNotNull(initOnly);

        RequestScopeDestroyOnlyComponent destroyOnly =
            (RequestScopeDestroyOnlyComponent) scope.getInstance(destroyOnlyContext);
        Assert.assertNotNull(destroyOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());
        Assert.assertFalse(destroyOnly.isDestroyed());

        // expire module
        scope.onEvent(new HttpSessionEnd(this, session));

        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneCtx =
            MockFactory.createAtomicComponent("one", scope, OrderedInitPojoImpl.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx =
            MockFactory.createAtomicComponent("two", scope, OrderedInitPojoImpl.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx =
            MockFactory.createAtomicComponent("three", scope, OrderedInitPojoImpl.class);
        scope.register(threeCtx);

        Object session = new Object();
        ctx.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session);
        scope.onEvent(new HttpSessionStart(this, session));
        OrderedInitPojo one = (OrderedInitPojo) scope.getInstance(oneCtx);
        Assert.assertNotNull(one);
        Assert.assertEquals(1, one.getNumberInstantiated());
        Assert.assertEquals(1, one.getInitOrder());

        OrderedInitPojo two = (OrderedInitPojo) scope.getInstance(twoCtx);
        Assert.assertNotNull(two);
        Assert.assertEquals(2, two.getNumberInstantiated());
        Assert.assertEquals(2, two.getInitOrder());

        OrderedInitPojo three = (OrderedInitPojo) scope.getInstance(threeCtx);
        Assert.assertNotNull(three);
        Assert.assertEquals(3, three.getNumberInstantiated());
        Assert.assertEquals(3, three.getInitOrder());

        // expire module
        scope.onEvent(new HttpSessionEnd(this, session));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(ctx);
        scope.start();

        SystemAtomicComponent oneCtx =
            MockFactory.createAtomicComponent("one", scope, OrderedEagerInitPojo.class);
        scope.register(oneCtx);
        SystemAtomicComponent twoCtx =
            MockFactory.createAtomicComponent("two", scope, OrderedEagerInitPojo.class);
        scope.register(twoCtx);
        SystemAtomicComponent threeCtx =
            MockFactory.createAtomicComponent("three", scope, OrderedEagerInitPojo.class);
        scope.register(threeCtx);

        Object session = new Object();
        ctx.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session);
        scope.onEvent(new HttpSessionStart(this, session));
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getInstance(oneCtx);
        Assert.assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getInstance(twoCtx);
        Assert.assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getInstance(threeCtx);
        Assert.assertNotNull(three);

        // expire module
        scope.onEvent(new HttpSessionEnd(this, session));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

}
