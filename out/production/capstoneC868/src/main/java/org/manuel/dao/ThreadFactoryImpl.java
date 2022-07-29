package org.manuel.dao;

import java.util.concurrent.ThreadFactory;

/**
 * @author Manuel Fuentes
 */
public class ThreadFactoryImpl implements ThreadFactory {
    public ThreadFactoryImpl() {
    }

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param runnable a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     * create a thread is rejected
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread (runnable);
        t.setName("DBTread");
        t.setDaemon(true);
        return t;
    }
}
