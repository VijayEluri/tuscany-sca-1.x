package org.apache.tuscany.runtime.webapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import junit.framework.TestCase;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import org.easymock.EasyMock;

/**
 * Verifies {@link org.apache.tuscany.runtime.webapp.TuscanySessionListener} notifies the runtime of session events
 *
 * @version $Rev$ $Date$
 */
public class TuscanySessionListenerTestCase extends TestCase {

    public void testSessionPropagated() throws Exception {
        WebappRuntime runtime = EasyMock.createNiceMock(WebappRuntime.class);
        runtime.sessionCreated(EasyMock.isA(HttpSessionEvent.class));
        runtime.sessionDestroyed(EasyMock.isA(HttpSessionEvent.class));
        EasyMock.replay(runtime);
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        EasyMock.expect(context.getAttribute(RUNTIME_ATTRIBUTE)).andReturn(runtime);
        EasyMock.replay(context);
        HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        EasyMock.replay(session);
        HttpSessionEvent event = new HttpSessionEvent(session);
        TuscanySessionListener listener = new TuscanySessionListener();
        listener.sessionCreated(event);
        listener.sessionDestroyed(event);
        EasyMock.verify(context);
        EasyMock.verify(runtime);
    }

    /**
     * Verifies an error is logged when no runtime is configured
     *
     * @throws Exception
     */
    public void testRuntimeNotConfigured() throws Exception {
        ServletContext context = EasyMock.createNiceMock(ServletContext.class);
        context.log(EasyMock.isA(String.class), EasyMock.isA(ServletException.class));
        EasyMock.replay(context);
        TuscanySessionListener listener = new TuscanySessionListener();
        HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        EasyMock.expect(session.getServletContext()).andReturn(context);
        EasyMock.replay(session);
        HttpSessionEvent event = new HttpSessionEvent(session);
        listener.sessionCreated(event);
        EasyMock.verify(context);
    }

    public void testSessionDestroyedBeforeCreated() throws Exception {
        TuscanySessionListener listener = new TuscanySessionListener();
        listener.sessionDestroyed(null);
    }
}
