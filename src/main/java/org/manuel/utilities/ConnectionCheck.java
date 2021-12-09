package org.manuel.utilities;

import org.manuel.Main;
import org.manuel.uicontrols.AlertBoxes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.*;
/**
 * @author Manuel Fuentes
 */
public class ConnectionCheck {
    private final static ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public static void checkConnection() {
        Runnable checker = () -> {
            try {
                if (!Main.getConn().isValid(2)) {
                    System.out.println("Connection Lost");
                } else {
                    System.out.println("Connection Good");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(checker, 10, 10, SECONDS);
        Runnable canceller = () -> beeperHandle.cancel(false);
        scheduler.schedule(canceller, 1, HOURS);
    }

    public static ScheduledExecutorService getScheduler(){
        return scheduler;
    }

}
