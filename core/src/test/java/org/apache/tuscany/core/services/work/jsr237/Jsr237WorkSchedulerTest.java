package org.apache.tuscany.core.services.work.jsr237;

import org.apache.tuscany.spi.services.work.NotificationListener;
import org.apache.tuscany.spi.services.work.WorkScheduler;

import commonj.work.WorkManager;
import junit.framework.TestCase;
import org.apache.tuscany.core.services.work.jsr237.workmanager.ThreadPoolWorkManager;

public class Jsr237WorkSchedulerTest extends TestCase {

    /*
     * Test method for 'org.apache.tuscany.core.services.work.jsr237.Jsr237WorkScheduler.scheduleWork(T) <T>'
     */
    public void testScheduleWorkT() {


        WorkManager workManager = new ThreadPoolWorkManager(1);
        WorkScheduler workScheduler = new Jsr237WorkScheduler(workManager);

        workScheduler.scheduleWork(new MyRunnable(), new MyNotificationListener());

    }

    /*
     * Test method for 'org.apache.tuscany.core.services.work.jsr237.Jsr237WorkScheduler.scheduleWork(T,
     * NotificationListener<T>) <T>'
     */
    public void testScheduleWorkTNotificationListenerOfT() {

    }

    private class MyRunnable implements Runnable {
        public void run() {
            System.err.println("Test executed");
        }
    }

    private class MyNotificationListener implements NotificationListener<MyRunnable> {

        public void workAccepted(MyRunnable work) {
            System.err.println("Work accepted");
        }

        public void workCompleted(MyRunnable work) {
            System.err.println("Work completed");
        }

        public void workStarted(MyRunnable work) {
            System.err.println("Work started");
        }

        public void workRejected(MyRunnable work) {
            System.err.println("Work rejected");
        }

        public void workFailed(MyRunnable work, Throwable error) {
            System.err.println("Work failed");
        }

    }

}
